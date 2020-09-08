import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.*;

public class Server {

	private static final Map<String, User> listUsers = new HashMap<>();
	private static ArrayList<User> userOnline = new ArrayList<>();
	private static ArrayList<ServiceThread> workers = new ArrayList<>();

	public final static int FILE_SIZE = 100;
	public final static String FILE_TO_RECEIVED = "/home/tuanlab/Linh/Bkav_ChatServer/src/test1.txt";

	public static Connection conn;

	public static void init() {
		User user1 = new User("linh", "aaa");
		listUsers.put(user1.getUsername(), user1);
		User user2 = new User("nva", "bbb");
		listUsers.put(user2.getUsername(), user2);
	}

	public static void connect() throws SQLException, ClassNotFoundException {
		System.out.println("Get connection ...");

		conn = ConnectionUtils.getMyConnection();

		System.out.println("Get connection " + conn);

		System.out.println("Done!");
	}

	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
		connect();
		ServerSocket listener = null;
		System.out.println("Server is waiting to accept user...");
		int clientNumber = 0;
		try {
			listener = new ServerSocket(1234);
		} catch (IOException e) {
			System.out.println();
			System.exit(1);
		}
		init();


		try {
			while (true) {
				Socket socketOfServer = listener.accept();
				BufferedReader is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
				String username, password;
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
							ServiceThread t = new ServiceThread(socketOfServer, clientNumber++, user);
							workers.add(t);
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
		private final Socket socketOfServer;
		private BufferedWriter os;
		private final User user;
		private InputStream in = null;



		public ServiceThread(Socket socketOfServer, int clientNumber, User user) {
			super();
			this.socketOfServer = socketOfServer;
			this.user = user;
			log("New connection with client#  " + clientNumber + " at " + socketOfServer);
		}

		@Override
		public void run() {
			try {
				in = socketOfServer.getInputStream();

				BufferedReader is = new BufferedReader(new InputStreamReader(in));
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
							String idChat = this.user.getUsername() + usernameNhan;
							String queryCheck = "SELECT count(*) from Chat WHERE id = ?";
							PreparedStatement ps = conn.prepareStatement(queryCheck);
							ps.setString(1, idChat);
							ResultSet rs =ps.executeQuery();
							Statement stmt = conn.createStatement();
							if (rs.next()) {} else {
								String sqlInsert = "insert into Chat (id) values ('" + idChat + "');";
								stmt.executeUpdate(sqlInsert);
							}
							String sqlInsert = "insert into Chat_Line(idChat, idUser, line_text) values('" + idChat + "','" + this.user.getUsername() + "','" + message + "');";
							stmt.executeUpdate(sqlInsert);
							worker.os.write("");
							worker.os.newLine();
							worker.os.flush();
							break;
						}
					}
					os.write("");
					os.newLine();
					os.flush();
				}

				if (line.contains("SEND")) {
					//String usernameNhan = line.substring(5, line.length() - 1);

					receiveFile();
					//os.write("");
					//os.newLine();
					//os.flush();
				}

				if (line.equals("QUIT")) {
					os.write("OK");
					os.newLine();
					os.flush();
				}
			} catch (IOException e) {
				System.out.println(e);
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void receiveFile() throws Exception {
			byte [] myByteArray = new byte[FILE_SIZE];

			FileOutputStream fos = new FileOutputStream(FILE_TO_RECEIVED);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			int bytesRead = in.read(myByteArray, 0, myByteArray.length);
			int current = bytesRead;

			do {
				bytesRead = in.read(myByteArray, current, (myByteArray.length - current));
				if (bytesRead >= 0) current += bytesRead;
			} while (bytesRead > -1);
			bos.write(myByteArray, 0, current);

			bos.flush();
			System.out.println("File " + FILE_TO_RECEIVED + " downloaded (" + bytesRead + " bytes read)");
			fos.close();
			bos.close();
			socketOfServer.close();


		}

	}

}
