package main;

import java.util.*;

public class TriSolitaire {
	
	public static void main(String args[]) {
		long startTime, runTime;
		Game peggy = new Game(args);
		System.out.println("Your original board:");
		peggy.printBoard();
		System.out.println("Start solving...");
		System.out.println();
		
		switch (Integer.parseInt(args[0])) {
		case 0:
			startTime = System.nanoTime();
			if (peggy.dfidSolve()) {
				runTime = System.nanoTime() - startTime;
				System.out.println("SOLVED!");
				System.out.println("Time required: " + runTime);
				System.out.println("# of nodes visited: " + peggy.nodesVisited());
				System.out.println("Solution path:");
				peggy.reversePath();
				while(!peggy.path.empty()) {
					((Board) peggy.path.pop()).printBoard();
					System.out.println("=============");
				}
			} else {
				System.out.println("Did not find a single peg solution");
			}
			break;
		case 1:
			Board b = new Board();
			startTime = System.nanoTime();
			if (peggy.biDfidSolve()) {
				runTime = System.nanoTime() - startTime;
				System.out.println("SOLVED!");
				System.out.println("Time required: " + runTime);
				System.out.println("# of nodes visited: " + peggy.nodesVisited());
				System.out.println("Solution path:");
				b = peggy.backwardPath.peek();
				if (peggy.qDFSSolve(b)) {
					peggy.reversePath();
					while(!peggy.path.empty()) {
						((Board) peggy.path.pop()).printBoard();
						System.out.println("=============");
					}
				}
				while(!peggy.backwardPath.empty()) {
					b = peggy.backwardPath.pop();
					b.printBoard();
					System.out.println("=============");
				}
			} else {
				System.out.println("Did not find a single peg solution");
			}
			break;
		}
		
	}
}
