import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
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
import javafx.scene.text.Text;
 
public class Controller implements Initializable{
	private Model model;
    @FXML private Text info;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private BarChart<String, Integer> chart;
    private XYChart.Series<String, Integer> mainSeries = new XYChart.Series<String, Integer>();
    @FXML private ListView<Medication> medicationList;
    ObservableList<Medication> observableList = FXCollections.observableArrayList();

    public Controller(Model model) {
    	this.model=model;
    }
    @FXML protected void resetMedication(ActionEvent event) {
    	model.getUser().getMedicationList().clear();
    	refreshList();
    	refreshChart();
    }
    @FXML protected void editMedication(ActionEvent event) {
    	MedicationDialog medicationDialog=new MedicationDialog(model);
    	medicationDialog.editDialogShow(medicationList);
    	refreshList();
    	refreshChart();
    }
    @FXML protected void addMedication(ActionEvent event) {
    	MedicationDialog medicationDialog=new MedicationDialog(model);
    	medicationDialog.addDialogShow();
    	refreshList();
    	refreshChart();
    }
    @FXML protected void deleteMedication(ActionEvent event) {
    	try {
	    	Medication medication=model.getUser().getMedicationList().get(medicationList.getSelectionModel().getSelectedIndex());
	    	Alert confirm = new Alert(AlertType.CONFIRMATION);
	    	confirm.setTitle("Delete?");
	    	confirm.setContentText("Do you want to remove "+medication.getName()+"?");
	
	    	Optional<ButtonType> result = confirm.showAndWait();
	    	if (result.get() == ButtonType.OK){
	    		model.getUser().getMedicationList().remove(medicationList.getSelectionModel().getSelectedIndex());
	        	refreshList();
	        	refreshChart();
	    	} 
    	} catch(ArrayIndexOutOfBoundsException e) {
    		System.out.println("Nothing is selected");
    	}
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
    	observableList.setAll(model.getUser().getMedicationList());
    	medicationList.setItems(observableList);
    	
    	xAxis.setLabel("Medication Name");
        yAxis.setLabel("Pill Count");
        yAxis.setTickUnit(1);
        yAxis.setAutoRanging(false);
        yAxis.setMinorTickVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.getData().add(mainSeries);
	}
	public void refreshList() {
		observableList.setAll(model.getUser().getMedicationList());
    	medicationList.setItems(observableList);
	}
	
	public void refreshChart() {
		mainSeries.getData().clear();
    	for(Medication med:model.getUser().getMedicationList()) {
        	addToSeries(med.getName(),med.getCount());
        }
	}
	
    @FXML public void handleMouseClick(MouseEvent mouse) {
        info.setText(medicationList.getSelectionModel().getSelectedItem().toStringInfo());
    }
    
    public void addToSeries(String medicationName,int medicationCount){
    	mainSeries.getData().add(new XYChart.Data<String,Integer>(medicationName,medicationCount));
    }
}