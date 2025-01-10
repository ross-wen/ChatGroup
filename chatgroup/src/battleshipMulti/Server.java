package battleshipMulti;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Application {
    private ServerSocket serverSocket;
    private TextArea logArea = new TextArea();
    private TextField portField = new TextField();
    private int connectedClients = 0; //Track the number of connected clients
    private static final int MAX_CLIENTS = 2; //Maximum number of allowed clients
    private PlayerHandler[] clients = new PlayerHandler[MAX_CLIENTS]; //Hold client handlers
    private ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTS); //Thread pool for handling clients

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //GUI Setup
        logArea.setEditable(false);
        logArea.setPrefHeight(300);

        portField.setPromptText("Enter port number");

        Button startButton = new Button("Start Server");
        startButton.setOnAction(e -> startServer());

        VBox root = new VBox(10, new Label("Server Log"), logArea, portField, startButton);
        root.setStyle("-fx-padding: 10;");
        Scene scene = new Scene(root, 400, 400);

        primaryStage.setTitle("Server Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startServer() {
        String portText = portField.getText();
        try {
            int port = Integer.parseInt(portText);
            serverSocket = new ServerSocket(port);
            InetAddress host = InetAddress.getLocalHost();
            appendLog("Server started on address: " + host + ", port: " + port);

            new Thread(() -> {
                try {
                    while (!serverSocket.isClosed() && connectedClients < MAX_CLIENTS) {
                        Socket socket = serverSocket.accept();

                        if (connectedClients >= MAX_CLIENTS) {
                            appendLog("Maximum number of clients reached. Closing connection...");
                            socket.close();
                            continue;
                        }

                        PlayerHandler clientHandler = new PlayerHandler(socket);
                        clients[connectedClients] = clientHandler;
                        connectedClients++;
                        appendLog("Client connected. Total clients: " + connectedClients);

                        pool.execute(clientHandler);

                        //Start the game if both players are connected
                        if (connectedClients == MAX_CLIENTS) {
                            startGame();
                        }
                    }
                } catch (IOException e) {
                    appendLog("Error: " + e.getMessage());
                }
            }).start();

        } catch (NumberFormatException e) {
            appendLog("Invalid port number. Please enter a valid integer.");
        } catch (IOException e) {
            appendLog("Could not start server: " + e.getMessage());
        }
    }

    private void startGame() {
        appendLog("Both players connected. Starting the game...");
        for (PlayerHandler client : clients) {
            if (client != null) {
                client.broadcastMessage("The game is starting!");
            }
        }
    }

    public synchronized void clientDisconnected(PlayerHandler client) {
        connectedClients--;
        appendLog("Client disconnected. Total clients: " + connectedClients);
        for (int i = 0; i < MAX_CLIENTS; i++) {
            if (clients[i] == client) {
                clients[i] = null;
                break;
            }
        }
    }

    private void appendLog(String message) {
        logArea.appendText(message + "\n");
    }

    @Override
    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            pool.shutdown();
            appendLog("Server stopped.");
        } catch (IOException e) {
            appendLog("Error while closing server: " + e.getMessage());
        }
    }
    
    
}


