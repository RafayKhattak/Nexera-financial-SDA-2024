package Bank;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.util.Scanner;


public class AccountDetailsController {

	@FXML
	private Button closeButton;

	@FXML
	private Label infoShow;

	@FXML
	private Button showdetailsButton;

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

		String accountDetailsQuery = """
                SELECT accNumber, balance, creationDate, custID, status 
                FROM BankAccount 
                WHERE custID = (SELECT custID FROM BankCustomer WHERE username = ?)
                """;

		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();

		try (PreparedStatement preparedStatement = connectDB.prepareStatement(accountDetailsQuery)) {
			preparedStatement.setString(1, username);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					String account = resultSet.getString("accNumber");
					String balance = resultSet.getString("balance");
					String creationDate = resultSet.getString("creationDate");
					String custID = resultSet.getString("custID");
					String status = resultSet.getString("status");

					infoShow.setText(String.format("""
                        Account#:\t%s
                        Balance (PKR):\t%s
                        Creation Date:\t%s
                        CustID:\t%s
                        Status:\t%s
                        """, account, balance, creationDate, custID, status));
				} else {
					infoShow.setText("No account details found for the given username.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			infoShow.setText("Error retrieving account details. Please try again.");
		}
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
}
