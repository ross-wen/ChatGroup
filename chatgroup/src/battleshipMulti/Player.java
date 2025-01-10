package battleshipMulti;

import java.io.*;

import java.net.*;
import java.text.ParseException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Player extends Application{

	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String username;
	
	
	static final int GAP = 20; 
	
	private PlayerBoard playerBoard;
	private Board enemyBoard;
	
	private int lastRow, lastCol;
	
	private boolean yourTurn;
	
	
	private Button btnFire, btnRotate, btnReady, btnMenu;
	
	private TextArea shotListTextArea;
	
	private ShotList shotList;
	
	private Label title, yourMsg, theirMsg, playerMsg;
	
	private TextArea chatArea = new TextArea();
    private TextField messageField = new TextField();
    private TextField usernameField = new TextField();
    private TextField ipField = new TextField();
    private TextField portField = new TextField();
    private Button connectButton = new Button("Connect");
    private Button sendButton = new Button("Send");
	
	@Override
    public void start(Stage primaryStage) {
        //Root layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: black;");

        //Title panel
        VBox titlePanel = new VBox();
        titlePanel.setPrefHeight(50);
        titlePanel.setAlignment(Pos.CENTER);

        title = new Label("Battleship Server");
        title.setTextFill(Color.GREEN);
        title.setFont(Font.font("Monospaced", 16));

        yourMsg = new Label("Connect to a server...");
        yourMsg.setTextFill(Color.GREEN);
        yourMsg.setFont(Font.font("Monospaced", 12));
        
        theirMsg = new Label("Waiting for other player to connect...");
        theirMsg.setTextFill(Color.GREEN);
        theirMsg.setFont(Font.font("Monospaced", 12));
        
        playerMsg = new Label("Place your ships!");
        playerMsg.setTextFill(Color.GREEN);
        playerMsg.setFont(Font.font("Monospaced", 12));

        titlePanel.getChildren().addAll(title, yourMsg,theirMsg, playerMsg);
        BorderPane.setMargin(titlePanel, new Insets(20, 0, 0, 200));
        root.setTop(titlePanel);

        //Chat area
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setPrefHeight(200);
        chatArea.setStyle("-fx-control-inner-background: white; -fx-text-fill: green; -fx-border-color: green;");

        usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        ipField = new TextField();
        ipField.setPromptText("Server IP (e.g., 127.0.0.1)");
        portField.setPromptText("Port (e.g., 5000)");

        messageField.setPromptText("Type your message here...");
        messageField.setDisable(true);
        sendButton.setDisable(true);

        VBox connectionPane = new VBox(5, new Label("Connection Details"), usernameField, ipField, portField, connectButton);
        VBox chatPane = new VBox(5, new Label("Chat"), chatArea, messageField, sendButton);
        
        connectButton.setOnAction(e -> connectToServer());
        sendButton.setOnAction(e -> sendMessage(messageField.getText()));
        messageField.setOnAction(e -> sendMessage(messageField.getText()));

        VBox sidePanel = new VBox(10, connectionPane, chatPane);
        sidePanel.setStyle("-fx-padding: 10;");
        root.setLeft(sidePanel);
        
        //Control panel
	    HBox controlPanel = new HBox(10);
	    controlPanel.setStyle("-fx-background-color: black;");
	    controlPanel.setAlignment(Pos.CENTER);
        
        btnFire = createStyledButton("Fire", 14);
	    btnRotate = createStyledButton("Rotate", 14);
	    btnReady = createStyledButton("Ready", 14);
	    btnMenu = createStyledButton("Menu", 14);
	    
	    btnFire.setOnAction(event -> shootShip());
	    btnRotate.setOnAction(event -> rotateShip());
	    btnReady.setOnAction(event -> ready());
	    btnMenu.setOnAction(event -> returnToMenu(primaryStage));
	    
	    btnFire.setDisable(true);
	    btnReady.setDisable(true);
	    btnMenu.setVisible(false);
	    
	    controlPanel.getChildren().addAll(btnFire, btnRotate, btnReady, btnMenu);

        //Player and enemy boards
	    enemyBoard = new Board();
	    playerBoard = new PlayerBoard();

	    enemyBoard.drawBoard();
	    playerBoard.drawBoard();

        HBox boardsPane = new HBox(20, enemyBoard, playerBoard);
        boardsPane.setAlignment(Pos.CENTER);
        
        //Center the boards and control panel
	    VBox boardsContainer = new VBox(GAP, boardsPane, controlPanel);
	    BorderPane.setMargin(boardsContainer, new Insets(40, 0, 0, 0));
        root.setCenter(boardsContainer);

        //Shot list
        VBox shotListPanel = new VBox(10);
        shotListPanel.setStyle("-fx-background-color: black;");
        shotListPanel.setPadding(new Insets(10));
        shotListPanel.setPrefWidth(300);

        shotListTextArea = new TextArea();
        shotListTextArea.setEditable(false);
        shotListTextArea.setStyle("-fx-control-inner-background: black; -fx-text-fill: green; -fx-border-color: green;");
        shotListTextArea.setFont(Font.font("Monospaced", 12));

        ScrollPane shotListScrollPane = new ScrollPane(shotListTextArea);
        shotListScrollPane.setStyle("-fx-background-color: black;");

        Button btnSortUp = createStyledButton("Sort ↑", 12);
        Button btnSortDown = createStyledButton("Sort ↓", 12);
        Button btnSearch = createStyledButton("Search", 12);

        btnSortUp.setOnAction(event -> sortUp());
        btnSortDown.setOnAction(event -> sortDown());
        //btnSearch.setOnAction(event -> searchLogs());

        HBox sortButtons = new HBox(10, btnSortUp, btnSortDown, btnSearch);
        sortButtons.setAlignment(Pos.CENTER);

        shotListPanel.getChildren().addAll(shotListScrollPane, sortButtons);
        BorderPane.setMargin(shotListPanel, new Insets(0, 20, 0, 0));
        root.setRight(shotListPanel);
        
        shotList = new ShotList();
        
        lastCol = -1;
        lastRow = -1;
        
        //Scene and stage setup
        Scene scene = new Scene(root, 1500, 600);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Battleship Server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
	
	private void connectToServer() {
        String ip = ipField.getText();
        String portText = portField.getText();
        username = usernameField.getText();

        if (ip.isEmpty() || portText.isEmpty() || username.isEmpty()) {
            receiveOwnMessage("Please fill in all fields to connect.");
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
            
            connectButton.setDisable(true);

            receiveOwnMessage("Connected to the server as " + username);
            messageField.setDisable(false);
            sendButton.setDisable(false);
            listenForMessage();
        } catch (IOException e) {
        	receiveOwnMessage("Error connecting to the server: " + e.getMessage());
        }
    }
	
	public void ready() {
		System.out.println(theirMsg.getText());
		if (!playerBoard.getAllShipsPlaced()) {
	        playerMsg.setText("Place all of your ships first!");
	        return;
	    } else {
	    	if(theirMsg.getText().equals("READY")) {
		    	sendMessage("WAITING");
		    	title.setText("Battleship - You are PLAYER TWO");
		    	yourTurn = false;
		    	btnReady.setVisible(false);
	    	} else {
	    		sendMessage("READY");
		    	title.setText("Battleship - You are PLAYER ONE");
		    	yourTurn = true;
		    	btnReady.setVisible(false);
	    	}

	    }
	}
	


	public void sendMessage(String message) {
        if (message.isEmpty()) {
            return;
        }

        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            receiveOwnMessage(message);
        } catch (IOException e) {
        	receiveOwnMessage("Error sending message: " + e.getMessage());
        }
	}
	
	private void listenForMessage() {
        new Thread(() -> {
            String msgFromServer;
            try {
                while ((msgFromServer = bufferedReader.readLine()) != null) {
                    String finalMsg = msgFromServer;
                    Platform.runLater(() -> logic(finalMsg));
                }
            } catch (IOException e) {
                Platform.runLater(() -> recieveOtherMessage("Connection closed: " + e.getMessage()));
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }).start();
    }
	
	private void logic(String otherMsg) {
		recieveOtherMessage(otherMsg);
		if (otherMsg.equals("The game is starting!")) {
			btnReady.setDisable(false);
		}
		if (otherMsg.startsWith("SHOT")) {
			String[] string = otherMsg.split("/");
			int row = Integer.parseInt(string[1]);
			int col = Integer.parseInt(string[2]);
			if (playerBoard.checkEnemyShot(row, col)) {
				updateShotList(new ShotRecord(row, col, true, "ENEMY"));
				sendMessage("HIT/" + row + "/" + col);
				btnFire.setDisable(false);
			} else {
				sendMessage("MISS/" + row + "/" + col);
				updateShotList(new ShotRecord(row, col, true, "ENEMY"));
				btnFire.setDisable(false);
			}
		}
		if (otherMsg.equals("WAITING")) {
			playerMsg.setText("YOUR TURN - FIRE");
			btnFire.setDisable(false);
		}
		if (otherMsg.startsWith("HIT")) {
			String[] string = otherMsg.split("/");
			int row = Integer.parseInt(string[1]);
			int col = Integer.parseInt(string[2]);
			enemyBoard.placeHitMarker(row, col);
			btnFire.setDisable(true);
			updateShotList(new ShotRecord(row, col, true, "YOU"));
		}
		if (otherMsg.startsWith("MISS")) {
			String[] string = otherMsg.split("/");
			int row = Integer.parseInt(string[1]);
			int col = Integer.parseInt(string[2]);
			enemyBoard.placeMissMarker(row, col);
			btnFire.setDisable(true);
			updateShotList(new ShotRecord(row, col, false, "YOU"));
		}
		if (otherMsg.equals("LOSE")) {
			btnFire.setDisable(true);
			btnMenu.setVisible(true);
			playerMsg.setText("YOU WON!");
		}
		
		if (playerBoard.hasLost()) {
			btnFire.setDisable(true);
			btnMenu.setVisible(true);
			playerMsg.setText("YOU LOST");
			sendMessage("LOSE");
		}
	}
	
	private void updateShotList(ShotRecord record) {
	    javafx.application.Platform.runLater(() -> {
	        shotList.insert(record);
	        shotListTextArea.setText(shotList.toString());
	    });
	}
	
	public void returnToMenu(Stage primaryStage) {
		//Launch the main menu
        MainUI mainMenu = new MainUI();
        try {
            mainMenu.start(new Stage());
            primaryStage.close(); //Close the game stage
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}
	
	public void shootShip() {
	    int row = enemyBoard.getRclick();
	    int col = enemyBoard.getCclick();
	    if (row == lastRow && col == lastCol) {
	        playerMsg.setText("Pick a different square than last time!");
	        return;
	    }
	    lastRow = row;
	    lastCol = col;
	    sendMessage("SHOT/" + row + "/" + col);
	    btnFire.setDisable(true);
	}
	
	public void sortUp() {
		try {
			shotList.insertionSortUp();
			shotListTextArea.setText(shotList.toString());
		} catch (ParseException e1) {
			//TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void sortDown() {
		try {
			shotList.insertionSortDown();
			shotListTextArea.setText(shotList.toString());
		} catch (ParseException e1) {
			//TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/*
	public void searchLogs() {
		String date = FXDialog.readString("Search for a record by date in format: HH:MM:ss");
			
		String out = "Record not found";
		try {
			shotList.insertionSortUp();
			int index = shotList.binarySearch(date);
			if (index != -1) {
				out = shotList.getRecord(index).toString();
			}
		}
		catch (ParseException e1) {
			out = "ParseException: Incorrect date format";
		}
		FXDialog.print(out);
	}
	*/
	
	public void rotateShip() {
		playerBoard.setIsVertical(!playerBoard.getIsVertical());
	}
	
	private void recieveOtherMessage(String message) {
		chatArea.appendText(message + "\n");
        theirMsg.setText(message);
	}
	
	private void receiveOwnMessage(String message) {
        chatArea.appendText(message + "\n");
        yourMsg.setText(message);
    }
	
	public void stop() {
    	closeEverything(socket, bufferedReader, bufferedWriter);
    }
	
	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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
	
	private Button createStyledButton(String text, int fontSize) {
	    Button button = new Button(text);
	    button.setStyle(
	        "-fx-background-color: black; " +
	        "-fx-text-fill: green; " +
	        "-fx-border-color: green; " +
	        "-fx-border-width: 1px;");

	    button.setOnMouseEntered(e -> button.setStyle(
	        "-fx-background-color: green; " +
	        "-fx-text-fill: black; " +
	        "-fx-border-color: green; " +
	        "-fx-border-width: 1px;"));

	    button.setOnMouseExited(e -> button.setStyle(
	        "-fx-background-color: black; " +
	        "-fx-text-fill: green; " +
	        "-fx-border-color: green; " +
	        "-fx-border-width: 1px;"));

	    button.setFont(Font.font("Monospaced", fontSize));
	    return button;
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	
}
