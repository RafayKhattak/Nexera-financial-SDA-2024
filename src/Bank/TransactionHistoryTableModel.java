package Bank;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TransactionHistoryTableModel {

	private final StringProperty transidCol;
	private final StringProperty accnumCol;
	private final StringProperty amountCol;
	private final StringProperty transDateCol;
	private final StringProperty dueDateCol;
	private final StringProperty reasonCol;

	// Constructor
	public TransactionHistoryTableModel(String transidCol, String accnumCol, String amountCol, String transDateCol, String dueDateCol, String reasonCol) {
		this.transidCol = new SimpleStringProperty(transidCol);
		this.accnumCol = new SimpleStringProperty(accnumCol);
		this.amountCol = new SimpleStringProperty(amountCol);
		this.transDateCol = new SimpleStringProperty(transDateCol);
		this.dueDateCol = new SimpleStringProperty(dueDateCol);
		this.reasonCol = new SimpleStringProperty(reasonCol);
	}

	// Getters and Setters for Transaction ID
	public String getTransidCol() {
		return transidCol.get();
	}

	public void setTransidCol(String transidCol) {
		this.transidCol.set(transidCol);
	}

	public StringProperty transidColProperty() {
		return transidCol;
	}

	// Getters and Setters for Account Number
	public String getAccnumCol() {
		return accnumCol.get();
	}

	public void setAccnumCol(String accnumCol) {
		this.accnumCol.set(accnumCol);
	}

	public StringProperty accnumColProperty() {
		return accnumCol;
	}

	// Getters and Setters for Amount
	public String getAmountCol() {
		return amountCol.get();
	}

	public void setAmountCol(String amountCol) {
		this.amountCol.set(amountCol);
	}

	public StringProperty amountColProperty() {
		return amountCol;
	}

	// Getters and Setters for Transaction Date
	public String getTransDateCol() {
		return transDateCol.get();
	}

	public void setTransDateCol(String transDateCol) {
		this.transDateCol.set(transDateCol);
	}

	public StringProperty transDateColProperty() {
		return transDateCol;
	}

	// Getters and Setters for Due Date
	public String getDueDateCol() {
		return dueDateCol.get();
	}

	public void setDueDateCol(String dueDateCol) {
		this.dueDateCol.set(dueDateCol);
	}

	public StringProperty dueDateColProperty() {
		return dueDateCol;
	}

	// Getters and Setters for Reason
	public String getReasonCol() {
		return reasonCol.get();
	}

	public void setReasonCol(String reasonCol) {
		this.reasonCol.set(reasonCol);
	}

	public StringProperty reasonColProperty() {
		return reasonCol;
	}
}
