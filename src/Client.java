import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	
	private User user;

	public static void main(String[] args) {
		
		final String serverHost = "localHost";
		
		Socket socketOfClient = null;
		BufferedWriter os = null;
		BufferedReader is = null;
		
		try {
			socketOfClient = new Socket(serverHost, 1234);
			
			os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
			
			is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + serverHost );
			return;
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + serverHost);
			return;
		}
		
		try {
			os.write("HELO");
			os.newLine();
			os.flush();
			os.write("I am Tom Cat");
			os.newLine();
			os.flush();
			os.write("QUIT");
			os.newLine();
			os.flush();
			
			String responseLine;
			while ((responseLine = is.readLine()) != null) {
				System.out.println("Server: " + responseLine);
				if (responseLine.indexOf("OK") != -1) {
					break;
				}
			}
			
			os.close();
			is.close();
			socketOfClient.close();
		} catch (UnknownHostException e) {
			System.err.println("Trying to connect to unknown host: " + e);
		} catch (IOException e) {
			System.out.println("IOException: " + e);
		}
	}

}
