package Bank;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.sql.*;
import java.util.Scanner;

public class WithdrawCashController {

	@FXML
	private Button closeButton;

	@FXML
	private Button withdrawButton;

	@FXML
	private Label withdrawMessageLabel;

	@FXML
	private TextField withdrawAmountField;

	@FXML
	public void closeButtonOnAction(ActionEvent e) {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}

	@FXML
	public void withdrawButtonOnAction(ActionEvent e) {
		String username = getUsernameFromFile();
		if (username == null || username.isEmpty()) {
			withdrawMessageLabel.setText("Error: Unable to fetch username.");
			return;
		}

		String amountText = withdrawAmountField.getText();
		if (amountText.isEmpty() || !amountText.matches("\\d+")) {
			withdrawMessageLabel.setText("Please enter a valid amount.");
			return;
		}

		int withdrawAmount = Integer.parseInt(amountText);

		try {
			DatabaseConnection connectNow = new DatabaseConnection();
			Connection connectDB = connectNow.getConnection();

			// Get current balance
			String balanceQuery = "SELECT balance FROM bankaccount WHERE custID = (SELECT custID FROM bankcustomer WHERE username = ?)";
			PreparedStatement balanceStmt = connectDB.prepareStatement(balanceQuery);
			balanceStmt.setString(1, username);
			ResultSet resultSet = balanceStmt.executeQuery();

			if (resultSet.next()) {
				int currentBalance = resultSet.getInt("balance");

				if (currentBalance < withdrawAmount) {
					withdrawMessageLabel.setText("Insufficient balance.");
					return;
				}

				int newBalance = currentBalance - withdrawAmount;

				// Update balance
				String updateBalanceQuery = "UPDATE bankaccount SET balance = ? WHERE custID = (SELECT custID FROM bankcustomer WHERE username = ?)";
				PreparedStatement updateStmt = connectDB.prepareStatement(updateBalanceQuery);
				updateStmt.setInt(1, newBalance);
				updateStmt.setString(2, username);
				updateStmt.executeUpdate();

				// Insert transaction record
				String insertTransactionQuery = "INSERT INTO banktransaction (transactionID, accNumber, transDate, amount, reason) " +
						"VALUES ((SELECT IFNULL(MAX(CAST(transactionID AS UNSIGNED)), 0) + 1 FROM banktransaction), " +
						"(SELECT accNumber FROM bankaccount WHERE custID = (SELECT custID FROM bankcustomer WHERE username = ?)), " +
						"NOW(), ?, 'Withdrawal')";
				PreparedStatement insertStmt = connectDB.prepareStatement(insertTransactionQuery);
				insertStmt.setString(1, username);
				insertStmt.setInt(2, withdrawAmount);
				insertStmt.executeUpdate();

				withdrawMessageLabel.setText("Withdrawal Successful!\nPrevious Balance: " + currentBalance + " PKR\nWithdrawn Amount: " + withdrawAmount + " PKR\nNew Balance: " + newBalance + " PKR");
			} else {
				withdrawMessageLabel.setText("Error: Unable to fetch balance.");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			withdrawMessageLabel.setText("Error: Unable to process withdrawal.");
		}
	}

	private String getUsernameFromFile() {
		try {
			File file = new File("username.txt");
			Scanner scanner = new Scanner(file);
			return scanner.hasNextLine() ? scanner.nextLine() : null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
