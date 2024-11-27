package Bank;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class ApplyLoanController {

	@FXML
	private Button closeButton;

	@FXML
	private Button applyButton;

	@FXML
	private TextField amountField;

	@FXML
	private TextField durationField;

	@FXML
	private Label applyMessageLabel;

	@FXML
	void applyButtonOnAction(ActionEvent event) {
		String username = getUsernameFromFile("username.txt");
		if (username.isEmpty()) {
			applyMessageLabel.setText("Error: Unable to retrieve username.");
			return;
		}

		if (checkExistingLoan(username)) {
			applyMessageLabel.setText("Already have a loan in progress.");
			return;
		}

		if (!validateAccountStatus(username)) {
			applyMessageLabel.setText("Account is inactive. Please activate it before applying for a loan.");
			return;
		}

		try {
			applyLoan(username);
			applyMessageLabel.setText("Loan application successfully sent.\nPending approval...");
		} catch (Exception e) {
			e.printStackTrace();
			applyMessageLabel.setText("Error occurred while applying for a loan.");
		}
	}

	@FXML
	void closeButtonOnAction(ActionEvent event) {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}

	private String getUsernameFromFile(String fileName) {
		StringBuilder username = new StringBuilder();
		try (Scanner scanner = new Scanner(new File(fileName))) {
			while (scanner.hasNextLine()) {
				username.append(scanner.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return username.toString();
	}

	private boolean checkExistingLoan(String username) {
		String query = """
                SELECT COUNT(*) 
                FROM BankLoan 
                WHERE accNumber = (SELECT accNumber FROM BankAccount WHERE custID = 
                              (SELECT custID FROM BankCustomer WHERE username = ?))
                """;

		try (Connection connectDB = new DatabaseConnection().getConnection();
			 PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
			preparedStatement.setString(1, username);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next() && resultSet.getInt(1) > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean validateAccountStatus(String username) {
		String query = """
                SELECT status 
                FROM BankAccount 
                WHERE custID = (SELECT custID FROM BankCustomer WHERE username = ?)
                """;

		try (Connection connectDB = new DatabaseConnection().getConnection();
			 PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
			preparedStatement.setString(1, username);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next() && "Active".equals(resultSet.getString("status"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void applyLoan(String username) throws Exception {
		String accountQuery = """
                SELECT accNumber 
                FROM BankAccount 
                WHERE custID = (SELECT custID FROM BankCustomer WHERE username = ?)
                """;

		String insertQuery = """
                INSERT INTO BankLoanApplication (loanID, accNumber, amount, applyDate, status)
                VALUES (?, ?, ?, NOW(), 'Pending')
                """;

		try (Connection connectDB = new DatabaseConnection().getConnection();
			 PreparedStatement accountStmt = connectDB.prepareStatement(accountQuery);
			 PreparedStatement insertStmt = connectDB.prepareStatement(insertQuery)) {

			accountStmt.setString(1, username);

			String accountNumber;
			try (ResultSet resultSet = accountStmt.executeQuery()) {
				if (resultSet.next()) {
					accountNumber = resultSet.getString("accNumber");
				} else {
					throw new Exception("No account found for the user.");
				}
			}

			insertStmt.setString(1, generateLoanID());
			insertStmt.setString(2, accountNumber);
			insertStmt.setInt(3, Integer.parseInt(amountField.getText()));

			insertStmt.executeUpdate();
		}
	}

	private String generateLoanID() {
		String query = "SELECT COUNT(*) FROM BankLoanApplication";
		try (Connection connectDB = new DatabaseConnection().getConnection();
			 PreparedStatement preparedStatement = connectDB.prepareStatement(query);
			 ResultSet resultSet = preparedStatement.executeQuery()) {
			if (resultSet.next()) {
				return String.valueOf(resultSet.getInt(1) + 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "1"; // Default to 1 if unable to generate
	}
}
