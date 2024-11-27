package Bank;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GenerateReportsController {

	@FXML
	private Button closeButton;

	@FXML
	private Label promptMessageLabel;

	@FXML
	private Button allTransactionsButton;

	@FXML
	void allTransactionsButtonOnAction(ActionEvent event) {
		String reportFilePath = "report.txt";

		try (FileWriter myWriter = new FileWriter(reportFilePath)) {
			try (Connection connectDB = new DatabaseConnection().getConnection()) {
				String query = "SELECT * FROM BankTransaction";

				try (PreparedStatement preparedStatement = connectDB.prepareStatement(query);
					 ResultSet rs = preparedStatement.executeQuery()) {

					// Writing headers to the report file
					myWriter.write("TransactionID\tAccountNumber\tTransactionDate\tDueDate\tAmount\tReason\n");

					// Writing transaction details to the report file
					while (rs.next()) {
						String transactionID = rs.getString("transactionID");
						String accountNumber = rs.getString("accNumber");
						String transactionDate = rs.getString("transDate");
						String dueDate = rs.getString("dueDate") != null ? rs.getString("dueDate") : "N/A";
						String amount = rs.getString("amount");
						String reason = rs.getString("reason");

						myWriter.write(transactionID + "\t\t" + accountNumber + "\t\t" + transactionDate + "\t" +
								dueDate + "\t" + amount + "\t\t" + reason + "\n");
					}

					promptMessageLabel.setText("Report successfully generated in 'report.txt'");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			promptMessageLabel.setText("Error: Unable to write to report file.");
		} catch (Exception e) {
			e.printStackTrace();
			promptMessageLabel.setText("Error: Failed to generate report. Please try again.");
		}
	}

	@FXML
	void closeButtonOnAction(ActionEvent event) {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}
}
