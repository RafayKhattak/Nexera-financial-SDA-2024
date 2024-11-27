package Bank;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ManageAccountCustomerController {

	@FXML
	private Button closeButton;

	@FXML
	private Label activeMessageLabel;

	@FXML
	private Button activateButton;

	@FXML
	private Button deactivateButton;

	private String getUsername() {
		String username = "";
		try (Scanner myReader = new Scanner(new File("username.txt"))) {
			if (myReader.hasNextLine()) {
				username = myReader.nextLine();
			}
		} catch (FileNotFoundException e) {
			activeMessageLabel.setText("Error: Unable to read username file.");
			e.printStackTrace();
		}
		return username;
	}

	@FXML
	void activateOnAction(ActionEvent event) {
		String username = getUsername();
		if (username.isEmpty()) {
			return;
		}

		String queryCheck = "SELECT COUNT(*) FROM bankaccount WHERE status = 'Active' AND custid = (SELECT custid FROM bankcustomer WHERE username = ?)";
		String queryActivate = "UPDATE bankaccount SET status = 'Active' WHERE custid = (SELECT custid FROM bankcustomer WHERE username = ?)";

		try (Connection connectDB = new DatabaseConnection().getConnection();
			 PreparedStatement checkStmt = connectDB.prepareStatement(queryCheck);
			 PreparedStatement activateStmt = connectDB.prepareStatement(queryActivate)) {

			checkStmt.setString(1, username);
			try (ResultSet rs = checkStmt.executeQuery()) {
				if (rs.next() && rs.getInt(1) == 1) {
					activeMessageLabel.setText("Account already active.");
					return;
				}
			}

			activateStmt.setString(1, username);
			int rowsAffected = activateStmt.executeUpdate();
			if (rowsAffected > 0) {
				activeMessageLabel.setText("Account activated successfully.");
			} else {
				activeMessageLabel.setText("Failed to activate account.");
			}

		} catch (Exception e) {
			activeMessageLabel.setText("An error occurred during activation.");
			e.printStackTrace();
		}
	}

	@FXML
	void deactivateOnAction(ActionEvent event) {
		String username = getUsername();
		if (username.isEmpty()) {
			return;
		}

		String queryCheck = "SELECT COUNT(*) FROM bankaccount WHERE status = 'DeActive' AND custid = (SELECT custid FROM bankcustomer WHERE username = ?)";
		String queryDeactivate = "UPDATE bankaccount SET status = 'DeActive' WHERE custid = (SELECT custid FROM bankcustomer WHERE username = ?)";

		try (Connection connectDB = new DatabaseConnection().getConnection();
			 PreparedStatement checkStmt = connectDB.prepareStatement(queryCheck);
			 PreparedStatement deactivateStmt = connectDB.prepareStatement(queryDeactivate)) {

			checkStmt.setString(1, username);
			try (ResultSet rs = checkStmt.executeQuery()) {
				if (rs.next() && rs.getInt(1) == 1) {
					activeMessageLabel.setText("Account already deactivated.");
					return;
				}
			}

			deactivateStmt.setString(1, username);
			int rowsAffected = deactivateStmt.executeUpdate();
			if (rowsAffected > 0) {
				activeMessageLabel.setText("Account deactivated successfully.");
			} else {
				activeMessageLabel.setText("Failed to deactivate account.");
			}

		} catch (Exception e) {
			activeMessageLabel.setText("An error occurred during deactivation.");
			e.printStackTrace();
		}
	}

	@FXML
	void closeButtonOnAction(ActionEvent event) {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}
}
