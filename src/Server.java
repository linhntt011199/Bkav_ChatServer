import javax.jws.soap.SOAPBinding;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

	private static Map<String, User> listUsers = new HashMap<String, User>();
	private static ArrayList<User> userOnline = new ArrayList<User>();
	private static ArrayList<ServiceThread> workers = new ArrayList<ServiceThread>();

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
			listener = new ServerSocket(1234);
		} catch (IOException e) {
			System.out.println(e);
			System.exit(1);
		}
		init();
		try {
			while (true) {
				Socket socketOfServer = listener.accept();
				BufferedReader is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
				BufferedWriter os = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));
				String username = null, password = null;
				String line = is.readLine();
				username = line;
				line = is.readLine();
				password = line;
				Set<String> set = listUsers.keySet();
				for (String key : set) {
					if (key.equals(username)) {
						User user = listUsers.get(key);
						if (user.getPassword().equals(password)) {
							userOnline.add(user);
							Thread t = new ServiceThread(socketOfServer, clientNumber++, user);
							workers.add((ServiceThread) t);
							t.start();
							break;
						}
					}
				}
			}

		} finally {
			listener.close();
		}
	}

	private static void log(String message) {
		System.out.println(message);
	}

	private static class ServiceThread extends Thread {
		private int clientNumber;
		private Socket socketOfServer;
		private BufferedReader is;
		private BufferedWriter os;
		private User user;


		public ServiceThread(Socket socketOfServer, int clientNumber, User user) {
			super();
			this.clientNumber = clientNumber;
			this.socketOfServer = socketOfServer;
			this.user = user;
			log("New connection with client#  " + this.clientNumber + " at " + socketOfServer);
		}

		@Override
		public void run() {
			try {

				is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
				os = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));


				String line = is.readLine();

				if (line.equals("PRINT")) {
					for (User user: userOnline) {
						os.write(">> " + user.getUsername());
						os.newLine();
						os.flush();
					}
					os.write("");
					os.newLine();
					os.flush();
				}

				if (line.contains("Chat")) {

					int end = line.indexOf("'");
					String usernameNhan = line.substring(5, end - 1);
					String message = line.substring(end + 1, line.length() - 1);

					for (ServiceThread worker : workers) {
						if (worker.user.getUsername().equals(usernameNhan)) {
							worker.os.write(" >> " + this.user.getUsername() + ": " + message);
							worker.os.newLine();
							worker.os.flush();
							worker.os.write("");
							worker.os.newLine();
							worker.os.flush();
							break;
						}
					}
				}

				if (line.equals("QUIT")) {
					os.write("OK");
					os.newLine();
					os.flush();

				}

			} catch (IOException e) {
				System.out.println(e);
				e.printStackTrace();
			}
		}



	}

}
