

public class Model {
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
