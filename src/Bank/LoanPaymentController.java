package Bank;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class LoanPaymentController {

	@FXML
	private Button closeButton;

	@FXML
	private Label paymentMessageLabel;

	@FXML
	private Button paymentButton;

	@FXML
	void closeButtonOnAction(ActionEvent event) {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}

	@FXML
	void paymentOnAction(ActionEvent event) {
		String username = getUsernameFromFile("username.txt");
		if (username.isEmpty()) {
			paymentMessageLabel.setText("Error: Unable to retrieve username.");
			return;
		}

		System.out.println("Username retrieved: " + username);

		try (Connection connectDB = new DatabaseConnection().getConnection()) {
			// Step 1: Fetch loan amount
			String loanAmountQuery = """
            SELECT SUM(amount) FROM BankLoan WHERE status = 'Active' AND accNumber IN (
                SELECT accNumber FROM BankAccount 
                WHERE custID = (
                    SELECT custID FROM BankCustomer WHERE username = ?
                )
            )
        """;
			int loanAmount = fetchSingleInt(connectDB, loanAmountQuery, username);
			System.out.println("Loan Amount: " + loanAmount);
			if (loanAmount == -1) {
				paymentMessageLabel.setText("Error: No loan found for payment.");
				return;
			}

			// Step 2: Fetch account balance
			String balanceQuery = """
            SELECT balance FROM BankAccount 
            WHERE custID = (SELECT custID FROM BankCustomer WHERE username = ?)
        """;
			int balance = fetchSingleInt(connectDB, balanceQuery, username);
			System.out.println("Current Balance: " + balance);

			// Step 3: Check if balance is sufficient
			if (balance < loanAmount) {
				paymentMessageLabel.setText("Error: Insufficient funds for loan payment.");
				return;
			}

			int newBalance = balance - loanAmount;

			// Step 4: Begin transaction
			connectDB.setAutoCommit(false);

			try {
				// Step 5: Update account balance
				String updateBalanceQuery = """
                UPDATE BankAccount 
                SET balance = ? 
                WHERE custID = (SELECT custID FROM BankCustomer WHERE username = ?)
            """;
				executeUpdate(connectDB, updateBalanceQuery, newBalance, username);

				// Step 6: Delete loan and loan application records
				String deleteLoanQuery = """
                DELETE FROM BankLoan 
                WHERE accNumber IN (
                    SELECT accNumber FROM BankAccount 
                    WHERE custID = (SELECT custID FROM BankCustomer WHERE username = ?)
                )
            """;
				String deleteLoanApplicationQuery = """
                DELETE FROM BankLoanApplication 
                WHERE accNumber IN (
                    SELECT accNumber FROM BankAccount 
                    WHERE custID = (SELECT custID FROM BankCustomer WHERE username = ?)
                )
            """;
				executeUpdate(connectDB, deleteLoanQuery, username);
				executeUpdate(connectDB, deleteLoanApplicationQuery, username);

				// Commit transaction
				connectDB.commit();

				paymentMessageLabel.setText("Payment successful!\nYour loan has been fully paid.\nYou can now apply for new loans.");
			} catch (Exception e) {
				connectDB.rollback(); // Rollback in case of error
				e.printStackTrace();
				paymentMessageLabel.setText("Error: Failed to complete the payment.");
			} finally {
				connectDB.setAutoCommit(true); // Reset autocommit
			}

		} catch (Exception e) {
			e.printStackTrace();
			paymentMessageLabel.setText("Error: Failed to complete the payment.");
		}
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

	private int fetchSingleInt(Connection connection, String query, String param) {
		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, param);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // Indicates an error or no result
	}

	private void executeUpdate(Connection connection, String query, Object... params) {
		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			for (int i = 0; i < params.length; i++) {
				if (params[i] instanceof Integer) {
					preparedStatement.setInt(i + 1, (Integer) params[i]);
				} else if (params[i] instanceof String) {
					preparedStatement.setString(i + 1, (String) params[i]);
				}
			}
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
