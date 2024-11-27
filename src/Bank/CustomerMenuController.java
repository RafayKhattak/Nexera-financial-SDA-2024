package Bank;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent; // For Parent
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.io.FileWriter;
import java.io.IOException;

public class CustomerMenuController {

	@FXML
	private Button closeButton;

	@FXML
	private Button logoutButton;

	@FXML
	private Button checkBalanceButton;

	@FXML
	private Button withdrawCashButton;

	@FXML
	private Button depositAmountButton;

	@FXML
	private Button applyForLoanButton;

	@FXML
	private Button checkLoanStatusButton;

	@FXML
	private Button manageAccountButton;

	@FXML
	private Button accountDetailsButton;

	@FXML
	private Button transactionHistoryButton;

	@FXML
	private Button loanPaymentButton;

	@FXML
	private Button loanInfoButton;

	public void logoutButtonOnAction(ActionEvent event) {
		closeCurrentStage(event); // Pass the event parameter
		clearUserDataFiles();
		loadFXMLScene("Login.fxml", "Login");
	}

	public void closeButtonOnAction(ActionEvent event) {
		closeCurrentStage(event); // Pass the event parameter
	}


	@FXML
	public void checkBalanceOnAction(ActionEvent event) {
		loadFXMLScene("CheckBalance.fxml", "Check Balance");
	}

	@FXML
	public void withdrawCashOnAction(ActionEvent event) {
		loadFXMLScene("WithdrawCash.fxml", "Withdraw Cash");
	}

	@FXML
	public void depositAmountOnAction(ActionEvent event) {
		loadFXMLScene("DepositAmount.fxml", "Deposit Amount");
	}

	@FXML
	public void applyLoanOnAction(ActionEvent event) {
		loadFXMLScene("ApplyLoan.fxml", "Apply for Loan");
	}

	@FXML
	public void loanStatusOnAction(ActionEvent event) {
		loadFXMLScene("LoanStatus.fxml", "Loan Status");
	}

	@FXML
	public void loanInformationOnAction(ActionEvent event) {
		loadFXMLScene("LoanInformation.fxml", "Loan Information");
	}

	@FXML
	public void accountDetailsOnAction(ActionEvent event) {
		loadFXMLScene("AccountDetails.fxml", "Account Details");
	}

	@FXML
	public void manageAccountCustomerOnAction(ActionEvent event) {
		loadFXMLScene("ManageAccountCustomer.fxml", "Manage Account");
	}

	@FXML
	public void loanPaymentOnAction(ActionEvent event) {
		loadFXMLScene("LoanPayment.fxml", "Loan Payment");
	}

	@FXML
	public void transactionHistoryOnAction(ActionEvent event) {
		loadFXMLScene("TransactionHistory.fxml", "Transaction History");
	}

	private void closeCurrentStage(ActionEvent event) {
		Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
		stage.close();
	}



	private void clearUserDataFiles() {
		try {
			FileWriter usernameWriter = new FileWriter("username.txt");
			usernameWriter.close();

			FileWriter typeWriter = new FileWriter("type.txt");
			typeWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadFXMLScene(String fxmlFile, String title) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
			Stage stage = new Stage();
			stage.initStyle(StageStyle.UNDECORATED);
			Scene scene = new Scene(root, 750, 600);
			stage.setScene(scene);
			stage.setTitle(title);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
