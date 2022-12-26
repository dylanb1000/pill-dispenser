import java.time.LocalTime;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MedicationDialog extends Alert {
	TextField name = new TextField();
	TextField count = new TextField();
	TextField slot = new TextField();
	TextField hour = new TextField();
	TextField min = new TextField();
	//ComboBox<Integer> hour = new ComboBox<Integer>();
	//ComboBox<Integer> min = new ComboBox<Integer>();
	TextField rate = new TextField();
	Model model;

	public MedicationDialog(Model model) {
		super(Alert.AlertType.NONE);
		this.model = model;
		this.getDialogPane().getStylesheets().add("resources/other.css");
		count.getProperties().put("vkType", "numeric");
		slot.getProperties().put("vkType", "numeric");
		rate.getProperties().put("vkType", "numeric");
		hour.getProperties().put("vkType", "numeric");
		min.getProperties().put("vkType", "numeric");
		DialogPane dialog = this.getDialogPane();
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		Label nameLabel = new Label("Medication Name");
		grid.add(nameLabel, 0, 0);
		grid.add(name, 1, 0);
		Label countLabel = new Label("Number of Pills");
		grid.add(countLabel, 0, 1);
		grid.add(count, 1, 1);
		Label slotLabel = new Label("Slot Number");
		grid.add(slotLabel, 0, 2);
		grid.add(slot, 1, 2);
		Label timeLabel = new Label("Dispense Time");
		Label hourLabel = new Label("H: ");
		Label minLabel = new Label("M: ");
		HBox timeBox = new HBox(hourLabel, hour, minLabel, min);
		grid.add(timeLabel, 0, 3);
		grid.add(timeBox, 1, 3);
		Label rateLabel = new Label("Dispense Rate");
		grid.add(rateLabel, 0, 4);
		grid.add(rate, 1, 4);
		dialog.setContent(grid);
		dialog.getButtonTypes().add(ButtonType.CANCEL);
		dialog.getButtonTypes().add(ButtonType.OK);
		Stage stage = (Stage) dialog.getScene().getWindow();
		stage.getIcons().add(new Image(this.getClass().getResource("/resources/main_icon.png").toString()));
		stage.setAlwaysOnTop(true);
		//removes focus on text elements when dialogpane is clicked
		dialog.setOnMousePressed(event -> {
	        if (!TextField.class.equals(event.getSource())) {
	                  	dialog.requestFocus();
	        }
	});
	}

	public void addDialogShow() {
		this.setTitle("Add Medication");
		this.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.getDialogPane().getStylesheets().add("resources/other.css");
				alert.setTitle("Warning Dialog");
				alert.setHeaderText("Incorrect Information!");
				try {
					if (checkSlotRange(Integer.parseInt(slot.getText())) == false) {
						if (checkName(name.getText()) == false) {
							if (checkSlot(Integer.parseInt(slot.getText())) == false) {
								if(checkTimeRange(Integer.parseInt(hour.getText()),Integer.parseInt(min.getText())) == true) {
								String name = this.name.getText();
								int count = Integer.parseInt(this.count.getText());
								int slotnumber = Integer.parseInt(slot.getText());
								LocalTime time = LocalTime.of(Integer.parseInt(hour.getText()),Integer.parseInt(min.getText()));
								int dispenseRate = Integer.parseInt(rate.getText());
								model.getUser().getMedicationList()
										.add(new Medication(name, count, slotnumber, time, dispenseRate));
								}
								else {
									alert.setContentText("Time is out of range.");
									alert.showAndWait();
								}
							} else {
								alert.setContentText("Slot number is already occupied.");
								alert.showAndWait();
							}
						} else {
							alert.setContentText("Medication cannot have the same name as another medication.");
							alert.showAndWait();
						}
					} else {
						alert.setContentText("Slot number must be between 1-6.");
						alert.showAndWait();
					}
				} catch (NumberFormatException e) {
					alert.setContentText("Count, Slot Number and Dispense rate must be a  whole number.");
					alert.showAndWait();
				}

			} else if (response == ButtonType.CANCEL) {
				this.close();
			}
		});
	}

	public void editDialogShow(ListView<Medication> medicationList) {
		this.setTitle("Edit Medication");
		int selectedIndex = medicationList.getSelectionModel().getSelectedIndex();
		Medication med = model.getUser().getMedicationList().get(selectedIndex);
		this.name.setText(med.getName());
		this.count.setText(String.valueOf(med.getCount()));
		this.slot.setText(String.valueOf(med.getSlotNumber()));
		this.hour.setText(String.valueOf(med.getDispenseTime().getHour()));
		this.min.setText(String.valueOf(med.getDispenseTime().getMinute()));
		this.rate.setText(String.valueOf(med.getDispenseRate()));
		// TODO catch ArrayIndexOutOfBoundsException when nothing is selected to edit
		this.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.getDialogPane().getStylesheets().add("resources/other.css");
				alert.setTitle("Warning Dialog");
				alert.setHeaderText("Incorrect Information!");
				try {
					if (checkSlotRange(Integer.parseInt(slot.getText())) == false) {
						if (checkName(name.getText()) == false || med.getName().equals(name.getText())) {
							if (checkSlot(Integer.parseInt(slot.getText())) == false
									|| med.getSlotNumber() == Integer.parseInt(slot.getText())) {
								if(checkTimeRange(Integer.parseInt(hour.getText()),Integer.parseInt(min.getText())) == true) {
								String name = this.name.getText();
								int count = Integer.parseInt(this.count.getText());
								int slotnumber = Integer.parseInt(slot.getText());
								LocalTime time = LocalTime.of(Integer.parseInt(hour.getText()),Integer.parseInt(min.getText()));
								int dispenseRate = Integer.parseInt(rate.getText());
								model.getUser().getMedicationList().set(selectedIndex,
										new Medication(name, count, slotnumber, time, dispenseRate));
								}
								else {
									alert.setContentText("Time is out of range.");
									alert.showAndWait();
								}
							} else {
								alert.setContentText("Slot number is already occupied.");
								alert.showAndWait();
							}
						} else {
							alert.setContentText("Medication cannot have the same name as another medication.");
							alert.showAndWait();
						}
					} else {
						alert.setContentText("Slot number must be between 1-6.");
						alert.showAndWait();
					}
				} catch (NumberFormatException e) {
					alert.setContentText("Count, Slot Number and Dispense rate must be a whole number.");
					alert.showAndWait();
				}

			} else if (response == ButtonType.CANCEL) {
				this.close();
			}
		});

	}

	public boolean checkSlot(int slotNumber) {
		List<Medication> medicationList = model.getUser().getMedicationList();
		for (Medication med : medicationList) {
			if (med.getSlotNumber() == slotNumber) {
				return true;
			}
		}
		return false;
	}

	public boolean checkName(String name) {
		List<Medication> medicationList = model.getUser().getMedicationList();
		for (Medication med : medicationList) {
			if (med.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkSlotRange(int slotNumber) {
		return (slotNumber > 6 || slotNumber < 1);
	}
	
	public boolean checkTimeRange(int hour, int min) {
		return (hour>=0 && hour<=23 && min>=0 && min<=59);
	}

}
