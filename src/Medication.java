
import java.text.MessageFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Medication {
	private String name;
	private int count;
	private int slotNumber;
	private List<Date> dateTaken=new ArrayList<Date>();
	private LocalTime dispenseTime;
	private int dispenseRate;
	
	public Medication(String name,int count,int slotnumber,LocalTime time,int dispenseRate) {
		this.name=name;
		this.count=count;
		this.slotNumber=slotnumber;
		this.dispenseTime=time;
		this.dispenseRate=dispenseRate;
	}
	public int getSlotNumber() {
		return this.slotNumber;
	}
	public String getName() {
		return this.name;
	}
	
	public int getCount() {
		return this.count;
	}
	
	public List<Date> getDateList(){
		return this.dateTaken;
	}
	
	public LocalTime getDispenseTime() {
		return this.dispenseTime;
	}
	
	public int getDispenseRate() {
		return this.dispenseRate;
	}
	
	public void setCount(int count) {
		this.count=count;
	}
	
	public void setSlotNumber(int number){
		this.slotNumber=number;
	}
	
	public void setDispenseTime(LocalTime time) {
		this.dispenseTime=time;
	}
	
	public void setDispenseRate(int rate) {
		this.dispenseRate=rate;
	}
	
	public void addEntry() {
		dateTaken.add(new Date());
		this.count--;
	}
	//determines how medication will be displayed on the main list
	@Override
	public String toString() {
		return this.name+" ["+this.slotNumber+"]";
	}
	
	public String toStringInfo() {
		return MessageFormat.format("NAME:{0} COUNT:{1} SLOT_NUMBER:{2} DISPENSE_TIME:{3} DISPENSE_RATE:{4}",name,count,slotNumber,dispenseTime.toString(),dispenseRate);
	}
	
}
