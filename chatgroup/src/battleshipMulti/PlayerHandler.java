package battleshipMulti;

import java.io.*;
import java.net.*;
import java.util.*;

public class PlayerHandler implements Runnable{
	
	public static ArrayList<PlayerHandler> playerHandlers = new ArrayList<>();
	
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	
	private String playerUsername;
	
	public PlayerHandler(Socket socket) {
		try {
			this.socket = socket;
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.playerUsername = bufferedReader.readLine();
			playerHandlers.add(this);
		} catch(IOException e) { 
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}

	@Override
	public void run() {
		String messageFromPlayer;
		
		while (socket.isConnected()) {
			try {
				messageFromPlayer = bufferedReader.readLine();
				broadcastMessage(messageFromPlayer);
			} catch(IOException e) {
				closeEverything(socket, bufferedReader, bufferedWriter);
				break;
			}
			
		}
		
	}
	
	public void broadcastMessage(String messageToSend) {
		for (PlayerHandler playerHandler : playerHandlers) {
			try {
				if (!playerHandler.playerUsername.equals(playerUsername)) {
					playerHandler.bufferedWriter.write(messageToSend);
					playerHandler.bufferedWriter.newLine();
					playerHandler.bufferedWriter.flush();

				}
			} catch (IOException e) {
				closeEverything(socket, bufferedReader, bufferedWriter);
			}
		}
	}
	
	public void removePlayerHandler() {
		playerHandlers.remove(this);
		broadcastMessage("SERVER: " + playerUsername + " has left the game!");
	}
	
	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
		removePlayerHandler();
		try {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (bufferedWriter != null) {
				bufferedWriter.close();
			}
			if (socket != null) {
				socket.close();
	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
