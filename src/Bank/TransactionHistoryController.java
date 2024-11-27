package Bank;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TransactionHistoryController implements Initializable {

    @FXML
    private Button closeButton;

    @FXML
    private TableView<TransactionHistoryTableModel> transHistoryTable;

    @FXML
    private TableColumn<TransactionHistoryTableModel, String> transidCol;

    @FXML
    private TableColumn<TransactionHistoryTableModel, String> accnumCol;

    @FXML
    private TableColumn<TransactionHistoryTableModel, String> amountCol;

    @FXML
    private TableColumn<TransactionHistoryTableModel, String> transDateCol;

    @FXML
    private TableColumn<TransactionHistoryTableModel, String> dueDateCol;

    @FXML
    private TableColumn<TransactionHistoryTableModel, String> reasonCol;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showTransHistory();
    }

    public ObservableList<TransactionHistoryTableModel> getTransactionHistoryList() {
        ObservableList<TransactionHistoryTableModel> list = FXCollections.observableArrayList();
        String username = "";

        try {
            File file = new File("username.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                username = scanner.nextLine();
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String query = "SELECT * FROM BankTransaction WHERE accNumber = (SELECT accNumber FROM BankAccount WHERE custid = (SELECT custid FROM BankCustomer WHERE username = '" + username + "'))";
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        try (Statement statement = connectDB.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                list.add(new TransactionHistoryTableModel(
                        resultSet.getString("transactionID"),
                        resultSet.getString("accNumber"),
                        resultSet.getString("amount"),
                        resultSet.getString("transDate"),
                        resultSet.getString("dueDate"),
                        resultSet.getString("reason")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void showTransHistory() {
        ObservableList<TransactionHistoryTableModel> list = getTransactionHistoryList();

        transidCol.setCellValueFactory(new PropertyValueFactory<>("transidCol"));
        accnumCol.setCellValueFactory(new PropertyValueFactory<>("accnumCol"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amountCol"));
        transDateCol.setCellValueFactory(new PropertyValueFactory<>("transDateCol"));
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDateCol"));
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reasonCol"));


        transHistoryTable.setItems(list);
    }

    @FXML
    void closeButtonOnAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
