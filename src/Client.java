import javax.crypto.CipherOutputStream;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

public class Client {

	private Socket socketOfClient = null;
	public final static String FILE_TO_SEND = "/home/tuanlab/Linh/Bkav_ChatServer/src/test.txt";
	private OutputStream out = null;

	public static void main(String[] args) throws IOException {
		Client client = new Client();
		client.connectServer();
	}

	public void connectServer() {
		final String serverHost = "localHost";

		BufferedWriter os = null;
		BufferedReader is = null;

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
				if (message.equals("QUIT "))
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
		/*FileInputStream fis = null;
		BufferedInputStream bis = null;
		File myFile = new File(FILE_TO_SEND);

		fis = new FileInputStream(myFile);
		bis = new BufferedInputStream(fis);
		bis.read(myByteArray, 0, myByteArray.length);
		System.out.println("Sending " + FILE_TO_SEND + "(" + myByteArray.length + " bytes)");
		OutputStream out = socketOfClient.getOutputStream();
		out.write(myByteArray, 0, myByteArray.length);
		out.flush();
		System.out.println("Done.");
		bis.close();*/
		//socketOfClient.close();
		/*DataOutputStream dos = new DataOutputStream(out);
		FileInputStream fis = new FileInputStream(FILE_TO_SEND);
		byte[] myByteArray = new byte[4096];

		int read;
		while ((read=fis.read(myByteArray)) > 0) {
			dos.write(myByteArray,0,read);
		}
		fis.close();*/
		//dos.close();
		/*File file = new File(FILE_TO_SEND);
		ObjectInputStream ois = new ObjectInputStream(socketOfClient.getInputStream());
		ObjectOutputStream oos = new ObjectOutputStream(socketOfClient.getOutputStream());

		oos.writeObject(file.getName());

		FileInputStream fis = new FileInputStream(file);
		byte [] buffer = new byte[Server.FILE_SIZE];
		Integer bytesRead = 0;

		while ((bytesRead = fis.read(buffer)) > 0) {
			oos.writeObject(bytesRead);
			oos.writeObject(Arrays.copyOf(buffer, buffer.length));
		}

		oos.close();
		ois.close();
		System.exit(0);*/
		File file = new File(FILE_TO_SEND);
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);

		//Get socket's output stream
		OutputStream os = socketOfClient.getOutputStream();

		//Read File Contents into contents array
		byte[] contents;
		long fileLength = file.length();
		long current = 0;

		long start = System.nanoTime();
		while(current!=fileLength){
			int size = 10000;
			if(fileLength - current >= size)
				current += size;
			else{
				size = (int)(fileLength - current);
				current = fileLength;
			}
			contents = new byte[size];
			bis.read(contents, 0, size);
			os.write(contents);
			System.out.print("Sending file ... "+(current*100)/fileLength+"% complete!");
		}

		os.flush();
		//File transfer done. Close the socket connection!
		//socketOfClient.close();
		System.out.println("File sent succesfully!");
	}
}
