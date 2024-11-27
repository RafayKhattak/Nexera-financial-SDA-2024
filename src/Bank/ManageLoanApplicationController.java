package Bank;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; // Added import
import java.util.ResourceBundle;

public class ManageLoanApplicationController implements Initializable {

	@FXML
	private Button closeButton;

	@FXML
	private Button acceptButton;

	@FXML
	private Button declineButton;

	@FXML
	private Label manageMessageLabel;

	@FXML
	private TableView<LoanApplicationTableModel> loanApplicationsTable;

	@FXML
	private TableColumn<LoanApplicationTableModel, String> loanidCol;

	@FXML
	private TableColumn<LoanApplicationTableModel, String> accnumCol;

	@FXML
	private TableColumn<LoanApplicationTableModel, String> amountCol;

	@FXML
	private TableColumn<LoanApplicationTableModel, String> applydateCol;

	@FXML
	private TableColumn<LoanApplicationTableModel, String> statusCol;

	@FXML
	private TextField idField;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		showLoanApplications();
	}

	public ObservableList<LoanApplicationTableModel> getLoanApplicationsList() {
		ObservableList<LoanApplicationTableModel> list = FXCollections.observableArrayList();
		String query = "SELECT * FROM BankLoanApplication";

		try (Connection connectDB = new DatabaseConnection().getConnection();
			 PreparedStatement statement = connectDB.prepareStatement(query);
			 ResultSet rs = statement.executeQuery()) {

			while (rs.next()) {
				list.add(new LoanApplicationTableModel(
						rs.getString("loanID"),
						rs.getString("accNumber"),
						rs.getString("amount"),
						rs.getString("applyDate"),
						rs.getString("status")));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public void showLoanApplications() {
		ObservableList<LoanApplicationTableModel> list = getLoanApplicationsList();

		loanidCol.setCellValueFactory(new PropertyValueFactory<>("loanId"));
		accnumCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
		amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
		applydateCol.setCellValueFactory(new PropertyValueFactory<>("applyDate"));
		statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

		loanApplicationsTable.setItems(list);
	}

	@FXML
	private void closeButtonOnAction(ActionEvent event) {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void acceptButtonOnAction(ActionEvent event) {
		String loanID = idField.getText();

		if (loanID.isBlank()) {
			manageMessageLabel.setText("Please enter a LoanID.");
			return;
		}

		String acceptQuery = "UPDATE BankLoanApplication SET status = 'Active' WHERE loanID = ?";
		try (Connection connectDB = new DatabaseConnection().getConnection();
			 PreparedStatement preparedStatement = connectDB.prepareStatement(acceptQuery)) {

			preparedStatement.setString(1, loanID);
			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				manageMessageLabel.setText("Loan Application Accepted.");
				showLoanApplications();
			} else {
				manageMessageLabel.setText("LoanID not found.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			manageMessageLabel.setText("Error while accepting the loan.");
		}
	}

	@FXML
	private void declineButtonOnAction(ActionEvent event) {
		String loanID = idField.getText();

		if (loanID.isBlank()) {
			manageMessageLabel.setText("Please enter a LoanID.");
			return;
		}

		String declineQuery = "UPDATE BankLoanApplication SET status = 'Inactive' WHERE loanID = ?";
		try (Connection connectDB = new DatabaseConnection().getConnection();
			 PreparedStatement preparedStatement = connectDB.prepareStatement(declineQuery)) {

			preparedStatement.setString(1, loanID);
			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				manageMessageLabel.setText("Loan Application Declined.");
				showLoanApplications();
			} else {
				manageMessageLabel.setText("LoanID not found.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			manageMessageLabel.setText("Error while declining the loan.");
		}
	}
}
