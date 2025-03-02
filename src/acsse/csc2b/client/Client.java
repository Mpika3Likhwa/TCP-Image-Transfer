package acsse.csc2b.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javafx.concurrent.Task;

public class Client {

	Socket link = null;
	static final int PORT = 5432;

	ObjectOutputStream objOut = null;
	ObjectInputStream objInn = null;
	DataInputStream dis = null;
	DataOutputStream dos = null;

	// ArrayList<String> list = null;

	// constructor:
	public Client() {
		try {
			link = new Socket("localhost", PORT);
			Initialise(link);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.err.println(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e);
		}
	}

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

	public Task<ArrayList<String>> getList() {

		Task<ArrayList<String>> task = new Task<>() {
			@SuppressWarnings("unchecked")
			@Override
			protected ArrayList<String> call() throws Exception {
				objOut.writeUTF("LIST");
				objOut.flush();
				ArrayList<String> list = (ArrayList<String>) objInn.readObject();
				return list;
			}
		};

		new Thread(task).start();
		return task;

	}

	public Task<File> Down(int ID) {
		Task<File> task = new Task<>() {

			@Override
			protected File call() throws Exception {
				// sending the DOWN command
				String command = String.format("DOWN %d", ID);
				objOut.writeUTF(command);
				objOut.flush();

				// Receiving the Image information then the actual Image
				String Image_information = objInn.readUTF();
				String[] tokens = Image_information.split(" ");
				String name = tokens[0];
				Long size = Long.parseLong(tokens[1]);

				File Fimage = new File("data/client/" + name);
				FileOutputStream fos = new FileOutputStream(Fimage);
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

				return Fimage;
			}
		};
		new Thread(task).start();
		return task;

	}

	public Task<String> Upload(int ID, File imgFile) {
		Task<String> task = new Task<>() {

			@Override
			protected String call() throws Exception {
				// sending the command
				String command = String.format("UP %d %s %d", ID, imgFile.getName(), imgFile.length());
				objOut.writeUTF(command);
				objOut.flush();

				// sending the actual file
				FileInputStream fis = new FileInputStream(imgFile);
				byte[] buffer = new byte[2048];
				int n = 0;
				while ((n = fis.read(buffer)) > 0) {
					dos.write(buffer, 0, n);
					dos.flush();
				}
				fis.close();

				// receiving the result
				String result = objInn.readUTF();
				return result;
			}

		};
		new Thread(task).start();
		return task;

	}
}
