import java.util.ArrayList;

public class Box {
	private User user1, user2;
	private ArrayList<String> listMessages = new ArrayList<String>();
	private String idBox;

	public Box(User user1, User user2) {
		super();
		this.user1 = user1;
		this.user2 = user2;
		this.idBox = this.user1.getUsername() + this.user2.getUsername();
	}

	public String getIdBox() {
		return idBox;
	}

	public void setIdBox(String idBox) {
		this.idBox = idBox;
	}

	public ArrayList<String> getListMessages() {
		return listMessages;
	}

	public void setListMessages(ArrayList<String> listMessages) {
		this.listMessages = listMessages;
	}




}
