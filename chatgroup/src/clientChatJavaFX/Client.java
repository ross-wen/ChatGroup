package clientChatJavaFX;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Client extends Application {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    private TextArea chatArea = new TextArea();
    private TextField messageField = new TextField();
    private TextField usernameField = new TextField();
    private TextField ipField = new TextField();
    private TextField portField = new TextField();
    private Button connectButton = new Button("Connect");
    private Button sendButton = new Button("Send");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Chat Area (non-editable)
        chatArea.setEditable(false);
        chatArea.setPrefHeight(300);

        // Input Fields
        usernameField.setPromptText("Enter your username");
        ipField.setPromptText("Server IP (e.g., 127.0.0.1)");
        portField.setPromptText("Port (e.g., 5000)");
        messageField.setPromptText("Type your message here...");
        messageField.setDisable(true);
        sendButton.setDisable(true);

        // Buttons
        connectButton.setOnAction(e -> connectToServer());
        sendButton.setOnAction(e -> sendMessage());
        messageField.setOnAction(e -> sendMessage());

        // Layout
        VBox connectionPane = new VBox(5, new Label("Connection Details"), usernameField, ipField, portField, connectButton);
        VBox chatPane = new VBox(5, new Label("Chat"), chatArea, messageField, sendButton);

        VBox root = new VBox(10, connectionPane, chatPane);
        root.setStyle("-fx-padding: 10;");

        Scene scene = new Scene(root, 400, 500);
        primaryStage.setTitle("Chat Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void stop() {
    	closeEverything(socket, bufferedReader, bufferedWriter);
    }

    private void connectToServer() {
        String ip = ipField.getText();
        String portText = portField.getText();
        username = usernameField.getText();

        if (ip.isEmpty() || portText.isEmpty() || username.isEmpty()) {
            appendToChat("Please fill in all fields to connect.");
            return;
        }

        try {
            int port = Integer.parseInt(portText);
            socket = new Socket(ip, port);

            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            appendToChat("Connected to the server as " + username);
            messageField.setDisable(false);
            sendButton.setDisable(false);

            listenForMessages();
        } catch (IOException e) {
            appendToChat("Error connecting to the server: " + e.getMessage());
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (message.isEmpty()) {
            return;
        }

        try {
            bufferedWriter.write(username + ": " + message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            appendToChat("You: " + message);
            messageField.clear();
        } catch (IOException e) {
            appendToChat("Error sending message: " + e.getMessage());
        }
    }

    private void listenForMessages() {
        new Thread(() -> {
            String msgFromServer;
            try {
                while ((msgFromServer = bufferedReader.readLine()) != null) {
                    String finalMsg = msgFromServer;
                    Platform.runLater(() -> appendToChat(finalMsg));
                }
            } catch (IOException e) {
                Platform.runLater(() -> appendToChat("Connection closed: " + e.getMessage()));
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }).start();
    }

    private void appendToChat(String message) {
        chatArea.appendText(message + "\n");
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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
