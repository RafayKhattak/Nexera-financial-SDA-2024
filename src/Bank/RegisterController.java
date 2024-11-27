package Bank;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
import java.util.UUID;

public class RegisterController {

	@FXML
	private AnchorPane registerAccType;

	@FXML
	private TextField registerName;

	@FXML
	private TextField registerBalance;

	@FXML
	private TextField registerPhoneNumber;

	@FXML
	private TextField registerEmail;

	@FXML
	private PasswordField registerPassword;

	@FXML
	private PasswordField registerRePassword;

	@FXML
	private TextField registerUsername;

	@FXML
	private TextField registerType;

	@FXML
	private Button registerRegisterButton;

	@FXML
	private Button registerCloseButton;

	@FXML
	private Label registerMessageLabel;

	@FXML
	private Label registerPasswordMessageLabel;

	@FXML
	public void closeButtonOnAction(ActionEvent event) {
		// Close the current stage
		Stage stage = (Stage) registerCloseButton.getScene().getWindow();
		stage.close();
	}


	@FXML
	public void registerButtonOnAction(ActionEvent event) {
		// Validate passwords match
		if (!registerPassword.getText().equals(registerRePassword.getText())) {
			registerPasswordMessageLabel.setText("Passwords do not match!");
			return;
		}

		// Proceed with user registration
		registerUser();
	}

	private void registerUser() {
		// Collect input from fields
		String name = registerName.getText();
		String username = registerUsername.getText();
		String password = registerPassword.getText();
		String email = registerEmail.getText();
		String phoneNumber = registerPhoneNumber.getText();
		String type = registerType.getText();
		String balanceText = registerBalance.getText();

		// Validate input fields
		if (name.isBlank() || username.isBlank() || password.isBlank() || email.isBlank() || phoneNumber.isBlank() || type.isBlank() || balanceText.isBlank()) {
			registerMessageLabel.setText("Please fill out all fields.");
			return;
		}

		// Validate account type
		if (!type.equalsIgnoreCase("Admin") && !type.equalsIgnoreCase("Customer")) {
			registerMessageLabel.setText("Type must be 'Admin' or 'Customer'.");
			return;
		}

		try {
			// Parse balance
			int balance = Integer.parseInt(balanceText);

			// Generate unique IDs
			String custID = UUID.randomUUID().toString().substring(0, 10);

			// Determine target table
			String tableName = type.equalsIgnoreCase("Admin") ? "BankAdmin" : "BankCustomer";
			DatabaseConnection connectNow = new DatabaseConnection();
			Connection connectDB = connectNow.getConnection();

			// Insert user data into the appropriate table
			String insertQuery = "INSERT INTO " + tableName + " (custID, name, phoneNumber, email, username, password) VALUES (?, ?, ?, ?, ?, ?)";
			try (PreparedStatement preparedStatement = connectDB.prepareStatement(insertQuery)) {
				preparedStatement.setString(1, custID);
				preparedStatement.setString(2, name);
				preparedStatement.setString(3, phoneNumber);
				preparedStatement.setString(4, email);
				preparedStatement.setString(5, username);
				preparedStatement.setString(6, password);
				preparedStatement.executeUpdate();
			}

			// If customer, create a new bank account
			if (type.equalsIgnoreCase("Customer")) {
				String accNumber = UUID.randomUUID().toString().substring(0, 12);
				String accountQuery = "INSERT INTO BankAccount (accNumber, balance, creationDate, custID, status) VALUES (?, ?, NOW(), ?, 'Active')";
				try (PreparedStatement accountStatement = connectDB.prepareStatement(accountQuery)) {
					accountStatement.setString(1, accNumber);
					accountStatement.setInt(2, balance);
					accountStatement.setString(3, custID);
					accountStatement.executeUpdate();
				}
			}

			// Success message
			registerMessageLabel.setText("User registered successfully!");
		} catch (NumberFormatException e) {
			registerMessageLabel.setText("Balance must be a valid number.");
		} catch (Exception e) {
			registerMessageLabel.setText("Error during registration. Please try again.");
			e.printStackTrace();
		}
	}

}
