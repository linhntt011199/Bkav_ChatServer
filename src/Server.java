import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Server {
	
	private static Map<String, User> listUsers = new HashMap<String, User>();
	
	public static void init() {
		User user1 = new User("linh", "aaa");
		listUsers.put(user1.getUsername(), user1);
		User user2 = new User("nva", "bbb");
		listUsers.put(user2.getUsername(), user2);
		
	}

	public static void main(String[] args) throws IOException{
	
		ServerSocket listener = null;
		
		System.out.println("Server is waiting to accept user...");
		int clientNumber = 0;
		
		try {
			listener = new ServerSocket(1235);
			
		} catch (IOException e) {
			System.out.println(e);
			System.exit(1);
		}
		init();

		try {
			while (true) {
			
				Socket socketOfServer = listener.accept();
				new ServiceThread(socketOfServer, clientNumber++).start();
			}
		} finally {
			listener.close();
		}
	}
	
	private static void log(String message) {
		System.out.println(message);
	}
	
	private static class ServiceThread extends Thread{
		private int clientNumber;
		private Socket socketOfServer;
		private User userGui, userNhan;
		
		
		public ServiceThread(Socket socketOfServer, int clientNumber) {
			super();
			this.clientNumber = clientNumber;
			this.socketOfServer = socketOfServer;
			
			log("New connection with client#  " + this.clientNumber + " at " + socketOfServer);
		}
		
		@Override
		public void run() {
			
			try {
				
				BufferedReader is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
				BufferedWriter os = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));
				
				while (true) {
					String line = is.readLine();
					String username = null, password = null;
					if (line.contains("username : ")) {
						username = line.substring(11);
					}
					else if (line.contains("password: ")) password = line.substring(11);
					Set<String> set = listUsers.keySet();
					for (String key : set) {
			            if (key.equals(username)) {
			            	userGui = listUsers.get(key);
			            	break;
			            }
			  
			        }
					if (line.contains("Chat ")) {
						int index = line.indexOf("'");
						username = line.substring(5, index - 2);
						set = listUsers.keySet();
						for (String key : set) {
				            if (key.equals(username)) {
				            	userNhan = listUsers.get(key);
				            	break;
				            }
						}
				       String message = line.substring(index + 1, line.length() - 1);
					}
					
					os.write(">> " + line);
					os.newLine();
					os.flush();
					
					if (line.equals("QUIT")) {
						os.write(">> OK");
						os.newLine();
						os.flush();
						break;
					}
				}
				
			} catch (IOException e) {
				System.out.println(e);
				e.printStackTrace();
			}
		}
			
	}

}
