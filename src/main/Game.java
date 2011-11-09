package main;

import java.io.*;
import java.util.*;

public class Game {
	
	Board startBoard;
	Board goalBoard;
	
	int goalRow, goalCol;
	int nodeCount = 0;
	
	Hashtable<String, Board> visited = new Hashtable<String, Board>();
	Stack<Board> path = new Stack<Board>();
	Stack<Board> forwardPath = new Stack<Board>();
	Stack<Board> backwardPath = new Stack<Board>();
	Stack<Board> halfway = new Stack<Board>();
	
	int cutOffDepth;
	
	public Game(String[] rootInput) {
		try{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream(rootInput[1]);
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
			if (rootInput.length > 2 && rootInput.length < 5) {
				try {
					goalRow = Integer.parseInt(rootInput[2]);
					if (goalRow > d - 1 || goalRow < 0) {
						System.out.println("Peg position outside of board");
						System.exit(1);
					}
					goalCol = Integer.parseInt(rootInput[3]);
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
		visited.put(startBoard.hash(), startBoard);
		startBoard.findValidForwardMoves();
		
		while (!path.isEmpty()) {
			parent = (Board) path.pop();
			if (parent.hasNextMove()) {
				child = parent.nextMove();
				nodeCount++;
				if (child.isGoal(goalRow, goalCol)){
					path.push(parent);
					path.push(child);
					return true;
				}
				
				path.push(parent);
				if (visited.containsKey(child.hash()))
					continue;
				
				path.push(child);
				visited.put(child.hash(), child);
				child.findValidForwardMoves();
			}
		}
		return false;
	}
	
	// Depth-First Iterative Deepening Search with a limit
	public boolean dfidSolve() {
		int limit = 1;
		while (limit < cutOffDepth) {
			if (dfid(limit))
				return true;
			startBoard.clearForwardMovesList();
			limit++;
			nodeCount = 0;
			visited.clear();
		}
		return false;
	}
	
	public boolean dfid(int limit) {
		Board parent;
		Board child;
		
		int length = 0;
		path.push(startBoard);
		visited.put(startBoard.hash(), startBoard);
		startBoard.findValidForwardMoves();
		startBoard.symmetricPrune();
		
		while (!path.isEmpty()) {
			parent = path.pop();
			
			if (length > limit) {
				length--;
				continue;
			}
			
			if (parent.hasNextMove()) {
				child = parent.nextMove();
				length++;
				nodeCount++;
				if (child.isGoal(goalRow, goalCol)){
					path.push(parent);
					path.push(child);
					return true;
				}
				
				path.push(parent);
				if (visited.containsKey(child.hash())) {
					length--;
					continue;
				}
				
				
				path.push(child);
				visited.put(child.hash(), child);
				if (length != limit)
					child.findValidForwardMoves();
			} else {
				length--;
			}
		}
		return false;
	}
	
	public boolean biDfidSolve2() {
		int limit = 1;
		while (limit < cutOffDepth) {
			if (biDfid2(limit))
				return true;
			
			startBoard.clearForwardMovesList();
			goalBoard.clearBackwardMovesList();
			limit++;
			nodeCount = 0;
			visited.clear();
		}
		return false;
	}
	
	public boolean biDfid2(int limit) {
		Board parent;
		Board child;
		
		int length = 0;
		
		forwardPath.push(startBoard);
		visited.put(startBoard.hash(), startBoard);
		startBoard.findValidForwardMoves();
		startBoard.symmetricPrune();
		
		while (!forwardPath.isEmpty()) {
			parent = forwardPath.pop();
			
			if (length > limit) {
				length--;
				continue;
			}
			if (parent.hasNextMove()) {
				child = parent.nextMove();
				length++;
				nodeCount++;
				if (child.isGoal(goalRow, goalCol)){
					forwardPath.push(parent);
					forwardPath.push(child);
					return true;
				}
				
				forwardPath.push(parent);
				if (visited.containsKey(child.hash())) {
					length--;
					continue;
				}
				
				forwardPath.push(child);
				visited.put(child.hash(), child);
				if (length == limit) {
					if (backwardDfid(child, limit))
						return true;
					if (backwardDfid(child, limit+1))
						return true;
				} else {
					child.findValidForwardMoves();
				}
			} else {
				length--;
			}
		}
		
		return false;
	}
	
	public boolean backwardDfid(Board goal, int limit) {
		Board parent;
		Board child;
		
		int length = 0;
		path.push(goalBoard);
		visited.put(goalBoard.hash(), goalBoard);
		goalBoard.findValidForwardMoves();
		
		while (!backwardPath.isEmpty()) {
			parent = backwardPath.pop();
			
			if (length > limit) {
				length--;
				continue;
			}
			
			if (parent.hasNextMove()) {
				child = parent.nextMove();
				length++;
				nodeCount++;
				if (child.isGoal(goal)){
					backwardPath.push(parent);
					backwardPath.push(child);
					return true;
				}
				
				backwardPath.push(parent);
				if (visited.containsKey(child.hash())) {
					length--;
					continue;
				}
				
				backwardPath.push(child);
				visited.put(child.hash(), child);
				if (length != limit)
					child.findValidForwardMoves();
			} else {
				length--;
			}
		}
		return false;
	}
	
	private void setCutOffDepth(int d) {
		cutOffDepth = 0;
		for (int i=1; i<=d; i++) {
			cutOffDepth += i;
		}
	}
	
	private boolean isInPath(Board b) {
		Stack<Board> s = path;
		while (!s.isEmpty()) {
			if (s.pop().equals(b))
				return true;
		}
		return false;
	}
	
	public void reversePath() {
		Stack<Board> tmp = new Stack<Board>();
		while (!path.empty()) {
			// Move everything in path to tmp in reverse order
			tmp.push((Board) path.pop());
		}
		path = tmp;
	}
	
	public void reverseForwardPath() {
		Stack<Board> tmp = new Stack<Board>();
		while (!forwardPath.empty()) {
			// Move everything in path to tmp in reverse order
			tmp.push(forwardPath.pop());
		}
		forwardPath = tmp;
	}
	
	public void reverseBackwardPath() {
		Stack<Board> tmp = new Stack<Board>();
		while (!backwardPath.empty()) {
			// Move everything in path to tmp in reverse order
			tmp.push(backwardPath.pop());
		}
		backwardPath = tmp;
	}
	
	public void printValidMoves() {
		startBoard.printValidMoves();
	}
	
	public void printBoard() {
		startBoard.printBoard();
	}
	
	public int nodesVisited() {
		return nodeCount;
	}
}
