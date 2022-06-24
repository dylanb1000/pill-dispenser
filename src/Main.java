
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class Main extends Application{
	@Override
	public void start(Stage primaryStage) throws IOException {
		Model model=new Model();
		Controller controller=new Controller(model);
		
		
		final FXMLLoader mainView = new FXMLLoader();
		mainView.setLocation(getClass().getResource("/resources/MainView.fxml"));
		mainView.setController(controller);
		
		
		GridPane grid=mainView.load();
		Scene scene=new Scene(grid);
		primaryStage.setTitle("Pill Dispenser");
        primaryStage.setScene(scene);
        primaryStage.show();
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



