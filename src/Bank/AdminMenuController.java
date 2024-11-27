package Bank;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent; // Import added
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminMenuController implements Initializable {

	@FXML
	private Button manageAccountsButton;

	@FXML
	private Button generateReportsButton;

	@FXML
	private Button manageLoanButton;

	@FXML
	private Button logoutButton;

	@FXML
	private Button closeButton;

	@FXML
	private ImageView adminImg;

	@Override
	public void initialize(URL url, ResourceBundle rs) {
		File adminLogo = new File("src/Bank/adminicon.jpg"); // Ensure this path points to the correct location
		if (adminLogo.exists()) {
			Image adminImage = new Image(adminLogo.toURI().toString());
			adminImg.setImage(adminImage);
		} else {
			System.out.println("Admin icon not found!");
		}
	}


	@FXML
	public void logoutButtonOnAction(ActionEvent e) {
		closeCurrentStage();
		clearUserDataFiles();
		loadFXMLScene("Login.fxml", "Login");
	}

	@FXML
	public void closeButtonOnAction(ActionEvent e) {
		closeCurrentStage();
	}

	@FXML
	public void MLAOnAction(ActionEvent e) {
		loadFXMLScene("MLA.fxml", "Manage Loan Applications");
	}

	@FXML
	public void ManageAccountsAdminOnAction(ActionEvent e) {
		loadFXMLScene("ManageAccountsAdmin.fxml", "Manage Accounts");
	}

	@FXML
	public void GenerateReportOnAction(ActionEvent e) {
		loadFXMLScene("GenerateReports.fxml", "Generate Reports");
	}

	private void closeCurrentStage() {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}

	private void clearUserDataFiles() {
		try {
			FileWriter usernameWriter = new FileWriter("username.txt");
			usernameWriter.close();

			FileWriter typeWriter = new FileWriter("type.txt");
			typeWriter.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void loadFXMLScene(String fxmlFile, String title) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			Parent root = loader.load();
			Stage stage = new Stage();
			stage.initStyle(StageStyle.UNDECORATED);
			Scene scene = new Scene(root, 750, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.setTitle(title);
			stage.show();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
