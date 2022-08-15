
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
	Model model = new Model();
	Controller controller;

	@Override
	public void start(Stage primaryStage) throws IOException {
		try {
			this.model = WriterReader.readObjectFromFile(new File(System.getProperty("user.dir") + "/data.ser"));
			System.out.println("Data read successfully");
		} catch (Exception e) {
			System.out.println("Not able to read data file");
		}
		this.controller = new Controller(model);

		final FXMLLoader mainView = new FXMLLoader();
		mainView.setLocation(getClass().getResource("/resources/MainView.fxml"));
		mainView.setController(controller);

		GridPane grid = mainView.load();
		Scene scene = new Scene(grid);
		primaryStage.setTitle("Pill Dispenser v1.0");
		primaryStage.getIcons().add(new Image("resources/main_icon.png"));
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.show();
	}

	@Override
	public void stop() {
		try {
			WriterReader.writeObjectToFile(this.model, new File(System.getProperty("user.dir") + "/data.ser"));
			System.out.println("Data saved successfully");
			controller.stopThread();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to save file.");
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
//notes
/*-One User total
 *-6 trays total
 *reset button to initialize all trays(All at once)(user manually types info on screen)
 *Edit button to edit individual medication
 *Add time field to medication to determine when to dispense
 *add dispense count to medication field to determine how much to dispense
 *
 *
 *right side of screen displays medication and stats with edit buttons below
 *left side of screen will show stats such as visual volume of pills,alert when pills are low,log,etc..
 *potential cost of pills and stats?
 *
 *limit only 6 medications
 *No medications named the same
 *implement log*/



