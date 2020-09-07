import javax.jws.soap.SOAPBinding;
import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

	private static Map<String, User> listUsers = new HashMap<String, User>();
	private static ArrayList<User> userOnline = new ArrayList<User>();
	private static ArrayList<ServiceThread> workers = new ArrayList<ServiceThread>();

	public final static int FILE_SIZE = 100;
	public final static String FILE_TO_RECEIVED = "/home/tuanlab/Linh/Bkav_ChatServer/src/test1.txt";

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
		private OutputStream out = null;
		private InputStream in = null;



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
				in = socketOfServer.getInputStream();

				is = new BufferedReader(new InputStreamReader(in));
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
			/*byte [] myByteArray = new byte[FILE_SIZE];
			InputStream in = socketOfServer.getInputStream();
			FileOutputStream fos = new FileOutputStream(FILE_TO_RECEIVED);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			System.out.println(myByteArray.length);
			int bytesRead = in.read(myByteArray, 0, myByteArray.length);
			/*int current = bytesRead;

			do {
				bytesRead = in.read(myByteArray, current, (myByteArray.length - current));
				if (bytesRead >= 0) current += bytesRead;
			} while (bytesRead > -1);

			//bos.write(myByteArray, 0, current);
			bos.write(myByteArray, 0, bytesRead);
			//bos.flush();
			System.out.println("File " + FILE_TO_RECEIVED + " downloaded (" + bytesRead + " bytes read)");
			//fos.close();
			bos.close();
			socketOfServer.close();+/ */
			/*DataInputStream dis = new DataInputStream(in);
			FileOutputStream fos = new FileOutputStream(FILE_TO_RECEIVED);
			byte[] buffer = new byte[4096];

			int filesize = 15123; // Send file size in separate msg
			int read = 0;
			int totalRead = 0;
			int remaining = filesize;
			while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
				totalRead += read;
				remaining -= read;
				System.out.println("read " + totalRead + " bytes.");
				fos.write(buffer, 0, read);
			}

			fos.close();
			dis.close();*/
			/*ObjectOutputStream oos = new ObjectOutputStream(socketOfServer.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socketOfServer.getInputStream());
			FileOutputStream fos = null;
			byte [] buffer = new byte[FILE_SIZE];

			// 1. Read file name.
			Object o = ois.readObject();

			if (o instanceof String) {
				fos = new FileOutputStream(o.toString());
			} else {
				throwException("Something is wrong");
			}

			// 2. Read file to the end.
			Integer bytesRead = 0;

			do {
				o = ois.readObject();

				if (!(o instanceof Integer)) {
					throwException("Something is wrong");
				}

				bytesRead = (Integer)o;

				o = ois.readObject();

				if (!(o instanceof byte[])) {
					throwException("Something is wrong");
				}

				buffer = (byte[])o;

				// 3. Write data to output file.
				fos.write(buffer, 0, bytesRead);

			} while (bytesRead == FILE_SIZE);

			System.out.println("File transfer success");

			fos.close();

			ois.close();
			oos.close();*/

			byte[] contents = new byte[10000];

			FileOutputStream fos = new FileOutputStream(FILE_TO_RECEIVED);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			InputStream is = socketOfServer.getInputStream();

			//No of bytes read in one read() call
			int bytesRead = 0;

			while((bytesRead=is.read(contents))!=-1)
				bos.write(contents, 0, bytesRead);

			bos.flush();
			socketOfServer.close();

			System.out.println("File saved successfully!");
		}

	}

}
