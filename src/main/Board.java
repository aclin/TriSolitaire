package main;

import java.util.*;

/*
 * The game board is represented by boolean[depth][depth]
 * The indices look like the following:
 * 				(0,0)
 * 			(1,0)	(1,1)
 *		(2,0)	(2,1)	(2,2)
 *	(3,0)	(3,1)	(3,2)	(3,3)
 * (and so on...)
 */


public class Board {
	
	boolean[][] myBoard;
	int depth;
	Vector<Board> validForwardMoves = new Vector<Board>();
	Vector<Board> validBackwardMoves = new Vector<Board>();
	
	int currentRow, currentCol;
	boolean dirNE = false;
	boolean dirE = false;
	boolean dirSE = false;
	boolean dirSW = false;
	boolean dirW = false;
	boolean dirNW = false;
	Board nextMove;
	Stack<Integer> emptySlots = new Stack<Integer>();
	
	public Board() {
		
	}
	
	public Board(Board other) {
		depth = other.depth;
		myBoard = new boolean[depth][depth];
		for (int i=0; i<depth; i++) {
			for (int j=0; j<i+1; j++)
				myBoard[i][j] = other.myBoard[i][j];
		}
	}
	
	public Board(int gRow, int gCol, int d) {
		myBoard = new boolean[d][d];
		for (int i=0; i < d; i++)
			for (int j=0; j<i+1; j++)
				myBoard[i][j] = false;
		myBoard[gRow][gCol] = true;
		depth = d;
	}
	
	public Board(String root, int d) {
		int row = 0;
		myBoard = new boolean[d][d];
		for (int i=0; i<d; i++) {
			row += i;
			// Fill myBoard with the appropriate bits
			for (int j=0; j<i+1; j++) {
				myBoard[i][j] = root.charAt(row+j) == '1';
			}
		}
		depth = d;
	}
	
	public Board nextMove() {
		return validForwardMoves.remove(validForwardMoves.size()-1);
	}
	
	public Board prevMove() {
		return validBackwardMoves.remove(validBackwardMoves.size() - 1);
	}
	
	public boolean hasNextMove() {
		return validForwardMoves.size() > 0;
	}
	
	public boolean hasNextMove2() {
		if (emptySlots.isEmpty())
			return false;
		currentRow = emptySlots.pop();
		currentCol = emptySlots.pop();
		if (!(currentRow-2 < 0 || currentCol > currentRow-2)) {				// Check that NE move is not outside of the board
			if (myBoard[currentRow-1][currentCol]) {		// Check NE adjacent peg exists for move to exist
				if (myBoard[currentRow-2][currentCol]) {
					nextMove = new Board(this).forwardMove(currentRow-2, currentCol, currentRow, currentCol);
					validForwardMoves.add(new Board(this).forwardMove(currentRow-2, currentCol, currentRow, currentCol));
				}
			}
		}
		if (!(j+2 > i)) {				// Check the E move is not outside of the board
			if (myBoard[i][j+1]){		// Check E adjacent peg exists for move to exist
				if (myBoard[i][j+2]) {
					validForwardMoves.add(new Board(this).forwardMove(i, j+2, i, j));
				}
			}
		}
		if (!(i+2 > depth-1 || j+2 > i+2)) {	// Check the SE move is not outside of the board
			if (myBoard[i+1][j+1]){			// Check SE adjacent peg exists for move to exist
				if (myBoard[i+2][j+2]) {
					validForwardMoves.add(new Board(this).forwardMove(i+2, j+2, i, j));
				}
			}
		}
		if (!(i+2 > depth-1)) {			// Check the SW move is not outside of the board
			if (myBoard[i+1][j]) {		// Check SW adjacent peg exists for move to exist
				if (myBoard[i+2][j]) {
					validForwardMoves.add(new Board(this).forwardMove(i+2, j, i, j));
				}
			}
		}
		if (!(j-2 < 0)) {				// Check the W move is not outside of the board
			if (myBoard[i][j-1]){		// Check W adjacent peg exists for move to exist
				if (myBoard[i][j-2]) {
					validForwardMoves.add(new Board(this).forwardMove(i, j-2, i, j));
				}
			}
		}
		if (!(i-2 < 0 || j-2 < 0)) {	// Check the NW move is not outside of the board
			if (myBoard[i-1][j-1]){		// Check NW adjacent peg exists for move to exist
				if (myBoard[i-2][j-2]) {
					validForwardMoves.add(new Board(this).forwardMove(i-2, j-2, i, j));
				}
			}
		}
	}
	
	public boolean hasPrevMove() {
		return validBackwardMoves.size() > 0;
	}
	
	public boolean findValidForwardMoves() {
		// Pegs in the corners only have 2 possible moves, so they are easy to check for first
		// CORNER MOVES START
		// Start with the top corner
		if (!myBoard[0][0]) {				// no peg exists at the top-most position
			if (myBoard[1][0]) {
				if (myBoard[2][0]) {		// 2 successively adjacent pegs must exist for move to exist
					// We move the peg from (2,0) to (0,0)
					validForwardMoves.add(new Board(this).forwardMove(2, 0, 0, 0));
				}
			}
			if (myBoard[1][1]) {			// check if the next adjacent peg exists
				if (myBoard[2][2]) {
					// We move the peg from (2,2) to (0,0)
					validForwardMoves.add(new Board(this).forwardMove(2, 2, 0, 0));
				}
			}
		}
		
		// Then the left corner
		if (!myBoard[depth-1][0]) {			// No peg exists at the left corner
			if (myBoard[depth-2][0]) {		// depth-1 in the first line to account for zero index
				if (myBoard[depth-3][0]) {
					// We move the peg from (depth-3,0) to (depth-1,0)
					validForwardMoves.add(new Board(this).forwardMove(depth-3, 0, depth-1, 0));
				}
			}
			if (myBoard[depth-1][1]) {
				if (myBoard[depth-1][2]) {
					// We move the peg from (depth-1,2) to (depth-1,0)
					validForwardMoves.add(new Board(this).forwardMove(depth-1, 2, depth-1, 0));
				}
			}
		}
		
		// Now the right corner
		if (!myBoard[depth-1][depth-1]) {			// No peg exists at the right corner
			if (myBoard[depth-2][depth-2]) {		// depth-1 in the first line to account for zero index
				if (myBoard[depth-3][depth-3]) {
					// We move the peg from (depth-3,depth-3) to (depth-1,depth-1)
					validForwardMoves.add(new Board(this).forwardMove(depth-3, depth-3, depth-1, depth-1));
				}
			}
			if (myBoard[depth-1][depth-2]) {
				if (myBoard[depth-1][depth-3]) {
					// We move the peg from (depth-1,depth-3) to (depth-1,depth-1)
					validForwardMoves.add(new Board(this).forwardMove(depth-1, depth-3, depth-1, depth-1));
				}
			}
		}
		// CORNER MOVES END
		
		// Now check all other positions
		// Since the board is triangular, pegs can enter a position from 6 different directions:
		// (In clockwise order:) NE, E, SE, SW, W, NW 
		for (int i=1; i<depth; i++) {			// We skip the peg at (0,0) since already checked it above
			for (int j=0; j<i+1; j++) {
				if (!((i == depth-1 && j == 0) || (i == depth-1 && j == depth-1))) {		  // Skip the corner positions
					if (!myBoard[i][j]) {			// No peg exists at the current positions
						if (!(i-2 < 0 || j > i-2)) {				// Check that NE move is not outside of the board
							if (myBoard[i-1][j]) {		// Check NE adjacent peg exists for move to exist
								if (myBoard[i-2][j]) {
									validForwardMoves.add(new Board(this).forwardMove(i-2, j, i, j));
								}
							}
						}
						if (!(j+2 > i)) {				// Check the E move is not outside of the board
							if (myBoard[i][j+1]){		// Check E adjacent peg exists for move to exist
								if (myBoard[i][j+2]) {
									validForwardMoves.add(new Board(this).forwardMove(i, j+2, i, j));
								}
							}
						}
						if (!(i+2 > depth-1 || j+2 > i+2)) {	// Check the SE move is not outside of the board
							if (myBoard[i+1][j+1]){			// Check SE adjacent peg exists for move to exist
								if (myBoard[i+2][j+2]) {
									validForwardMoves.add(new Board(this).forwardMove(i+2, j+2, i, j));
								}
							}
						}
						if (!(i+2 > depth-1)) {			// Check the SW move is not outside of the board
							if (myBoard[i+1][j]) {		// Check SW adjacent peg exists for move to exist
								if (myBoard[i+2][j]) {
									validForwardMoves.add(new Board(this).forwardMove(i+2, j, i, j));
								}
							}
						}
						if (!(j-2 < 0)) {				// Check the W move is not outside of the board
							if (myBoard[i][j-1]){		// Check W adjacent peg exists for move to exist
								if (myBoard[i][j-2]) {
									validForwardMoves.add(new Board(this).forwardMove(i, j-2, i, j));
								}
							}
						}
						if (!(i-2 < 0 || j-2 < 0)) {	// Check the NW move is not outside of the board
							if (myBoard[i-1][j-1]){		// Check NW adjacent peg exists for move to exist
								if (myBoard[i-2][j-2]) {
									validForwardMoves.add(new Board(this).forwardMove(i-2, j-2, i, j));
								}
							}
						}
					}
				}
			}
		}		
		
		return validForwardMoves.size() > 0;
	}
	
	public boolean findValidBackwardMoves() {
		// Pegs in the corners only have 2 possible entry moves, so they are easy to check for first
		// CORNER MOVES START
		// Start with the top corner
		if (myBoard[0][0]) {				// A peg exists at the top-most position
			if (!myBoard[1][0]) {
				if (!myBoard[2][0]) {		// 2 successively adjacent pegs must NOT exist for move to exist
					// We make a reverse move from (0,0) to (2,0)
					validBackwardMoves.add(new Board(this).backwardMove(0, 0, 2, 0));
				}
			}
			if (!myBoard[1][1]) {			// check if the next adjacent peg does not exist
				if (!myBoard[2][2]) {
					// We reverse move from (0,0) to (2,2)
					validBackwardMoves.add(new Board(this).backwardMove(0, 0, 2, 2));
				}
			}
		}
		
		// Then the left corner
		if (myBoard[depth-1][0]) {			// A peg exists at the left corner
			if (!myBoard[depth-2][0]) {		// depth-1 in the first line to account for zero index
				if (!myBoard[depth-3][0]) {
					// We reverse move the peg from (depth-1,0) to (depth-3,0)
					validBackwardMoves.add(new Board(this).backwardMove(depth-1, 0, depth-3, 0));
				}
			}
			if (!myBoard[depth-1][1]) {
				if (!myBoard[depth-1][2]) {
					// We reverse move the peg from (depth-1,0) to (depth-1,2)
					validBackwardMoves.add(new Board(this).backwardMove(depth-1, 0, depth-1, 2));
				}
			}
		}
		
		// Now the right corner
		if (myBoard[depth-1][depth-1]) {			// A peg exists at the right corner
			if (!myBoard[depth-2][depth-2]) {		// depth-1 in the first line to account for zero index
				if (!myBoard[depth-3][depth-3]) {
					// We reverse move the peg from (depth-1,depth-1) to (depth-3,depth-3)
					validBackwardMoves.add(new Board(this).backwardMove(depth-1, depth-1, depth-3, depth-3));
				}
			}
			if (!myBoard[depth-1][depth-2]) {
				if (!myBoard[depth-1][depth-3]) {
					// We reverse move the peg from (depth-1,depth-1) to (depth-1,depth-3)
					validBackwardMoves.add(new Board(this).backwardMove(depth-1, depth-1, depth-1, depth-3));
				}
			}
		}
		// CORNER MOVES END
		
		// Now check all other positions
		// Since the board is triangular, pegs can enter a position from 6 different directions:
		// (In clockwise order:) NE, E, SE, SW, W, NW 
		for (int i=1; i<depth; i++) {			// We skip the peg at (0,0) since already checked it above
			for (int j=0; j<i+1; j++) {
				if (!((i == depth-1 && j == 0) || (i == depth-1 && j == depth-1))) {		  // Skip the corner positions
					if (myBoard[i][j]) {			// A peg exists at the current positions
						if (!(i-2 < 0 || j > i-2)) {				// Check that NE move is not outside of the board
							if (!myBoard[i-1][j]) {		// Check NE adjacent pegs DO NOT exist for move to exist
								if (!myBoard[i-2][j]) {
									validBackwardMoves.add(new Board(this).backwardMove(i, j, i-2, j));
								}
							}
						}
						if (!(j+2 > i)) {				// Check the E move is not outside of the board
							if (!myBoard[i][j+1]){		// Check E adjacent pegs DO NOT exist for move to exist
								if (!myBoard[i][j+2]) {
									validBackwardMoves.add(new Board(this).backwardMove(i, j, i, j+2));
								}
							}
						}
						if (!(i+2 > depth-1 || j+2 > i+2)) {	// Check the SE move is not outside of the board
							if (!myBoard[i+1][j+1]){			// Check SE adjacent pegs DO NOT exist for move to exist
								if (!myBoard[i+2][j+2]) {
									validBackwardMoves.add(new Board(this).backwardMove(i, j, i+2, j+2));
								}
							}
						}
						if (!(i+2 > depth-1)) {			// Check the SW move is not outside of the board
							if (!myBoard[i+1][j]) {		// Check SW adjacent pegs DO NOT exist for move to exist
								if (!myBoard[i+2][j]) {
									validBackwardMoves.add(new Board(this).backwardMove(i, j, i+2, j));
								}
							}
						}
						if (!(j-2 < 0)) {				// Check the W move is not outside of the board
							if (!myBoard[i][j-1]){		// Check W adjacent pegs DO NOT exist for move to exist
								if (!myBoard[i][j-2]) {
									validBackwardMoves.add(new Board(this).backwardMove(i, j, i, j-2));
								}
							}
						}
						if (!(i-2 < 0 || j-2 < 0)) {	// Check the NW move is not outside of the board
							if (!myBoard[i-1][j-1]){		// Check NW adjacent peg DO NOT exist for move to exist
								if (!myBoard[i-2][j-2]) {
									validBackwardMoves.add(new Board(this).backwardMove(i, j, i-2, j-2));
								}
							}
						}
					}
				}
			}
		}		
		
		return validBackwardMoves.size() > 0;
	}
	
	private Board forwardMove(int sRow, int sCol, int tRow, int tCol) {
		myBoard[sRow][sCol] = false;			// Remove the peg from the source position (sRow, sCol)
		myBoard[tRow][tCol] = true;				// Add a peg at the target position (tRow, tCol)
		myBoard[(sRow + tRow)/2][(sCol + tCol)/2] = false;	// Remove the peg in between that was jumped over
		return this;
	}
	
	private Board backwardMove(int sRow, int sCol, int tRow, int tCol) {
		myBoard[sRow][sCol] = false;			// Remove the peg from the source position (sRow, sCol)
		myBoard[tRow][tCol] = true;				// Add a peg at the target position (tRow, tCol)
		myBoard[(sRow + tRow)/2][(sCol + tCol)/2] = true;	// Remove the peg in between that was jumped over
		return this;
	}
	
	private void findEmptySlots() {
		for (int i=0; i<depth; i++)
			for (int j=0; j<i+1; j++)
				if (!myBoard[i][j]) {
					emptySlots.push(j);
					emptySlots.push(i);
				}
	}
	
	public boolean isGoal(int gRow, int gCol) {
		// Flip the boolean at the goal position
		// Then OR everything with a false value.
		// If the final value is still false,
		// then there was only one peg at the goal position.
		boolean restZero = false;
		myBoard[gRow][gCol] = !myBoard[gRow][gCol];
		for (int i=0; i<depth; i++)
			for (int j=0; j<i+1; j++)
				restZero = restZero | myBoard[i][j];
		// Flip the boolean at the goal position back
		myBoard[gRow][gCol] = !myBoard[gRow][gCol];
		return !restZero;
	}
	
	public boolean isGoal(Board goal) {
		// Compare every peg state in the current board
		// against the goal board. AND the boolean "match"
		// with the previous comparison. If any of the positions
		// don't match, then "match" will be false;
		boolean match = true;
		for (int i=0; i<depth; i++)
			for (int j=0; j<i+1; j++)
				match = match && (myBoard[i][j] && goal.myBoard[i][j]);
		return match;
	}
	
	public void printBoard() {
		for (int i=0; i<depth; i++) {
			for (int j=0; j<i+1; j++) {
				if (myBoard[i][j])
					System.out.print("X ");
				else
					System.out.print("O ");
			}
			System.out.println();
		}
	}
	
	public int validMovesCount() {
		return validForwardMoves.size();
	}
	
	public void printValidMoves() {
		findValidForwardMoves();
		System.out.println("Valid moves from the above configuration are:");
		for (int i=0; i<validForwardMoves.size(); i++) {
			validForwardMoves.elementAt(i).printBoard();
			System.out.println("and");
		}
	}
	
	public void clearForwardMovesList() {
		validForwardMoves.clear();
	}
}
