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

public class LoanStatusController {

	@FXML
	private Button closeButton;

	@FXML
	private Label statusShow;

	@FXML
	private Button showStatusButton;

	@FXML
	void closeButtonOnAction(ActionEvent event) {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}

	@FXML
	void showStatusButtonOnAction(ActionEvent event) {
		String username = getUsernameFromFile("username.txt");
		if (username.isEmpty()) {
			statusShow.setText("*Error: Unable to retrieve username*");
			return;
		}

		String statusQuery = """
            SELECT status 
            FROM BankLoanApplication 
            WHERE accNumber = (SELECT accNumber FROM BankAccount WHERE custID = (SELECT custID FROM BankCustomer WHERE username = ?))
        """;

		try (Connection connectDB = new DatabaseConnection().getConnection();
			 PreparedStatement preparedStatement = connectDB.prepareStatement(statusQuery)) {
			preparedStatement.setString(1, username);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				String status = resultSet.getString("status");
				statusShow.setText("Loan Status: " + status);
			} else {
				statusShow.setText("*No application sent or loan not found*");
			}
		} catch (Exception e) {
			e.printStackTrace();
			statusShow.setText("*Error: Failed to retrieve loan status*");
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
