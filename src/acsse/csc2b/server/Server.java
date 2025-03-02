package acsse.csc2b.server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

	ServerSocket servSock = null;
	static final int PORT = 5432;
	boolean running = false;

	// Constructor:
	public Server() {
		try {
			servSock = new ServerSocket(PORT);
			running = true;
			System.out.println("Server Running On Port: "+ PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e);
		}
	}

	public void Start() {
		while (running == true) {
			// Handling connections using the client handler class
			try {
				Thread thread = new Thread(new ClientHandler(servSock.accept()));
				thread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println(e);
			}

		}
	}
}
