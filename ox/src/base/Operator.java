package base;

import javax.swing.JOptionPane;
import static base.GamePanel.NAUGHT;

class Operator {
	private final Algorithm algorithm = new Algorithm(3, 3, 3);
	
	private GamePanel gamePanel;

	private byte[][] tab;
	
	Operator(int size_rows, int size_columns, int seriesLength, GamePanel gamePanel){
		this.gamePanel = gamePanel;
		if(gamePanel != null)
			tab = gamePanel.getTab();
		
		algorithm.setProperties(seriesLength, size_rows, size_columns);
	}
	
	void setProperties(int size_rows, int size_columns, int seriesLength, GamePanel gamePanel) {
		algorithm.setProperties(seriesLength, size_rows, size_columns);
		this.gamePanel = gamePanel;
		tab = gamePanel.getTab();
	}
	
	void naughtsMove() {
		//board is full
		if(!algorithm.arrayContains(tab, 0)) {
			JOptionPane.showMessageDialog(null, "Draw");
			System.exit(0);
		}
		
		short[] naughtsCoords = algorithm.getOptimalMove(tab);
		
		//if the algorithm fails, it returns an invalid solution
		try {
			tab
			[naughtsCoords[0]]
			[naughtsCoords[1]] = NAUGHT;
		}catch(ArrayIndexOutOfBoundsException e) {
			JOptionPane.showMessageDialog(null,
					"Algorithm error, exiting",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(0);
		}
		
		
		//checking if ai won, after its move
		if(algorithm.checkIfWon(tab, NAUGHT)) {
			JOptionPane.showMessageDialog(null,
					"Owned",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}else
			//board is full
			if(!algorithm.arrayContains(tab, 0)) {
				JOptionPane.showMessageDialog(null, "Draw");
				System.exit(0);
			}
		
		//enabling the player to make the next move
		gamePanel.setTurnIndicator(true);
	}
}