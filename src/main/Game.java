package main;

import java.io.*;
import java.util.*;

public class Game {
	
	Board startBoard;
	Stack<Board> path = new Stack<Board>();
	
	int cutOffDepth;
	
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
			setCutOffDepth(d);
			startBoard = new Board(root, d);
			
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
	}
	
	/*
	public boolean solve() {
		
		// Calls recursive Depth-First Search
		if (dfs(startBoard)) {
			path.push(startBoard);
			return true;
		} else {
			return false;
		}
	}
	*/
	
	// Iterative Depth-First Search
	public boolean solve() {
		Board parent;
		Board child;
		
		path.push(startBoard);
		startBoard.findValidForwardMoves();
		
		while (!path.isEmpty()) {
			parent = (Board) path.pop();
			if (parent.hasNextMove()) {
				child = parent.nextMove();
				if (child.isGoal()){
					path.push(parent);
					path.push(child);
					reversePath();
					return true;
				}
				if (path.search(child) != -1)
					continue;
				path.push(parent);
				path.push(child);
				child.findValidForwardMoves();
			}
		}
		return false;
	}
	
	// Iterative Depth-First Search with a limit
	public boolean dfidSolve() {
		int limit = 0;
		while (limit < cutOffDepth) {
			if (dfid(limit))
				return true;
			startBoard.clearMovesList();
			limit++;
		}
		return false;
	}
	public boolean dfid(int limit) {
		Board parent;
		Board child;
		
		int length = 0;
		
		path.push(startBoard);
		startBoard.findValidForwardMoves();
		
		while (!path.isEmpty()) {
			parent = (Board) path.pop();
			if (length > limit) {
				System.out.println("Length greater than limit");
				System.out.println("length: " + length + " Limit: " + limit);
				continue;
			}
			if (parent.hasNextMove()) {
				System.out.println("Number of valid moves: " + parent.validMovesCount());
				child = parent.nextMove();
				length++;
				if (child.isGoal()){
					path.push(parent);
					path.push(child);
					reversePath();
					return true;
				}
				if (path.search(child) != -1)
					continue;
				
				path.push(parent);
				path.push(child);
				child.findValidForwardMoves();
			} else {
				length--;
			}
		}
		return false;
	}
	
	// Recursive DFS
	private boolean dfs(Board node) {
		Board b;
		// Check if the current node (board) is the goal state (one peg left)
		if (node.isGoal())
			return true;
		// Check if the current node (board) has any children (has any moves available)
		if (!node.findValidForwardMoves())
			return false;
		while (node.hasNextMove()) {
			// While current node (board) has one child (available move)
			b = node.nextMove();
			// Do DFS on the subtree with the child as root
			if (dfs(b)) {
				// Put this node on the path if a depth-first search of it returns a goal state
				path.push(b);
				return true;
			}
			// If DFS on the subtree returns no goal state, ignore this node, and continue with the next child node
		}
		return false;
	}
	
	private void setCutOffDepth(int d) {
		cutOffDepth = 0;
		for (int i=1; i<=d; i++) {
			cutOffDepth += i;
		}
		
		//cutOffDepth -= 2;
	}
	
	private void reversePath() {
		Stack<Board> tmp = new Stack<Board>();
		while (!path.empty()) {
			// Move everything in path to tmp in reverse order
			tmp.push((Board) path.pop());
		}
		path = tmp;
	}
	
	public void printValidMoves() {
		startBoard.printValidMoves();
	}
	
	public void printBoard() {
		startBoard.printBoard();
	}
}
