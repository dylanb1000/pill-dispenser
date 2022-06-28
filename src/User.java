
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	private String firstName;
	private String lastName;
	private Log log = new Log();
	private List<Medication> medicationList = new ArrayList<Medication>();

	public User(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Log getLog() {
		return this.log;
	}

	public List<Medication> getMedicationList() {
		return this.medicationList;
	}

	public void addMedication(Medication medication) {
		medicationList.add(medication);
	}
}
