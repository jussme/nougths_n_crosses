package base;

import static base.GamePanel.CROSS;
import static base.GamePanel.NAUGHT;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Algorithm {
	/* checkIfWon method implementation
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
	/*An array[8] of vectors for checking win conditions;
	arranged so that -vectors[i] == vectors[7 - i] == vectors[vectors.length - 1 - i],
	in other words - it's symmetrical*/
	private final static int[][] vectors = {
		{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}
	};
	
	//boards X size
	private final int size_rows;
		
	//boards Y size
	private final int size_columns;
	
	//how many hits in a line are a win condition
	private final int seriesLength;

	//the most edges the alg. will spawn, on one level - the first ai's move.
	private final int maxEdgeCount;
	
	private final int threadCount;
	
	private final int depth;
	
	Algorithm(int size_rows, int size_columns, int seriesLength,
			int threadCount, int depth){
		this.size_rows = size_rows;
		this.size_columns = size_columns;
		this.seriesLength = seriesLength;
		this.threadCount = threadCount;
		this.depth = depth;
		
		maxEdgeCount = size_rows * size_columns - 1;
	}
	
	int[] getOptimalMove(byte[][] tab) {
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		List<Future<Float[]>> futureList = new ArrayList<>();
		
		//multithreaded search for the best move
		for(short row = 0; row < size_rows; ++row)
			for(short column = 0; column < size_columns; ++column)
				//if(cell empty)
				if(tab[row][column] == 0)
				{
					//make new edge
					byte[][] tabClone0 = copy(tab, size_rows, size_columns);
					//define node
					tabClone0[row][column] = NAUGHT;
					
					Float final_row = (float) row, final_column = (float) column;
					
					futureList.add(executorService.submit(() -> {
						//get a float score for the node
						float buff = appraiseABoard(tabClone0, depth);
						
						Float[] array = {buff, final_row, final_column};
						
						System.gc();
						
						return array;
					}));
				}
		
		//picking the best move based on scores(results[0])
		float bestSoFar_Score = -1 * 2^128;
		int[] bestSoFar_Coords = {-1, -1};
		for(Future<Float[]> future : futureList) {
			try {
				Float[] results = future.get();
				if(results[0] > bestSoFar_Score) {
					bestSoFar_Score = results[0];
					bestSoFar_Coords[0] = results[1].intValue();
					bestSoFar_Coords[1] = results[2].intValue();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		//check whether any one score calculation succeeded.
		if(bestSoFar_Coords[0] == -1)
			return null;
		else
			return bestSoFar_Coords;
	}
	
	private float appraiseABoard(byte[][] tab, float depth) {
		
		int currentBranchCount = 0; 
		for(byte[] array1D : tab)
			for(byte cell : array1D)
				if(cell == 0)
					++currentBranchCount;
		++currentBranchCount;//one branch is already projected - the current one
		
		if(checkIfDraw(tab)) {
			return 1/getFactorialRatio(currentBranchCount);
		}
		if(checkIfWon(tab, NAUGHT)) {
			return 2/getFactorialRatio(currentBranchCount);
		}
		if(checkIfWon(tab, CROSS)) {
			return -3/getFactorialRatio(currentBranchCount);
		}
		if(--depth == 0)
			return 0;
		
		//counting moves
		int sum = 0;
		byte projection;
		for(byte[] tab1D : tab)
			for(byte cell : tab1D)
				sum += cell;
		//who's move to project?
		projection = (sum == 0)? CROSS : NAUGHT;

		float allUnderlyingBoardsScore = 0;
		
		for(short row = 0; row < size_rows; ++row)
			for(short column = 0; column < size_columns; ++column) {
				byte[][] tabClone = copy(tab, size_rows, size_columns);
				if(tabClone[row][column] == 0) {
					tabClone[row][column] = projection;
					allUnderlyingBoardsScore += appraiseABoard(tabClone, depth);
				}
			}
		
		return allUnderlyingBoardsScore;
	}
	
	private boolean checkIfDraw(byte[][] tab) {
		return !arrayContains(tab, 0)
				&& !checkIfWon(tab, NAUGHT)
				&& !checkIfWon(tab, CROSS);
	}
	
	
	boolean checkIfWon(byte[][]tab, int value) {
		//default value is false, which answers "was this line checked?"
		boolean[][][] checkArray = new boolean[size_rows][size_columns][8];
		
		for(int row = 0; row < size_rows; ++row)
			for(int column = 0; column < size_columns; ++column)
				for(int vectorPair = 0; vectorPair < 8; ++vectorPair)
					if(!checkArray[row][column][vectorPair]) 
					{
						if(checkLine(tab, row, column,
							vectors[vectorPair][0],
							vectors[vectorPair][1],
							value))
						{
							return true;
						}else {
							//set the other end of the vector as checked
							try {
								checkArray[row + (seriesLength - 1) * vectors[vectorPair][0]]
										[column + (seriesLength - 1) * vectors[vectorPair][1]][7 - vectorPair]
												= true;
							}catch(ArrayIndexOutOfBoundsException e) {
								//when checkLine() was checking "beyond the board"
							}
						}
					}
		return false;
	}
	
	private boolean checkLine(byte[][] tab, int x, int y,
			int vectorX, int vectorY, int value) {
		try{
			for (int it = 0; it < seriesLength; ++it) {
				try{
					if(tab[x + it * vectorX][y + it * vectorY] != value)
						return false;
				}catch(ArrayIndexOutOfBoundsException e) {
					return false;
				}
			}
			
			return true;
		}catch(ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			System.exit(1);
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
		for(byte[] tab1D : tab)
			for(byte cell : tab1D)
				if(cell == value)
					return true;
		
		return false;
	}
	
	/**
	 * 
	 * @param number number of current parallel branches
	 * @return maxBranchCount! / currentBranchCount!
	 */
	private float getFactorialRatio(int number) {
		float result = maxEdgeCount;
		for(int it = maxEdgeCount - 1; it > number; --it) {
			result *= it;
		}
		return result;
	}
}