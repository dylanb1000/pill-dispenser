import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import javafx.util.Pair;

public class Controller implements Initializable {
	private Model model;
	@FXML
	private Text info;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;
	@FXML 
	private TabPane tabPane;
	@FXML 
	private Tab MainPage;
	@FXML
	private Tab Inventory;
	@FXML
	private Tab Log;
	@FXML
	private ListView<String> logList;
	@FXML
	private BarChart<String, Integer> chart;
	@FXML 
	private MenuBar menuBar;
	@FXML
	private Menu fileMenu;
	@FXML
	private Menu themeMenu;
	private XYChart.Series<String, Integer> mainSeries = new XYChart.Series<String, Integer>();
	@FXML
	private ListView<Medication> medicationList;
	ObservableList<Medication> observableList = FXCollections.observableArrayList();
	ObservableList<String> observableListLog = FXCollections.observableArrayList();
	//Default theme
	String styleSheet = "darkTheme.css";
	private Machine machine;
	private Thread t1;

	public Controller(Model model) {
		this.model = model;
		this.machine = new Machine(this.model);
		Thread t1 = new Thread(this.machine);
		t1.start();
	}

	@FXML
	protected void resetMedication(ActionEvent event) {
		try {
			Alert confirm = new Alert(AlertType.CONFIRMATION);
			confirm.setTitle("Delete?");
			confirm.setContentText("Do you want to reset medication list?");

			Optional<ButtonType> result = confirm.showAndWait();
			if (result.get() == ButtonType.OK) {
				model.getUser().getMedicationList().clear();
				refreshProgram();
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Nothing is selected");
		}
	}

	@FXML
	protected void editMedication(ActionEvent event) {
		MedicationDialog medicationDialog = new MedicationDialog(model,styleSheet);
		try {
			medicationDialog.editDialogShow(medicationList);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Nothing is selected");
		}
		refreshProgram();
	}

	@FXML
	protected void addMedication(ActionEvent event) {
		MedicationDialog medicationDialog = new MedicationDialog(model,styleSheet);
		try {
			medicationDialog.addDialogShow();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Nothing is selected");
		}
		refreshProgram();
	}

	@FXML
	protected void deleteMedication(ActionEvent event) {
		try {
			Medication medication = model.getUser().getMedicationList()
					.get(medicationList.getSelectionModel().getSelectedIndex());
			Alert confirm = new Alert(AlertType.CONFIRMATION);
			confirm.setTitle("Delete?");
			confirm.setContentText("Do you want to remove " + medication.getName() + "?");

			Optional<ButtonType> result = confirm.showAndWait();
			if (result.get() == ButtonType.OK) {
				model.getUser().getMedicationList().remove(medicationList.getSelectionModel().getSelectedIndex());
				refreshProgram();
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Nothing is selected");
		}
	}
	
	@FXML
	public void clearLog() {
		model.getUser().getLog().getLog().clear();
		refreshProgram();
	}
	
	@FXML
	public void exportLog() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Documents", "*.txt"));
		File selectedFile = fileChooser.showSaveDialog(null);
		if (selectedFile != null) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));
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
	
	@FXML 
	protected void exit() {
		Platform.exit();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		observableList.setAll(model.getUser().getMedicationList());
		medicationList.setItems(observableList);
		
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		List<String> logInfo = new ArrayList<String>();
		for (Pair<String, String> info : model.getUser().getLog().getLog()) {
			logInfo.add(info.getKey() + " : " + info.getValue());
		}
		observableListLog.setAll(logInfo);
		logList.setItems(observableListLog);

		for(MenuItem item:themeMenu.getItems()) {
			item.setOnAction(new EventHandler<ActionEvent>() {
			    @Override
			    public void handle(ActionEvent e) {
		    	Stage stage = (Stage) menuBar.getScene().getWindow();
			    try {
			    	stage.getScene().getStylesheets().remove(stage.getScene().getStylesheets().get(0));
			    }
			    catch(Exception err){
			    	
			    }
			    stage.getScene().getStylesheets().add("resources/"+item.getId()+".css");
			    System.out.println(item.getId()+".css");
			     }
			    });
		}
		
		xAxis.setLabel("Medication Name");
		xAxis.setTickLabelFill(Color.WHITE);
		yAxis.setLabel("Pill Count");
		yAxis.setTickUnit(10);
		yAxis.setAutoRanging(false);
		yAxis.setMinorTickVisible(false);
		yAxis.setTickLabelFill(Color.WHITE);
		chart.setLegendVisible(false);
		chart.setHorizontalGridLinesVisible(false);
		chart.setVerticalGridLinesVisible(false);
		chart.setAnimated(false);
		chart.getData().add(mainSeries);
		for (Medication med : model.getUser().getMedicationList()) {
			addToSeries(med.getName(), med.getCount());
		}

		final Timeline timeline = new Timeline(new KeyFrame(Duration.millis(60000), event -> {
			for (Medication medication : model.getUser().getMedicationList()) {
				System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
				checkToDispense(medication, machine);
			}
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();

	}

	public void refreshList() {
		observableList.setAll(model.getUser().getMedicationList());
		medicationList.setItems(observableList);
		try {
			info.setText(medicationList.getSelectionModel().getSelectedItem().toStringInfo());
		} catch (Exception e) {
			info.setText("");
		}
	}
	public void refreshLog() {
		List<String> logInfo = new ArrayList<String>();
		for (Pair<String, String> info : model.getUser().getLog().getLog()) {
			logInfo.add(info.getKey() + " : " + info.getValue());
		}
		observableListLog.setAll(logInfo);
		logList.setItems(observableListLog);
	}
	public void refreshChart() {
		mainSeries.getData().clear();
		for (Medication med : model.getUser().getMedicationList()) {
			addToSeries(med.getName(), med.getCount());
		}
	}
	public void refreshProgram() {
		refreshList();
		refreshChart();
		refreshLog();
	}

	@FXML
	public void handleMouseClick(MouseEvent mouse) {
		try {
			info.setText(medicationList.getSelectionModel().getSelectedItem().toStringInfo());
		} catch (Exception e) {
			info.setText("");
		}
	}

	public void addToSeries(String medicationName, int medicationCount) {
		mainSeries.getData().add(new XYChart.Data<String, Integer>(medicationName, medicationCount));
	}

	public void checkToDispense(Medication medication, Machine machine) {
		int hourNow = LocalDateTime.now().getHour();
		int minuteNow = LocalDateTime.now().getMinute();
		if (medication.getDispenseTime().getHour() == hourNow
				&& medication.getDispenseTime().getMinute() == minuteNow) {
			for(int i=0;i<medication.getDispenseRate();i++) {
				if(medication.getDispenseRate()>medication.getCount()) {
					Alert alert = new Alert(AlertType.WARNING);
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					stage.setAlwaysOnTop(true);
					alert.getDialogPane().getStylesheets().add("resources/other.css");
					alert.setTitle("Warning Dialog");
					alert.setHeaderText(medication.getName()+" is empty. Please refill");
					String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
					model.getUser().getLog().addEntry("Failed: "+medication.getName(), time);
					alert.show();
					refreshProgram();
					break;
				}
				else {
					System.out.println("Adding "+medication.getName()+" to Queue");
					machine.addToMedicationQueue(medication);
				}
			}
		}
	}
	
	public void stopThread() {
		this.machine.stop();
		this.t1.interrupt();
		;
	}
}