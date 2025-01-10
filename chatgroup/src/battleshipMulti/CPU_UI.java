package battleshipMulti;


import javafx.animation.Timeline;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.text.ParseException;

public class CPU_UI extends Application {
	
	static final int GAP = 20; 
	
	private PlayerBoard playerBoard;
	private Board enemyBoard;
	
	private int lastRow, lastCol;
	
	private static boolean isMulti;
	
	private Button btnFire, btnRotate, btnReady, btnMenu;
	
	private TextArea shotListTextArea;
	private ScrollPane shotListScrollPane;
	
	private ShotList shotList;
	
	private ExecutorService gameExecutor;
	
	private Label title, serverMsg;
	
	Timeline gameLoop;
	
	private CPU cpu;
	private String cpuGameTurn;
	
	private Parent createContent(Stage primaryStage) {
	    primaryStage.setWidth(900);
	    primaryStage.setHeight(1000);

	    BorderPane root = new BorderPane();
	    root.setStyle("-fx-background-color: black;");

	    //Title panel
	    VBox titlePanel = new VBox();
	    titlePanel.setPrefHeight(50);
	    titlePanel.setAlignment(Pos.CENTER);

	    title = new Label("Playing: CPU");
	    title.setTextFill(Color.GREEN);
	    title.setFont(Font.font("Monospaced", 16));

	    serverMsg = new Label("Place your ships...");
	    serverMsg.setTextFill(Color.GREEN);
	    serverMsg.setFont(Font.font("Monospaced", 12));

	    titlePanel.getChildren().addAll(title, serverMsg);
	    root.setTop(titlePanel);

	    //Player and enemy boards
	    enemyBoard = new Board();
	    playerBoard = new PlayerBoard();

	    enemyBoard.drawBoard();
	    playerBoard.drawBoard();

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
	    btnReady.setOnAction(event -> runGame());
	    btnMenu.setOnAction(event -> returnToMenu(primaryStage));

	    btnFire.setDisable(true);
	    btnFire.setVisible(false);
	    btnMenu.setVisible(false);

	    controlPanel.getChildren().addAll(btnFire, btnRotate, btnReady, btnMenu);

	    //Center the boards and control panel
	    VBox boardsContainer = new VBox(GAP, enemyBoard, playerBoard, controlPanel);

	    
	    boardsContainer.setAlignment(Pos.CENTER);

	    //Use StackPane to center the VBox in the screen
	    StackPane centerPane = new StackPane(boardsContainer);
	    centerPane.setAlignment(Pos.CENTER);

	    root.setCenter(centerPane);

	    //Shot list panel
	    
	    shotList = new ShotList();
	    
	    VBox shotListPanel = new VBox(10);
	    shotListPanel.setStyle("-fx-background-color: black;");
	    shotListPanel.setPrefWidth(300); //Increase the width of the shot list
	    shotListPanel.setPadding(new Insets(10)); //Add internal padding

	    shotListTextArea = new TextArea();
	    shotListTextArea.setEditable(false);
	    shotListTextArea.setStyle("-fx-control-inner-background: black; -fx-text-fill: green; -fx-border-color: green;");
	    shotListTextArea.setFont(Font.font("Monospaced", 12)); //Slightly larger font
	    shotListTextArea.setPrefHeight(500); //Increase height as needed

	    shotListScrollPane = new ScrollPane(shotListTextArea);
	    shotListScrollPane.setStyle("-fx-background-color: black;");

	    //Sorting and searching buttons
	    Button btnSortUp = createStyledButton("Sort ↑", 12);
        Button btnSortDown = createStyledButton("Sort ↓", 12);
	    Button btnSearch = createStyledButton("Search", 12);
	    
	    btnSortUp.setOnAction(event -> sortUp());
	    btnSortDown.setOnAction(event -> sortDown());
	    //btnSearch.setOnAction(event -> searchLogs());

	    HBox sortButtons = new HBox(10, btnSortUp, btnSortDown, btnSearch);
	    sortButtons.setAlignment(Pos.CENTER);

	    shotListPanel.getChildren().addAll(shotListScrollPane, sortButtons);

	    //Offset the panel slightly from the right edge
	    BorderPane.setMargin(shotListPanel, new Insets(0, 20, 0, 0)); //Add a 20px margin on the right
	    root.setRight(shotListPanel);

	    return root;
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
	
	public void runGame() {
		if (!isMulti) {
			if (!playerBoard.getAllShipsPlaced()) {
		        serverMsg.setText("Place all of your ships first!");
		        return;
		    }
		    
		    btnFire.setVisible(true);
		    btnFire.setDisable(false);
		    btnRotate.setDisable(true);
		    btnReady.setDisable(true);
		    
		    cpu = new CPU();
		    cpuGameTurn = "Waiting";
		    
		    gameExecutor = Executors.newSingleThreadExecutor();
		    gameExecutor.submit(this::gameLoop);
		}
		else if (isMulti) {
			serverMsg.setText("Multiplayer test");
		}
	}
	

	
	private void gameLoop() {
	    while (!Thread.currentThread().isInterrupted()) {
	        try {
	            Thread.sleep(250);
	            if ("Waiting".equals(cpuGameTurn)) {
	                javafx.application.Platform.runLater(() -> serverMsg.setText("Select a cell and fire!"));
	            } else if ("ShotAt".equals(cpuGameTurn)) {
	                javafx.application.Platform.runLater(this::handlePlayerShot);
	            } else if ("cpu".equals(cpuGameTurn)) {
	                javafx.application.Platform.runLater(this::handleCPUShot);
	            }
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	        }
	    }
	}
	
	private void updateShotList(ShotRecord record) {
	    javafx.application.Platform.runLater(() -> {
	        shotList.insert(record);
	        shotListTextArea.setText(shotList.toString());
	    });
	}
	
	public static void setIsMulti(boolean multi) {
        isMulti = multi;
    }
	


	private void handlePlayerShot() {
	    boolean hit = cpu.checkHit(enemyBoard.getRclick(), enemyBoard.getCclick());
	    if (hit) {
	        enemyBoard.placeHitMarker(enemyBoard.getRclick(), enemyBoard.getCclick());
	    } else {
	        enemyBoard.placeMissMarker(enemyBoard.getRclick(), enemyBoard.getCclick());
	    }

	    updateShotList(new ShotRecord(enemyBoard.getRclick(), enemyBoard.getCclick(), hit, "You"));

	    if (cpu.hasLost()) {
	        endGame(true);
	    } else {
	        cpuGameTurn = "cpu";
	    }
	}

	private void handleCPUShot() {
	    int[] shot = cpu.shoot();
	    boolean hit = playerBoard.checkEnemyShot(shot[0], shot[1]);
	    if (hit) {
	        playerBoard.placeHitMarker(shot[0], shot[1]);
	    } else {
	        playerBoard.placeMissMarker(shot[0], shot[1]);
	    }

	    updateShotList(new ShotRecord(shot[0], shot[1], hit, "CPU"));

	    if (playerBoard.hasLost()) {
	        endGame(false);
	    } else {
	        cpuGameTurn = "Waiting";
	    }
	}
	
	private void endGame(boolean playerWon) {
	    title.setText(playerWon ? "You won!" : "You lost!");
	    btnMenu.setVisible(true);
	    btnFire.setDisable(true);
	    if (gameExecutor != null && !gameExecutor.isShutdown()) {
	        gameExecutor.shutdownNow();
	    }
	}
	
	public void shootShip() {
	    int row = enemyBoard.getRclick();
	    int col = enemyBoard.getCclick();
	    if (row == lastRow && col == lastCol) {
	        serverMsg.setText("Pick a different square than last time!");
	        return;
	    }
	    lastRow = row;
	    lastCol = col;
	    cpuGameTurn = "ShotAt";
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
	
		
	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(createContent(primaryStage));
        primaryStage.setTitle("Battleship");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
		
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
