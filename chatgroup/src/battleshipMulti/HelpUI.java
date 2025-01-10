package battleshipMulti;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class HelpUI extends Application{
	
	private TextArea txaHelpInfo;
	
	private Parent createContent(Stage primaryStage) {
		primaryStage.setWidth(900);
	    primaryStage.setHeight(900);

	    BorderPane root = new BorderPane();
	    root.setStyle("-fx-background-color: black;");
	    
        //Title label
        Label lblStudio = new Label("Help");
        lblStudio.setTextFill(Color.LIME);
        lblStudio.setFont(Font.font("Monospaced", 30));
        BorderPane.setMargin(lblStudio, new Insets(20, 0, 0, 0));
        lblStudio.setPadding(new Insets(10));

        //Help text area
        txaHelpInfo = new TextArea();
        try {
			txaHelpInfo.setText(loadHelpText("data/help.txt"));
		} catch (IOException e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
        txaHelpInfo.setFont(Font.font("Monospaced", 14));
        txaHelpInfo.setStyle("-fx-text-fill: lime; -fx-control-inner-background: black;");
        txaHelpInfo.setWrapText(true);
        txaHelpInfo.setEditable(false);

        //Close button
        Button btnClose = new Button("Return");
        btnClose.setFont(Font.font("Monospaced", 12));
        btnClose.setStyle("-fx-text-fill: black; -fx-background-color: green;");
        btnClose.setOnAction(e -> returnToMenu(primaryStage));

        //Layout
        BorderPane.setMargin(btnClose, new Insets(10));
        root.setTop(lblStudio);
        root.setCenter(txaHelpInfo);
        root.setBottom(btnClose);
	    
	    
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

	private String loadHelpText(String fileName) throws IOException {
        StringBuilder helpText = new StringBuilder();
        try (BufferedReader input = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = input.readLine()) != null) {
                helpText.append(line).append("\n");
            }
        }
        return helpText.toString();
    }
	
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(createContent(primaryStage));
        primaryStage.setTitle("Battleship - Help");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
	}
	
	public static void main(String[] args) {
        launch(args);
    }

}
