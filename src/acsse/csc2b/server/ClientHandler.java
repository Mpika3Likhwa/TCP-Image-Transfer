package acsse.csc2b.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler implements Runnable {

	ObjectOutputStream objOut = null;
	ObjectInputStream objInn = null;
	DataInputStream dis = null;
	DataOutputStream dos = null;

	ArrayList<String> storedList = null;

	// Constructor:
	public ClientHandler(Socket link) {
		Initialise(link);
		System.out.println("Connection Accepted");
	}

	// Method to initialise all the communication streams
	private void Initialise(Socket link) {
		try {
			objOut = new ObjectOutputStream(link.getOutputStream());
			objInn = new ObjectInputStream(link.getInputStream());
			dis = new DataInputStream(link.getInputStream());
			dos = new DataOutputStream(link.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e);
		}

	}

	@Override
	public void run() {
		// Handle all types of requests from the client

		try {
			while (true) {
				String request = objInn.readUTF();
				if (request.startsWith("LIST")) {
					// Calling the function for handling a LIST request
					HandleLIST();
				} else if (request.startsWith("DOWN")) {
					// Calling the function for handling the DOWN request
					HandleDOWN(request);
				} else if (request.startsWith("UP")) {
					// Calling the function for handling the UP request
					HandleUP(request);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
		}
		// }

	}

	@SuppressWarnings("unchecked")
	private void HandleLIST() {
		// Getting the list of images and sending them over
		ArrayList<String> list = new ArrayList<>();
		try {
			Scanner scanner = new Scanner(new File("data/server/ImgList.txt"));
			while (scanner.hasNextLine()) {
				list.add(scanner.nextLine());
			}
			scanner.close();
			storedList = (ArrayList<String>) list.clone();
			objOut.writeObject(list);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e);
		}
	}

	private void HandleDOWN(String request) {

		if (storedList != null) {

			String[] tokens = request.split(" ");

			String ID = tokens[1];

			for (String line : storedList) {
				if (line.startsWith(ID)) {
					String name = line.split(" ")[1];
					// System.out.println("name");
					File FileToSend = new File("data/server/" + name);
					if (FileToSend.exists()) {
						System.out.println("in file exists");

						try {
							// sending the name and size of the Image
							String Image_Infor = String.format("%s %d", FileToSend.getName(), FileToSend.length());

							objOut.writeUTF(Image_Infor);
							objOut.flush();

							// sending the actual File/Image
							FileInputStream fis = new FileInputStream(FileToSend);
							byte[] buffer = new byte[2048];
							int n = 0;
							while ((n = fis.read(buffer)) > 0) {
								dos.write(buffer, 0, n);
								dos.flush();
							}
							fis.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.err.println(e);
						}
					}
				}
			}
		}
	}

	private void HandleUP(String request) {
		// receive the Image and save it then add it's ID and name to the txt list
		String[] tokens = request.split(" ");
		String ID = tokens[1];
		String name = tokens[2];
		Long size = Long.parseLong(tokens[3]);

		File FileToReceive = new File("data/server/" + name);
		try {
			FileOutputStream fos = new FileOutputStream(FileToReceive);
			byte[] buffer = new byte[2048];
			int n = 0;
			int totalbytes = 0;
			while (totalbytes != size) {
				n = dis.read(buffer, 0, buffer.length);
				fos.write(buffer, 0, n);
				fos.flush();
				totalbytes += n;
			}
			fos.close();

			String line = ID + " " + name;
			FileWriter FW = new FileWriter(new File("data/server/ImgList.txt"), true);
			PrintWriter out = new PrintWriter(FW);
			out.print("\n" + line);
			out.close();

			objOut.writeUTF("SUCCESS");
			objOut.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			try {
				objOut.writeUTF("Failed");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.err.println(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				objOut.writeUTF("Failed");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.err.println(e);
		}
	}
}
