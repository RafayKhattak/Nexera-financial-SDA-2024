package Bank;

import javafx.beans.property.SimpleStringProperty;

public class CustomerTableModel {
	private SimpleStringProperty custID;
	private SimpleStringProperty name;
	private SimpleStringProperty phoneNumber;
	private SimpleStringProperty email;
	private SimpleStringProperty username;
	private SimpleStringProperty password;

	public CustomerTableModel(String custID, String name, String phoneNumber, String email, String username, String password) {
		this.custID = new SimpleStringProperty(custID);
		this.name = new SimpleStringProperty(name);
		this.phoneNumber = new SimpleStringProperty(phoneNumber);
		this.email = new SimpleStringProperty(email);
		this.username = new SimpleStringProperty(username);
		this.password = new SimpleStringProperty(password);
	}

	// Getters and setters
	public String getCustID() { return custID.get(); }
	public void setCustID(String value) { custID.set(value); }

	public String getName() { return name.get(); }
	public void setName(String value) { name.set(value); }

	public String getPhoneNumber() { return phoneNumber.get(); }
	public void setPhoneNumber(String value) { phoneNumber.set(value); }

	public String getEmail() { return email.get(); }
	public void setEmail(String value) { email.set(value); }

	public String getUsername() { return username.get(); }
	public void setUsername(String value) { username.set(value); }

	public String getPassword() { return password.get(); }
	public void setPassword(String value) { password.set(value); }
}
