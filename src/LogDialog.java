import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Pair;

public class LogDialog extends Alert {

	private Model model;
	private ButtonType clear;
	private ButtonType export;
	ObservableList<String> observableList = FXCollections.observableArrayList();

	public LogDialog(Model model) {
		super(AlertType.INFORMATION);
		this.model = model;
		this.setTitle("Log");
		ListView<String> logList = new ListView<String>();
		List<String> logInfo = new ArrayList<String>();
		try {
			for (Pair<String, String> info : model.getUser().getLog().getLog()) {
				logInfo.add(info.getKey() + " : " + info.getValue());
			}
			observableList.setAll(logInfo);
		} catch (NullPointerException e) {
			System.out.println("Nothing in log.");
		}

		logList.setItems(observableList);
		DialogPane dialog = this.getDialogPane();
		GridPane grid = new GridPane();
		grid.add(logList, 0, 0);
		dialog.setContent(grid);
		clear = new ButtonType("Clear", ButtonBar.ButtonData.BACK_PREVIOUS);
		export = new ButtonType("Export", ButtonBar.ButtonData.OK_DONE);
		dialog.getButtonTypes().remove(ButtonType.OK);
		dialog.getButtonTypes().add(export);
		dialog.getButtonTypes().add(clear);
		dialog.getButtonTypes().add(ButtonType.CANCEL);
		dialog.setHeaderText(null);
		dialog.setGraphic(null);
		Stage stage = (Stage) dialog.getScene().getWindow();
		stage.getIcons().add(new Image(this.getClass().getResource("/resources/log_icon.png").toString()));
	}

	public void showLog() {
		this.showAndWait().ifPresent(response -> {
			if (response == clear) {
				model.getUser().getLog().getLog().clear();
			} else if (response == export) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Documents", "*.txt"));
				File selectedFile = fileChooser.showSaveDialog(this.getOwner());
				if (selectedFile != null) {
					LogToText(selectedFile);
				}
				// TODO export log data as XLS
			}
		});
	}

	public void LogToText(File file) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for (Pair<String, String> info : model.getUser().getLog().getLog()) {
				writer.write(info.getKey() + " : " + info.getValue() + "\n");
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
