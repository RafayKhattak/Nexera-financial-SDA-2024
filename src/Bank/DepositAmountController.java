package Bank;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class DepositAmountController {

	@FXML
	private Button closeButton;

	@FXML
	private Button depositButton;

	@FXML
	private Label depositMessageLabel;

	@FXML
	private TextField depositAmountField;

	@FXML
	public void depositButtonOnAction(ActionEvent event) {
		String username = getUsernameFromFile("username.txt");
		if (username.isEmpty()) {
			depositMessageLabel.setText("Error: Unable to retrieve username.");
			return;
		}

		String depositAmount = depositAmountField.getText();
		if (!isNumeric(depositAmount)) {
			depositMessageLabel.setText("Invalid deposit amount. Please enter a valid number.");
			return;
		}

		int depositValue = Integer.parseInt(depositAmount);

		if (depositValue <= 0) {
			depositMessageLabel.setText("Deposit amount must be greater than zero.");
			return;
		}

		try (Connection connectDB = new DatabaseConnection().getConnection()) {
			// Fetch current balance
			String balanceQuery = """
                SELECT balance, accNumber 
                FROM BankAccount 
                WHERE custID = (SELECT custID FROM BankCustomer WHERE username = ?)
                """;

			try (PreparedStatement balanceStmt = connectDB.prepareStatement(balanceQuery)) {
				balanceStmt.setString(1, username);
				ResultSet balanceResult = balanceStmt.executeQuery();

				if (balanceResult.next()) {
					int currentBalance = balanceResult.getInt("balance");
					String accountNumber = balanceResult.getString("accNumber");

					// Update balance
					int newBalance = currentBalance + depositValue;
					String updateBalanceQuery = "UPDATE BankAccount SET balance = ? WHERE accNumber = ?";
					try (PreparedStatement updateStmt = connectDB.prepareStatement(updateBalanceQuery)) {
						updateStmt.setInt(1, newBalance);
						updateStmt.setString(2, accountNumber);
						updateStmt.executeUpdate();
					}

					// Log transaction
					String insertTransactionQuery = """
                        INSERT INTO BankTransaction (transactionID, accNumber, transDate, amount, reason) 
                        VALUES ((SELECT COUNT(*) + 1 FROM BankTransaction), ?, NOW(), ?, 'Deposit')
                        """;
					try (PreparedStatement transactionStmt = connectDB.prepareStatement(insertTransactionQuery)) {
						transactionStmt.setString(1, accountNumber);
						transactionStmt.setInt(2, depositValue);
						transactionStmt.executeUpdate();
					}

					depositMessageLabel.setText(String.format("""
                        Deposit Successful!
                        Previous Balance: PKR. %d
                        Deposited Amount: PKR. %d
                        New Balance: PKR. %d
                        """, currentBalance, depositValue, newBalance));
				} else {
					depositMessageLabel.setText("Error: Account not found.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			depositMessageLabel.setText("Error processing deposit. Please try again.");
		}
	}

	@FXML
	public void closeButtonOnAction(ActionEvent event) {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}

	private String getUsernameFromFile(String fileName) {
		try (Scanner scanner = new Scanner(new File(fileName))) {
			if (scanner.hasNextLine()) {
				return scanner.nextLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	private boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
