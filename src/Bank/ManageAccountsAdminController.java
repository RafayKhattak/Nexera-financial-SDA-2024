package Bank;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory; // Add this import
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;


public class ManageAccountsAdminController implements Initializable {

    @FXML
    private Button closeButton;

    @FXML
    private TableView<CustomerTableModel> CustomerAccountsTable;

    @FXML
    private TableColumn<CustomerTableModel, String> custidCol;

    @FXML
    private TableColumn<CustomerTableModel, String> nameCol;

    @FXML
    private TableColumn<CustomerTableModel, String> phonenumCol;

    @FXML
    private TableColumn<CustomerTableModel, String> emailCol;

    @FXML
    private TableColumn<CustomerTableModel, String> usernameCol;

    @FXML
    private TableColumn<CustomerTableModel, String> passwordCol;

    @FXML
    private TextField idField;

    @FXML
    private Button deleteButton;

    @FXML
    private Label manageMessageLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showCustomers();
    }

    public ObservableList<CustomerTableModel> getCustomerList() {
        ObservableList<CustomerTableModel> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM BankCustomer";

        try (Connection connectDB = new DatabaseConnection().getConnection();
             PreparedStatement statement = connectDB.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                list.add(new CustomerTableModel(
                        rs.getString("custID"),
                        rs.getString("name"),
                        rs.getString("phoneNumber"),  // Corrected column name
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void showCustomers() {
        ObservableList<CustomerTableModel> list = getCustomerList();
        custidCol.setCellValueFactory(new PropertyValueFactory<>("custID"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        phonenumCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));

        CustomerAccountsTable.setItems(list);
    }

    @FXML
    void closeButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void deleteButtonOnAction(ActionEvent event) {
        String custID = idField.getText();
        if (custID.isBlank()) {
            manageMessageLabel.setText("Please enter a valid CustID.");
            return;
        }

        String deleteAccountsQuery = "DELETE FROM BankAccount WHERE custID = ?";
        String deleteCustomerQuery = "DELETE FROM BankCustomer WHERE custID = ?";

        try (Connection connectDB = new DatabaseConnection().getConnection()) {

            // Begin transaction
            connectDB.setAutoCommit(false);

            // Delete associated bank accounts
            try (PreparedStatement deleteAccountsStmt = connectDB.prepareStatement(deleteAccountsQuery)) {
                deleteAccountsStmt.setString(1, custID);
                deleteAccountsStmt.executeUpdate();
            }

            // Delete customer
            try (PreparedStatement deleteCustomerStmt = connectDB.prepareStatement(deleteCustomerQuery)) {
                deleteCustomerStmt.setString(1, custID);
                int rowsAffected = deleteCustomerStmt.executeUpdate();

                if (rowsAffected > 0) {
                    manageMessageLabel.setText("Customer and associated accounts deleted successfully.");
                    connectDB.commit(); // Commit transaction
                    showCustomers(); // Refresh the table
                } else {
                    manageMessageLabel.setText("Customer not found.");
                    connectDB.rollback(); // Rollback transaction
                }
            }

        } catch (Exception e) {
            manageMessageLabel.setText("An error occurred while deleting the customer.");
            e.printStackTrace();
        }
    }

}
