import java.io.Serializable;

public class Model implements Serializable {
	private static final long serialVersionUID = 1L;
	private User user = new User("Dylan", "Bui");
	private int machineCurrentSlot;
	public Model() {
		this.machineCurrentSlot=0;
	}

	public User getUser() {
		return this.user;
	}
	
	public int getCurrentSlot(){
		return this.machineCurrentSlot;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public void setCurrentSlot(int slot) {
		this.machineCurrentSlot = slot;
	}
}
