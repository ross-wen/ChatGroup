package battleshipMulti;
	
public class CPU {
	
	private PlayerBoard cpuBoard;
	private boolean hasShot[][];
	
	public CPU() {
		cpuBoard = new PlayerBoard();
		hasShot = new boolean[10][10];
		
		placeShips();
		
	}
	
	public void placeShips() {
		while (true) {
			int row = (int)(Math.random()*10);
			int col = (int)(Math.random()*10);
			boolean isVertical = false;
			
			if (Math.random()*10 > 5) {
				isVertical = true;
			}
			
			cpuBoard.setIsVertical(isVertical);
			cpuBoard.placeShip(row, col);
			
			if (cpuBoard.getAllShipsPlaced()) {
				break;
			}
		}
	}
	
	public int[] shoot() {
		int row = 0;
		int col = 0;
		
		while (true) {
			row = (int)(Math.random()*10);
			col = (int)(Math.random()*10);
			
			if (!hasShot[row][col]) {
				break;
			}
		}
		return new int[] {row, col};
	}
	
	public boolean checkHit(int row, int col) {
		return cpuBoard.checkEnemyShot(row, col);
	}
	
	public boolean hasLost() {
		return cpuBoard.hasLost();
	}

}
