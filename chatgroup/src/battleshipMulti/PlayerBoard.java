package battleshipMulti;

public class PlayerBoard extends Board {

    private Ship[] ships;
    private int hp;
    private int index;
    private boolean isVertical;
    private boolean shipsPlaced;

    public PlayerBoard() {
        super();

        this.ships = new Ship[5];
        ships[0] = new Ship("Carrier", 5);
        ships[1] = new Ship("Battleship", 4);
        ships[2] = new Ship("Cruiser", 3);
        ships[3] = new Ship("Submarine", 3);
        ships[4] = new Ship("Destroyer", 2);

        this.hp = 17; //Total health points for all ships
        this.index = 0;
        this.isVertical = false;
        this.shipsPlaced = false;
    }

    public void placeShip(int row, int col) {
        boolean fits = true;

        try {
            //Check if the ship fits within bounds and doesn't overlap existing ships
            for (int i = 0; i < ships[index].getLength(); i++) {
                if (isVertical) {
                    if (btnGrid[row + i][col].getText().equals(" ")) {
                        fits = false;
                        break;
                    }
                } else {
                    if (btnGrid[row][col + i].getText().equals(" ")) {
                        fits = false;
                        break;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            fits = false; //If ship placement goes out of bounds
        }

        if (fits) {
            //Place the ship on the grid
            ships[index].setPos(row, col, isVertical);
            for (int i = 0; i < ships[index].getLength(); i++) {
                if (isVertical) {
                    btnGrid[row + i][col].setStyle("-fx-background-color: green; -fx-border-color: green; -fx-border-width: 1px;");
                    btnGrid[row + i][col].setText(" ");
                } else {
                    btnGrid[row][col + i].setStyle("-fx-background-color: green; -fx-border-color: green; -fx-border-width: 1px;");
                    btnGrid[row][col + i].setText(" ");
                }
            }

            index++; //Move to the next ship

            if (index == ships.length) {
                shipsPlaced = true; //All ships placed
                disableBoard();
            }
        }
    }

    private void disableBoard() {
        //Disable all buttons once ships are placed
        for (int row = 0; row < btnGrid.length; row++) {
            for (int col = 0; col < btnGrid[row].length; col++) {
                btnGrid[row][col].setDisable(true);
                btnGrid[row][col].setOpacity(1);
            }
        }
    }

    public boolean checkEnemyShot(int row, int col) {
        for (Ship ship : ships) {
            if (ship.checkHit(row, col)) {
                hp--;
                placeHitMarker(row, col);
                return true; //Hit
            }
        }
        placeMissMarker(row, col);
        return false; //Miss
    }

    public boolean hasLost() {
        return hp == 0;
    }

    public void setIsVertical(boolean isVertical) {
        this.isVertical = isVertical;
    }

    public boolean getIsVertical() {
        return isVertical;
    }

    public boolean getAllShipsPlaced() {
        return shipsPlaced;
    }

    public void setHP(int hp) {
        this.hp = hp;
    }

    @Override
    public void handleButtonClick(int r, int c) {
    	for (int row = 0; row < btnGrid.length; row++) {
			for (int col = 0; col < btnGrid.length; col++) {
				if (btnGrid[row][col] == btnGrid[r][c]) {
					placeShip(row, col);
				}
			}
		}
    }
}
