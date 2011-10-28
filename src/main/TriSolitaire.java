package main;

import java.util.*;

public class TriSolitaire {
	
	public static void main(String args[]) {
		long startTime, runTime;
		Game peggy = new Game(args);
		System.out.println("Your original board:");
		peggy.printBoard();
		System.out.println();
		//peggy.printValidMoves();
		
		startTime = System.nanoTime();
		//if (peggy.dfsSolve()) {
		if (peggy.dfidSolve()) {
			runTime = System.nanoTime() - startTime;
			System.out.println("SOLVED!");
			System.out.println("Time required: " + runTime);
			System.out.println("Solution path:");
			peggy.reversePath();
			while(!peggy.path.empty()) {
				((Board) peggy.path.pop()).printBoard();
				System.out.println("=============");
			}
		} else {
			System.out.println("Did not find a single peg solution");
		}
	}
}
