package Bank;

import javafx.beans.property.SimpleStringProperty;

public class LoanApplicationTableModel {

	private SimpleStringProperty loanId;
	private SimpleStringProperty accountNumber;
	private SimpleStringProperty amount;
	private SimpleStringProperty applyDate;
	private SimpleStringProperty status;

	// Constructor
	public LoanApplicationTableModel(String loanId, String accountNumber, String amount, String applyDate, String status) {
		this.loanId = new SimpleStringProperty(loanId);
		this.accountNumber = new SimpleStringProperty(accountNumber);
		this.amount = new SimpleStringProperty(amount);
		this.applyDate = new SimpleStringProperty(applyDate);
		this.status = new SimpleStringProperty(status);
	}

	// Getter and Setter for loanId
	public String getLoanId() {
		return loanId.get();
	}

	public void setLoanId(String loanId) {
		this.loanId.set(loanId);
	}

	// Getter and Setter for accountNumber
	public String getAccountNumber() {
		return accountNumber.get();
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber.set(accountNumber);
	}

	// Getter and Setter for amount
	public String getAmount() {
		return amount.get();
	}

	public void setAmount(String amount) {
		this.amount.set(amount);
	}

	// Getter and Setter for applyDate
	public String getApplyDate() {
		return applyDate.get();
	}

	public void setApplyDate(String applyDate) {
		this.applyDate.set(applyDate);
	}

	// Getter and Setter for status
	public String getStatus() {
		return status.get();
	}

	public void setStatus(String status) {
		this.status.set(status);
	}

	// Property methods (if needed)
	public SimpleStringProperty loanIdProperty() {
		return loanId;
	}

	public SimpleStringProperty accountNumberProperty() {
		return accountNumber;
	}

	public SimpleStringProperty amountProperty() {
		return amount;
	}

	public SimpleStringProperty applyDateProperty() {
		return applyDate;
	}

	public SimpleStringProperty statusProperty() {
		return status;
	}


// Override toString() for debugging and logging
	@Override
	public String toString() {
		return "LoanApplicationTableModel{" +
				"loanId='" + loanId + '\'' +
				", accountNumber='" + accountNumber + '\'' +
				", amount='" + amount + '\'' +
				", applyDate='" + applyDate + '\'' +
				", status='" + status + '\'' +
				'}';
	}
}
