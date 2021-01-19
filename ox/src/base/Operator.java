package base;

import static base.GamePanel.NAUGHT;
import static base.GamePanel.CROSS;

import javax.swing.JOptionPane;

import base.GamePanel.Tile;

class Operator {
	private final Algorithm algorithm;
	
	private int rows;
	private int columns;
	private int seriesLength;
	
	private boolean player_sTurnIndicator = true;
	
	private byte[][] throwToByteArray(Tile[][] tab){
		int rows = tab.length, columns = tab[0].length;
		byte[][] byteArray = new byte[rows][columns];
		
		for(int row = 0; row < rows; ++row) {
			for(int column = 0; column < columns; ++column) {
				byteArray[row][column] = tab[row][column].getValue();
			}
		}
		
		return byteArray;
	}
	/**
	 * @return
	 * int[0] - rows<br>
	 * int[1] - columns<br>
	 * int[2] - series length<br>
	 */
	int[] getSettings() {
		int[] array = {rows, columns, seriesLength};
		return array;
	}
		
	Operator(int rows, int columns, int seriesLength, int threadCount, int recursionDepth){
		this.rows = rows;
		this.columns = columns;
		this.seriesLength = seriesLength;
		
		algorithm = new Algorithm(rows, columns, seriesLength, threadCount, recursionDepth);
	}
	
	void playerWantsToMakeAMove(Tile[][] tileArray, GamePanel gamePanel, Tile targetTile){
		if(player_sTurnIndicator == true) 
		{
			player_sTurnIndicator = false;
			if(targetTile.getValue() == 0)
				targetTile.setValue(CROSS);
			
			//if the player won
			byte[][] byteArray = throwToByteArray(tileArray);
			if(algorithm.checkIfWon(byteArray, CROSS)) {
				JOptionPane.showMessageDialog(null,
						"You won",
						"You won",
						JOptionPane.WARNING_MESSAGE);
				System.exit(0);
			}
			//board is full
			if(algorithm.boardArrayIsFull(byteArray)) {
				JOptionPane.showMessageDialog(null, "Draw");
				System.exit(0);
			}
			
			new Thread(() -> {//should be ok, no race conditions?
				performNaughtsMove(byteArray, gamePanel);
				player_sTurnIndicator = true;
			}).start();
		}
	}
	
	//the meat
	private void performNaughtsMove(byte[][] byteArray, GamePanel gamePanel) {
		
		int[] naughtsCoords = algorithm.getOptimalMove(byteArray);
		
		//if the algorithm fails, it returns a null
		if(naughtsCoords != null) {
			gamePanel.placeANaught(naughtsCoords[0], naughtsCoords[1]);

			//updating the local array
			byteArray[naughtsCoords[0]][naughtsCoords[1]] = NAUGHT;
		}else {
			JOptionPane.showMessageDialog(null,
					"Algorithm error, exiting",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		//checking if ai won, after its move
		if(algorithm.checkIfWon(byteArray, NAUGHT)) {
			JOptionPane.showMessageDialog(null,
					"Owned",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}else
			//board is full
			if(algorithm.boardArrayIsFull(byteArray)) {
				JOptionPane.showMessageDialog(null, "Draw");
				System.exit(0);
			}
		
		//enabling the player to make the next move
		player_sTurnIndicator = true;
	}
}