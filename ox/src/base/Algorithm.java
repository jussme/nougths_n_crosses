package base;

import static base.GamePanel.CROSS;
import static base.GamePanel.NAUGHT;

public class Algorithm {
	/*
	 * 10 ->
	 * 11 \
	 * 01 |
	 * -11 /
	 * -10 <-
	 * -1-1 \
	 * 0-1 |
	 * 1-1 /
	 *
	*/
	/*An array of vectors for checking win conditions;
	arranged so that -vectors[i] == vectors[7 - i] == vectors[vectors.length - 1 - i] ,
	in other words - it's symmetrical*/
	private final static int[][] vectors = {
		{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}
	};
	
	//boards X size
	private int size_rows;
		
	//boards Y size
	private int size_columns;
	
	private int seriesLength;
	
	
	/**Setting boards properties.
	 * <p>
	 * @param seriesLength is the required amount of consecutive characters for a win;<br>
	 *  <= 3 && <= boardX && <= boardY
	 * @param boardX horizontal size of the board; min 3
	 * @param boardY vertical size of the board; min 3
	 * @return true if the properties have been successfully set, false otherwise
	 * </p>
	 */
	boolean setProperties(int seriesLength, int size_rows, int size_columns) {
		if(seriesLength > size_rows || seriesLength > size_columns || seriesLength < 3
				|| size_rows < 3 || size_columns < 3)
			return false;
		
		this.size_rows = size_rows;
		this.size_columns = size_columns;
		this.seriesLength = seriesLength;
		
		return true;
	}
	
	Algorithm(int seriesLength, int boardX, int boardY){
		setProperties(seriesLength, boardX, boardY);
	}
	
	short[] getOptimalMove(byte[][] tab) {
		float bestSoFar_WinCount = -32768, buff;
		short[] bestSoFar_Coords = {(short) (size_rows + 1), (short) (size_columns + 1)};
		for(short it = 0; it < size_rows; ++it)
			for(short itt = 0; itt < size_columns; ++itt) {
				if(tab[it][itt] == 0) {
					byte[][] tabClone0 = copy(tab, size_rows, size_columns);
					tabClone0[it][itt] = NAUGHT;
					
					if((buff = appraiseABoard_0(tabClone0)) > bestSoFar_WinCount) {
						bestSoFar_WinCount = buff;
						bestSoFar_Coords[0] = it;
						bestSoFar_Coords[1] = itt;
					}
					System.gc();
				}
			}
		
		return bestSoFar_Coords;
	}
	
	private float appraiseABoard_0(byte[][] tab) {
		float amountOfX = 0;
		for(byte[] tab1D : tab)
			for(byte cell : tab1D)
				if(cell == 1)
					++amountOfX;
		
		int scaleFactor = size_rows * size_columns - 1;
		
		if(checkIfDraw(tab)) {
			return 1/((float)Math.pow(scaleFactor, amountOfX));
		}
		if(checkIfWon(tab, NAUGHT)) {
			return 2/((float)Math.pow(scaleFactor, amountOfX));
		}
		if(checkIfWon(tab, CROSS)) {
			return -4/((float)Math.pow(scaleFactor, amountOfX));
		}
		
		float allBoardsScore = 0;
		
		
		short sum = 0;
		byte projection;
		for(byte[] tab1D : tab)
			for(byte cell : tab1D)
				sum += cell;
		//cross's turn?(will be projected(by iterating possibillities))
		projection = (sum == 0)? CROSS : NAUGHT;
		
		for(short it = 0; it < size_rows; ++it)
			for(short itt = 0; itt < size_columns; ++itt) {
				byte[][] tabClone = copy(tab, size_rows, size_columns);
				if(tabClone[it][itt] == 0) {
					tabClone[it][itt] = projection;
					allBoardsScore += appraiseABoard_0(tabClone);
				}
			}
		
		return allBoardsScore;
	}
	
	private boolean checkIfDraw(byte[][] tab) {
		return !arrayContains(tab, 0)
				&& !checkIfWon(tab, NAUGHT)
				&& !checkIfWon(tab, CROSS);
	}
	
	
	boolean checkIfWon(byte[][]tab, int value) {
		//default value is false, which answers "was checked?"
		boolean[][][] checkArray = new boolean[size_rows][size_columns][8];
		
		for(int row = 0; row < size_rows; ++row)
			for(int column = 0; column < size_columns; ++column)
				for(int vectorPair = 0; vectorPair < 8; ++vectorPair)
					if(!checkArray[row][column][vectorPair]) 
					{
						if(checkLine(tab, row, column,
							vectors[vectorPair][0],
							vectors[vectorPair][1], value))
						{
							return true;
						}else {
							try {
								checkArray[row + (seriesLength - 1) * vectors[vectorPair][0]]
										[column + (seriesLength - 1) * vectors[vectorPair][1]][7 - vectorPair]
												= true;
							}catch(ArrayIndexOutOfBoundsException e) {
								//nuffin
							}
						}
					}
		return false;
	}
	
	private boolean checkLine(byte[][] tab, int x, int y,
			int vectorRow, int vectorCol, int value) {
		try{
			for (int it = 0; it < seriesLength; ++it) {
				try{
					if(tab[x + it * vectorRow][y + it * vectorCol] != value)
						return false;
				}catch(ArrayIndexOutOfBoundsException e) {
					return false;
				}
			}
			
			return true;
		}catch(ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			return false;
		}
	}
 	
	private byte[][] copy(byte[][] tab, int count_rows, int count_columns){
		byte[][] returnTab = new byte[count_rows][count_columns];
		for(int it = 0; it < count_rows; ++it)
			for(int itt = 0; itt < count_columns; ++itt) {
				returnTab[it][itt] = tab[it][itt];
			}
		
		return returnTab;
	}
	
	boolean arrayContains(byte[][] tab, int value) {
		boolean contains = false;
		for(byte[] tab1D : tab)
			for(byte cell : tab1D)
				if(cell == value)
					contains = true;
		
		return contains;
	}
}