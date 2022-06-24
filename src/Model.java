import java.io.Serializable;

public class Model implements Serializable{
	private static final long serialVersionUID = 1L;
	private User user = new User("Dylan","Bui");
	
	public Model() {

	}
	public User getUser() {
		return this.user;
	}
	
	public void setUser(User user) {
		this.user=user;
	}
}
