import javax.crypto.CipherOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) throws IOException {

		final String serverHost = "localHost";

		Socket socketOfClient = null;
		BufferedWriter os = null;
		BufferedReader is = null;

		Scanner scanner = new Scanner(System.in);

		try {
			socketOfClient = new Socket(serverHost, 1234);

			os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));

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
}
