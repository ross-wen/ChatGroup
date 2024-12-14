package clientChatJavaFX;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.*;

public class Server extends Application {
    private ServerSocket serverSocket;
    private TextArea logArea = new TextArea();
    private TextField portField = new TextField();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // GUI Setup
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
                    while (!serverSocket.isClosed()) {
                        Socket socket = serverSocket.accept();
                        appendLog("A new client has connected!");
                        ClientHandler clientHandler = new ClientHandler(socket);

                        Thread thread = new Thread(clientHandler);
                        thread.start();
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

    private void appendLog(String message) {
        logArea.appendText(message + "\n");
    }

    @Override
    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            appendLog("Server stopped.");
        } catch (IOException e) {
            appendLog("Error while closing server: " + e.getMessage());
        }
    }
}
