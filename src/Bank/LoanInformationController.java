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

public class LoanInformationController {

	@FXML
	private Button closeButton;

	@FXML
	private Label infoShow;

	@FXML
	private Button showStatusButton;

	@FXML
	void closeButtonOnAction(ActionEvent event) {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}

	@FXML
	void showInfoOnAction(ActionEvent event) {
		String username = getUsernameFromFile("username.txt");

		if (username.isEmpty()) {
			infoShow.setText("Error: Unable to retrieve username.");
			return;
		}

		System.out.println("Username retrieved: " + username);

		String loanCheckQuery = """
            SELECT COUNT(*) 
            FROM BankLoan 
            WHERE accNumber IN (
                SELECT accNumber FROM BankAccount 
                WHERE custID = (
                    SELECT custID FROM BankCustomer WHERE username = ?
                )
            )
        """;

		String loanInfoQuery = """
            SELECT loanID, accNumber, amount, interestRate, acceptDate, endDate 
            FROM BankLoan 
            WHERE accNumber IN (
                SELECT accNumber FROM BankAccount 
                WHERE custID = (
                    SELECT custID FROM BankCustomer WHERE username = ?
                )
            )
        """;

		try (Connection connectDB = new DatabaseConnection().getConnection()) {
			// Check if any loans exist for the user
			try (PreparedStatement loanCheckStmt = connectDB.prepareStatement(loanCheckQuery)) {
				loanCheckStmt.setString(1, username);
				ResultSet loanCheckResult = loanCheckStmt.executeQuery();

				if (loanCheckResult.next() && loanCheckResult.getInt(1) == 0) {
					infoShow.setText("No granted loan application.");
					return;
				}
			}

			// Fetch loan details
			try (PreparedStatement loanInfoStmt = connectDB.prepareStatement(loanInfoQuery)) {
				loanInfoStmt.setString(1, username);
				ResultSet loanInfoResult = loanInfoStmt.executeQuery();

				StringBuilder loanDetails = new StringBuilder();
				while (loanInfoResult.next()) {
					loanDetails.append("Loan ID: ").append(loanInfoResult.getString("loanID")).append("\n")
							.append("Account #: ").append(loanInfoResult.getString("accNumber")).append("\n")
							.append("Amount (PKR): ").append(loanInfoResult.getString("amount")).append("\n")
							.append("Interest Rate (%): ").append(loanInfoResult.getString("interestRate")).append("\n")
							.append("Accepted Date: ").append(loanInfoResult.getString("acceptDate")).append("\n")
							.append("End Date: ").append(loanInfoResult.getString("endDate")).append("\n\n");
				}

				if (loanDetails.length() == 0) {
					infoShow.setText("No granted loan application.");
				} else {
					infoShow.setText(loanDetails.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			infoShow.setText("Error retrieving loan information. Please try again.");
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
}
