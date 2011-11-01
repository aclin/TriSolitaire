package main;

import java.io.*;
import java.util.*;

public class Game {
	
	Board startBoard;
	Board goalBoard;
	
	int goalRow, goalCol;
	
	Stack<Board> path = new Stack<Board>();
	Stack<Board> forwardPath = new Stack<Board>();
	Stack<Board> backwardPath = new Stack<Board>();
	Stack<Board> halfway = new Stack<Board>();
	
	int cutOffDepth;
	
	public Game(String[] rootInput) {
		try{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream(rootInput[0]);
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
			if (rootInput.length > 1 && rootInput.length < 4) {
				try {
					goalRow = Integer.parseInt(rootInput[1]);
					if (goalRow > d - 1 || goalRow < 0) {
						System.out.println("Peg position outside of board");
						System.exit(1);
					}
					goalCol = Integer.parseInt(rootInput[2]);
					if (goalCol > goalRow || goalCol < 0) {
						System.out.println("Peg position outside of board");
						System.exit(1);
					}
					goalBoard = new Board(goalRow, goalCol, d);
				} catch (NumberFormatException nfe) {
					System.err.println("Error: Goal peg position not a number");
					System.err.println("Error: " + nfe.getMessage());
				}
			}
			
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
	}
	
	public boolean recSolve() {
		
		// Calls recursive Depth-First Search
		if (dfs(startBoard)) {
			path.push(startBoard);
			return true;
		} else {
			return false;
		}
	}
	
	// Recursive DFS
	private boolean dfs(Board node) {
		Board b;
		// Check if the current node (board) is the goal state (one peg left)
		if (node.isGoal(goalRow, goalCol))
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
	
	// Iterative Depth-First Search (NOT Iterative Deepening)
	public boolean dfsSolve() {
		Board parent;
		Board child;
		
		path.push(startBoard);
		startBoard.findValidForwardMoves();
		
		while (!path.isEmpty()) {
			parent = (Board) path.pop();
			if (parent.hasNextMove()) {
				child = parent.nextMove();
				if (child.isGoal(goalRow, goalCol)){
					path.push(parent);
					path.push(child);
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
	
	// Depth-First Iterative Deepening Search with a limit
	public boolean dfidSolve() {
		int limit = 0;
		while (limit < cutOffDepth) {
			if (dfid(limit))
				return true;
			startBoard.clearForwardMovesList();
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
				//System.out.println("Length greater than limit");
				//System.out.println("length: " + length + " Limit: " + limit);
				length -= 2;
				continue;
			}
			if (parent.hasNextMove()) {
				//System.out.println("Number of valid moves: " + parent.validMovesCount());
				child = parent.nextMove();
				length++;
				if (child.isGoal(goalRow, goalCol)){
					path.push(parent);
					path.push(child);
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
	
	// Bi-directional Depth-First Iterative Deepening Search
	public boolean biDfidSolve() {
		int limit = 0;
		Board interim;
		while (limit < cutOffDepth) {
			if (biDfid(goalBoard, true, limit))
				return true;
			while (!halfway.isEmpty()) {
				interim = halfway.pop();
				if (biDfid(interim, false, limit))
					return true;
				if (biDfid(interim, false, limit+1))
					return true;
			}
			
			startBoard.clearForwardMovesList();
			limit++;
		}
		return false;
	}
	
	public boolean biDfid(Board goal, boolean forward, int limit) {
		Board parent;
		Board child;
		
		int length = 0;
		
		if (forward) {						// We're searching forward
			forwardPath.push(startBoard);
			startBoard.findValidForwardMoves();
			while (!forwardPath.isEmpty()) {
				parent = forwardPath.pop();
				if (length > limit) {
					length -= 2;
					halfway.push(parent);
					System.out.println("forward");
					continue;
				}
				if (parent.hasNextMove()) {
					child = parent.nextMove();
					length++;
					if (child.isGoal(goalRow, goalCol)){
						forwardPath.push(parent);
						forwardPath.push(child);
						return true;
					}
					if (forwardPath.search(child) != -1)
						continue;
					
					forwardPath.push(parent);
					forwardPath.push(child);
					child.findValidForwardMoves();
				} else {
					length--;
				}
			}
		} else {							// We're searching backward
			backwardPath.push(goalBoard);
			goalBoard.findValidBackwardMoves();
			while (!backwardPath.isEmpty()) {
				parent = backwardPath.pop();
				System.out.println("backward");
				if (length > limit) {
					length = length - 2;
					continue;
				}
				if (parent.hasPrevMove()) {
					child = parent.prevMove();
					length++;
					if (child.isGoal(goal)){
						backwardPath.push(parent);
						backwardPath.push(child);
						return true;
					}
					if (backwardPath.search(child) != -1)
						continue;
					
					backwardPath.push(parent);
					backwardPath.push(child);
					child.findValidBackwardMoves();
				} else {
					length--;
				}
			}
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
	
	public void reversePath() {
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
