import java.util.ArrayList;

public class Box {
	private User user1, user2;
	private ArrayList<String> listMessages = new ArrayList<String>();
	private String idBox;
	
	public Box(User user1, User user2) {
		super();
		this.user1 = user1;
		this.user2 = user2;
		if (this.user1.getUsername().compareTo(this.user2.getUsername()) < 0) this.idBox = this.user1.getUsername() + this.user2.getUsername();
		else this.idBox = this.user2.getUsername() + this.user1.getUsername();
	}
	
	
	
	
}
