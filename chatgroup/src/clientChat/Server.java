package clientChat;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Server {
	
	private ServerSocket serverSocket;

	public Server(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}
	
	public void startServer() {
		
		try {
			
			while (!serverSocket.isClosed()) {
				
				Socket socket = serverSocket.accept();
				System.out.println("A new client has connected!");
				ClientHandler clientHandler = new ClientHandler(socket);
				
				Thread thread = new Thread(clientHandler);
				thread.start();
			}
		} catch (IOException e) {
			
		}
	}
	
	public void closeServerSocket() {
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Port: ");
			String port = scanner.nextLine();
			ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port));
			Server server = new Server(serverSocket);
	        InetAddress host = InetAddress.getLocalHost();
	        System.out.println("The server address is: " + host + ". Please enter on the client side to connect.");
			server.startServer();
		}
	}

}
