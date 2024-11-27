package Bank;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			// Load the Login FXML
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
			BorderPane root = loader.load();

			// Set up the stage with UNDECORATED style
			primaryStage.initStyle(StageStyle.UNDECORATED);
			Scene scene = new Scene(root, 750, 600);

			// Add external stylesheets
			String cssFile = getClass().getResource("application.css").toExternalForm();
			scene.getStylesheets().add(cssFile);

			// Set the scene and show the stage
			primaryStage.setScene(scene);
			primaryStage.setTitle("Bank Management System");
			primaryStage.show();
		} catch (Exception e) {
			// Log the error to the console
			System.err.println("Error occurred while starting the application:");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
