package battleshipMulti;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        //Title label
        Label titleLabel = new Label("BATTLESHIP");
        titleLabel.setFont(Font.font("Monospaced", 36));
        titleLabel.setTextFill(Color.GREEN);

        //Buttons
        Button startButton = createStyledButton("Start Game Against CPU");
        startButton.setOnAction(e -> singlePlayer(primaryStage));
       

        Button instructionsButton = createStyledButton("Create Server");
        instructionsButton.setOnAction(e -> createServer(primaryStage));

        Button settingsButton = createStyledButton("Join Server");
        settingsButton.setOnAction(e -> openPlayerIU(primaryStage));
        
        Button helpButton = createStyledButton("Help");
        helpButton.setOnAction(e -> openHelpMenu(primaryStage));

        Button exitButton = createStyledButton("Exit Game");
        exitButton.setOnAction(e -> primaryStage.close());
        

        //Background image (optional)
        Image backgroundImage = new Image("images/battleship_background.jpg");
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setFitWidth(1200);
        backgroundView.setFitHeight(1000);
        backgroundView.setOpacity(0.2); //Subtle background
        backgroundView.setPreserveRatio(true);

        //Layout
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(titleLabel, startButton, instructionsButton, settingsButton, helpButton, exitButton);

        //Root pane with black background
        StackPane root = new StackPane();
        root.getChildren().addAll(backgroundView, layout);
        root.setStyle("-fx-background-color: black;");

        //Scene setup
        Scene scene = new Scene(root, 1200, 1000);
        primaryStage.setTitle("Battleship - Main Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    

	

	private void createServer(Stage primaryStage) {
		Server server = new Server();
        try {
        	server.start(new Stage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}




	private void openPlayerIU(Stage primaryStage) {
		Player player = new Player();
        try {
        	player.start(new Stage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //primaryStage.close(); //Close the main menu window
	}




	private void openHelpMenu(Stage primaryStage) {
    	HelpUI battleshipGame = new HelpUI();
        try {
            battleshipGame.start(new Stage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        primaryStage.close(); //Close the main menu window
	}

    


	private void singlePlayer(Stage primaryStage) {
		CPU_UI battleshipGame = new CPU_UI();
        try {
            battleshipGame.start(new Stage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        primaryStage.close(); //Close the main menu window
	}
	
	 private TextField createStyledTextField(String promptText) {
	        TextField textField = new TextField();
	        textField.setPromptText(promptText);
	        textField.setFont(Font.font("Monospaced", 18));
	        textField.setStyle(
	                "-fx-background-color: black; " +
	                "-fx-text-fill: green; " +
	                "-fx-prompt-text-fill: green; " +
	                "-fx-border-color: green; -fx-border-width: 2; " +
	                "-fx-font-size: 18px; -fx-padding: 5;"
	        );
	        textField.setMaxWidth(500);
	        return textField;
	    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Monospaced", 18));
        button.setStyle(
                "-fx-background-color: green; " +
                "-fx-text-fill: black; " +
                "-fx-padding: 10 20 10 20; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-border-color: black; -fx-border-width: 2;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #00a300; " + //Brighter green
                "-fx-text-fill: black; " +
                "-fx-padding: 10 20 10 20; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-border-color: black; -fx-border-width: 2;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: green; " +
                "-fx-text-fill: black; " +
                "-fx-padding: 10 20 10 20; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-border-color: black; -fx-border-width: 2;"
        ));
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
