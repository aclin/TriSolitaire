package main;

import java.io.*;
import java.util.*;

public class Game {
	
	Board startBoard;
	Stack<Board> path = new Stack<Board>();
	
	public Game(String rootInput) {
		try{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream(rootInput);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			String root = "";
			int d = 0;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				//System.out.println (strLine);
				// Form a single string from contents of the input file
				root = root.concat(strLine);
				d++;
			}
			//Close the input stream
			in.close();
			
			startBoard = new Board(root, d);
			
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
	}
	
	public boolean solve() {
		Board parent;
		Board child;
		path.push(startBoard);
		//startBoard.findValidForwardMoves();
		
		while (!path.isEmpty()) {
			parent = (Board) path.peek();
			parent.findValidForwardMoves();
			child = parent.nextMove();
			if (child.isGoal())
				return true;
			if (path.search(child) != -1)
				continue;
			path.push(child);
			child.findValidForwardMoves();
		}
		
		return false;
		
	}
	
	public void printValidMoves() {
		startBoard.printValidMoves();
	}
	
	public void printBoard() {
		startBoard.printBoard();
	}
}
