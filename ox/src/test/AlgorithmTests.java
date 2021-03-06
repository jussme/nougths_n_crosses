/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package test;

import org.junit.Test;

import base.Algorithm;

public class AlgorithmTests {
     @Test 
     public void testGetOptimalMove() {
        Algorithm testAlgorithm = new Algorithm(4, 4, 4, 4, 6);
        byte[][] testTab = {
        		{1, 0, 0, 0},
        		{0, 0, 0, 0},
        		{0, 0, 0, 0},
        		{0, 0, 0, 0},
        	};
        
        long endTime;
        int numberOfRuns = 100;
        long[] timeArray = new long[numberOfRuns];
        for(int it = 0; it<numberOfRuns; ++it) {
        	long startTime = System.nanoTime();
            testAlgorithm.getOptimalMove(testTab);
            endTime = System.nanoTime();
            timeArray[it] = endTime - startTime;
        }
        
        long avgTime = 0;
        for(long time : timeArray) {
        	avgTime += time;
        }
        avgTime /= numberOfRuns;
        System.out.println("-----\ntime: "+avgTime/1000000000f + "\n-----");
    }
    
    //@Test 
    public void testAppraiseABoard() {
    	byte[][] testTab = {
        		{-1, -1, 1, -1},
        		{-1, 1, 1, -1},
        		{0, 1, 1, 0},
        		{1, 0, 0, 0},
        	};
    }
}
