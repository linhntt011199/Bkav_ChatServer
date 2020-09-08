import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public final static String FILE_TO_SEND = "/home/tuanlab/Linh/Bkav_ChatServer/src/test.txt";
	private OutputStream out = null;

	public static void main(String[] args) {
		Client client = new Client();
		client.connectServer();
	}

	public void connectServer() {
		final String serverHost = "localHost";
		Socket socketOfClient;

		BufferedWriter os;
		BufferedReader is;

		Scanner scanner = new Scanner(System.in);

		try {
			socketOfClient = new Socket(serverHost, 1234);
			out = socketOfClient.getOutputStream();

			os = new BufferedWriter(new OutputStreamWriter(out));

			is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host" + serverHost);
			return;
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + serverHost);
			return;
		}

		try {
			System.out.print("Enter username: ");
			String username = scanner.nextLine();
			os.write(username);
			os.newLine();
			os.flush();

			System.out.print("Enter password: ");
			String password = scanner.nextLine();
			os.write(password);
			os.newLine();
			os.flush();

			while (true) {

				System.out.print("Enter: ");
				String message = scanner.nextLine();
				if (message.equals("QUIT"))
					break;
				if (message.contains("SEND")) sendFile();
				os.write(message);
				os.newLine();
				os.flush();

				while (true) {
					String responseLine = is.readLine();
					if (responseLine == null || responseLine.equals("")) break;
					System.out.println("Server: " + responseLine);
				}
			}

			os.close();
			is.close();
			socketOfClient.close();
		} catch (UnknownHostException e) {
			System.err.println("Trying to connect to unknown host: " + e);
		} catch (IOException e) {
			System.err.println("IOException: " + e);
		}
	}

	public void sendFile() throws IOException {
		FileInputStream fis;
		BufferedInputStream bis;
		File myFile = new File(FILE_TO_SEND);

		fis = new FileInputStream(myFile);
		bis = new BufferedInputStream(fis);
		byte [] myByteArray = new byte[(int) myFile.length()];
		bis.read(myByteArray, 0, myByteArray.length);
		System.out.println("Sending " + FILE_TO_SEND + "(" + myByteArray.length + " bytes)");

		out.write(myByteArray, 0, myByteArray.length);
		out.flush();
		System.out.println("Done.");
		bis.close();


	}
}
