import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

public class Log implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<Pair<String,String>> log;

	public Log() {
		this.log=new ArrayList<Pair<String,String>>();
	}
	
	public void addEntry(String name, String date) {
		this.log.add(new Pair<String,String>(name,date));
	}
	
	public List<Pair<String,String>> getLog(){
		return this.log;
	}
}
