package Bank;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class CheckBalanceController {

	@FXML
	private Button closeButton;

	@FXML
	private Label balanceShow;

	@FXML
	private Button showBalanceButton;

	@FXML
	public void showBalanceButtonOnAction(ActionEvent event) {
		String username = getUsernameFromFile("username.txt");
		if (username.isEmpty()) {
			balanceShow.setText("Error: Unable to retrieve username.");
			return;
		}

		String balanceQuery = """
                SELECT balance 
                FROM BankAccount 
                WHERE custID = (SELECT custID FROM BankCustomer WHERE username = ?)
                """;

		try (Connection connectDB = new DatabaseConnection().getConnection();
			 PreparedStatement preparedStatement = connectDB.prepareStatement(balanceQuery)) {

			preparedStatement.setString(1, username);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					balanceShow.setText(resultSet.getString("balance"));
				} else {
					balanceShow.setText("No balance information available.");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			balanceShow.setText("Error retrieving balance. Please try again.");
		}
	}

	@FXML
	public void closeButtonOnAction(ActionEvent event) {
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
}
