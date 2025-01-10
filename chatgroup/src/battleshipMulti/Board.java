package battleshipMulti;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Board extends GridPane {

    protected Button[][] btnGrid;
    protected boolean[][] disabledGrid;
    
    private int rClick = -1; //Previous clicked row
    private int cClick = -1; //Previous clicked column

    public Board() {
        super();
        
        setAlignment(Pos.CENTER);
        
        btnGrid = new Button[10][10];
        
        disabledGrid = new boolean[10][10];
        
        for (int i = 0; i < btnGrid.length; i++) {
        	for (int j = 0; j < btnGrid.length; j++) {
        		btnGrid[i][j] = new Button("");
			}
        }
        
        for (int i = 0; i < disabledGrid.length; i++) {
        	for (int j = 0; j < disabledGrid.length; j++) {
        		disabledGrid[i][j] = false;
			}
        }
		
			
    }

    public void drawBoard() {
        setVgap(5);
        setHgap(5);

        //Adding column headers
        for (int col = 0; col < 10; col++) {
            Text colLabel = new Text(Integer.toString(col + 1));
            colLabel.setFill(Color.GREEN);
            add(colLabel, col + 1, 0);
        }

        //Adding row headers
        for (int row = 0; row < 10; row++) {
            Text rowLabel = new Text(Character.toString((char) (65 + row))); //'A' to 'J'
            rowLabel.setFill(Color.GREEN);
            add(rowLabel, 0, row + 1);
        }

        //Adding buttons to the grid
        for (int row = 0; row < btnGrid.length; row++) {
            for (int col = 0; col < btnGrid[row].length; col++) {
            	
                Button newButton = createButton(btnGrid[row][col], row, col);
                add(newButton, col + 1, row + 1); //Offset by 1 for headers
            }
        }
    }

    private Button createButton(Button oldButton, int row, int col) {
        Button button = oldButton;
        button.setMinSize(20, 20);
        button.setStyle("-fx-background-color: black; -fx-border-color: green; -fx-border-width: 1px;");
        button.setTooltip(new Tooltip("Row: " + (char) (65 + row) + ", Col: " + (col + 1)));

        button.setOnAction(event -> handleButtonClick(row, col));
        return button;
    }

    public void handleButtonClick(int row, int col) {
        if (rClick != -1 && cClick != -1 && disabledGrid[rClick][cClick] != true) {
            //Reset the previous button to black
            btnGrid[rClick][cClick].setStyle("-fx-background-color: black; -fx-border-color: green; -fx-border-width: 1px;");
        }

        //Highlight the clicked button
        rClick = row;
        cClick = col;
        btnGrid[row][col].setStyle("-fx-background-color: green; -fx-border-color: green; -fx-border-width: 1px;");
    }

    public void placeHitMarker(int row, int col) {
        btnGrid[row][col].setDisable(true);
        disabledGrid[row][col] = true;
        btnGrid[row][col].setStyle("-fx-background-color: #8B0000; -fx-border-color: #8B0000; -fx-border-width: 1px;");
    }

    public void placeMissMarker(int row, int col) {
        btnGrid[row][col].setDisable(true);
        disabledGrid[row][col] = true;
        btnGrid[row][col].setStyle("-fx-background-color: gray; -fx-border-color: gray; -fx-border-width: 1px;");
    }

    public int getRclick() {
        return rClick;
    }

    public int getCclick() {
        return cClick;
    }
}
