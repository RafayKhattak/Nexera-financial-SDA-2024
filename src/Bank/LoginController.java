package Bank;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.*;

public class LoginController {

	@FXML
	private Button cancelButton;

	@FXML
	private Button loginButton;

	@FXML
	private Button createNewUserButton;

	@FXML
	private Label loginMessageLabel;

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	/**
	 * Handles the login button action.
	 */
	@FXML
	public void loginButtonOnAction() {
		if (!usernameField.getText().isBlank() && !passwordField.getText().isBlank()) {
			validateLogin();
		} else {
			loginMessageLabel.setText("Please enter username and password!");
		}
	}

	/**
	 * Handles the cancel button action.
	 */
	@FXML
	public void cancelButtonOnAction() {
		Stage stage = (Stage) cancelButton.getScene().getWindow();
		stage.close();
	}

	/**
	 * Handles the register button action.
	 */
	@FXML
	public void registerButtonOnAction() {
		createAccountForm();
	}

	/**
	 * Validates user login credentials.
	 */
	private void validateLogin() {
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();

		String verifyLoginCust = "SELECT COUNT(1) FROM BankCustomer WHERE username = ? AND password = ?";
		String verifyLoginAdm = "SELECT COUNT(1) FROM BankAdmin WHERE username = ? AND password = ?";

		try (PreparedStatement customerStmt = connectDB.prepareStatement(verifyLoginCust);
			 PreparedStatement adminStmt = connectDB.prepareStatement(verifyLoginAdm)) {

			customerStmt.setString(1, usernameField.getText());
			customerStmt.setString(2, passwordField.getText());

			try (ResultSet queryResult = customerStmt.executeQuery()) {
				if (queryResult.next() && queryResult.getInt(1) == 1) {
					loginMessageLabel.setText("Welcome Customer!");
					writeToFile("username.txt", usernameField.getText());
					writeToFile("type.txt", "Customer");
					closeCurrentStage();
					createCustomerMenu();
					return;
				}
			}

			adminStmt.setString(1, usernameField.getText());
			adminStmt.setString(2, passwordField.getText());

			try (ResultSet queryResult = adminStmt.executeQuery()) {
				if (queryResult.next() && queryResult.getInt(1) == 1) {
					loginMessageLabel.setText("Welcome Admin!");
					writeToFile("username.txt", usernameField.getText());
					writeToFile("type.txt", "Admin");
					closeCurrentStage();
					createAdminMenu();
				} else {
					loginMessageLabel.setText("Invalid login. Please try again.");
				}
			}

		} catch (Exception e) {
			loginMessageLabel.setText("An error occurred. Please try again.");
			e.printStackTrace();
		}
	}

	/**
	 * Writes content to a specified file.
	 */
	private void writeToFile(String fileName, String content) {
		try (FileWriter myWriter = new FileWriter(fileName)) {
			myWriter.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the current stage.
	 */
	private void closeCurrentStage() {
		Stage stage = (Stage) loginButton.getScene().getWindow();
		stage.close();
	}

	/**
	 * Opens the account creation form.
	 */
	private void createAccountForm() {
		try {
			AnchorPane register = FXMLLoader.load(getClass().getResource("Register.fxml"));
			Stage registerStage = new Stage();
			registerStage.initStyle(StageStyle.UNDECORATED);
			Scene scene = new Scene(register, 750, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			registerStage.setScene(scene);
			registerStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Opens the customer menu.
	 */
	private void createCustomerMenu() {
		try {
			AnchorPane menu = FXMLLoader.load(getClass().getResource("CustomerMenu.fxml"));
			Stage customerMenuStage = new Stage();
			customerMenuStage.initStyle(StageStyle.UNDECORATED);
			Scene scene = new Scene(menu, 750, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			customerMenuStage.setScene(scene);
			customerMenuStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Opens the admin menu.
	 */
	private void createAdminMenu() {
		try {
			AnchorPane menu = FXMLLoader.load(getClass().getResource("AdminMenu.fxml"));
			Stage adminMenuStage = new Stage();
			adminMenuStage.initStyle(StageStyle.UNDECORATED);
			Scene scene = new Scene(menu, 750, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			adminMenuStage.setScene(scene);
			adminMenuStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
