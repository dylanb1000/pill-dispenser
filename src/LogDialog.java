import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class LogDialog extends Alert{
	
	private Model model;
	ObservableList<String> observableList = FXCollections.observableArrayList();

	public LogDialog(Model model) {
		super(AlertType.INFORMATION);
		this.model=model;
		this.setTitle("Log");
		ListView<String> logList=new ListView<String>();
		List<String> logInfo=new ArrayList<String>();
		try {
			for(Pair<String,String> info:model.getUser().getLog().getLog()) {
				logInfo.add(info.getKey()+" : "+info.getValue());
			}
			observableList.setAll(logInfo);
		} catch(NullPointerException e) {
			System.out.println("Nothing in log.");
		}
				
		logList.setItems(observableList);
		logList.setItems(observableList);
		DialogPane dialog=this.getDialogPane();
		GridPane grid=new GridPane();
		grid.add(logList, 0, 0);
		dialog.setContent(grid);
		dialog.getButtonTypes().add(ButtonType.CANCEL);
		dialog.setHeaderText(null);
		dialog.setGraphic(null);
	}
}
