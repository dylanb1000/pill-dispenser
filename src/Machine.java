import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;

public class Machine implements Runnable{
	private Queue<Medication> queue =new LinkedList<Medication>();
	private Model model;
	private int currentSlot;
	public Machine(Model model) {
		this.model=model;
	}
	
	public Queue<Medication> getQueue(){
		return this.queue;
	}
	
	public void addToMedicationQueue(Medication med) {
		queue.add(med);
	}
	
	public void dispenseMedication() {
		Medication dispensedMed=queue.remove();
		int slotNumber = dispensedMed.getSlotNumber();
		//logic to dispense medication(Probably as a function below)
		System.out.println("Dispensed: "+ dispensedMed.getName());
		//log
		String name=dispensedMed.getName();
		String time=LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		model.getUser().getLog().addEntry(name, time);
	}
	@Override
	public void run() {
		while(true) {
			try {
				dispenseMedication();
			} catch(Exception e) {
				
			}
		}
	}
}
