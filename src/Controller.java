import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Controller implements Initializable {
	private Model model;
	@FXML
	private Text info;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;
	@FXML
	private BarChart<String, Integer> chart;
	private XYChart.Series<String, Integer> mainSeries = new XYChart.Series<String, Integer>();
	@FXML
	private ListView<Medication> medicationList;
	ObservableList<Medication> observableList = FXCollections.observableArrayList();
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
		model.getUser().getMedicationList().clear();
		refreshList();
		refreshChart();
	}

	@FXML
	protected void editMedication(ActionEvent event) {
		MedicationDialog medicationDialog = new MedicationDialog(model);
		try {
			medicationDialog.editDialogShow(medicationList);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Nothing is selected");
		}
		refreshList();
		refreshChart();
	}

	@FXML
	protected void addMedication(ActionEvent event) {
		MedicationDialog medicationDialog = new MedicationDialog(model);
		try {
			medicationDialog.addDialogShow();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Nothing is selected");
		}
		refreshList();
		refreshChart();
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
				refreshList();
				refreshChart();
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Nothing is selected");
		}
	}

	@FXML
	protected void openLog(ActionEvent event) {
		LogDialog logDialog = new LogDialog(model);
		logDialog.showLog();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		observableList.setAll(model.getUser().getMedicationList());
		medicationList.setItems(observableList);
		xAxis.setLabel("Medication Name");
		xAxis.setTickLabelFill(Color.WHITE);
		yAxis.setLabel("Pill Count");
		yAxis.setTickUnit(1);
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
	}

	public void refreshChart() {
		mainSeries.getData().clear();
		for (Medication med : model.getUser().getMedicationList()) {
			addToSeries(med.getName(), med.getCount());
		}
	}

	@FXML
	public void handleMouseClick(MouseEvent mouse) {
		try {
			info.setText(medicationList.getSelectionModel().getSelectedItem().toStringInfo());
		} catch (Exception e) {

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
			System.out.println("Adding Medication to Queue");
			machine.addToMedicationQueue(medication);
		}
	}

	public void stopThread() {
		this.machine.stop();
		this.t1.interrupt();
		;
	}
}