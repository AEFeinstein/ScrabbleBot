package com.gelakinetic.scrabblebot;

public class ScrabbleTile {
	private char letter;
	private boolean newlyPlaced;
	private boolean wildcard;
	
	/**
	 * Constructor, sets the tile's values, and marks the wildcard if necessary
	 * 
	 * @param letter The character for this tile
	 * @param newlyPlaced Whether this tile is newly placed, or if it had been on the board
	 */
	public ScrabbleTile(char letter, boolean newlyPlaced) {
		this.letter = letter;
		this.newlyPlaced = newlyPlaced;
		if(letter == '*') {
			wildcard = true;
		}
		else {
			wildcard = false;
		}
	}

	/**
	 * Returns the value of this tile when scoring a word. 
	 * 
	 * @return 0 if the tile is a wildcard, the tile's value otherwise
	 */
	int getValue() {
		if(wildcard) {
			return 0;
		}
		char tmp = (letter + "").toLowerCase().charAt(0);
		int val = 0;
		
		switch(tmp) {
			case 'a': val = 1; break;
			case 'b': val = 3; break;
			case 'c': val = 3; break;
			case 'd': val = 2; break;
			case 'e': val = 1; break;
			case 'f': val = 4; break;
			case 'g': val = 2; break;
			case 'h': val = 4; break;
			case 'i': val = 1; break;
			case 'j': val = 8; break;
			case 'k': val = 5; break;
			case 'l': val = 1; break;
			case 'm': val = 3; break;
			case 'n': val = 1; break;
			case 'o': val = 1; break;
			case 'p': val = 3; break;
			case 'q': val = 10; break;
			case 'r': val = 1; break;
			case 's': val = 1; break;
			case 't': val = 1; break;
			case 'u': val = 1; break;
			case 'v': val = 4; break;
			case 'w': val = 4; break;
			case 'x': val = 8; break;
			case 'y': val = 4; break;
			case 'z': val = 10; break;
			case '*': val = 0; break;
		}
		return val;
	}

	/**
	 * Returns whether the tile is empty or not
	 * 
	 * @return true if the tile is empty, false otherwise
	 */
	public boolean isEmpty() {
		return letter == 0;
	}
	
	/**
	 * Just a debugging tool, prints the char at this tile
	 * 
	 * @return The char for this tile, in string form
	 */
	public String toString() {
		return letter+"";
	}

	/**
	 * Sets whether this tile is a wildcard or not
	 * @param isWildcard whether this tile is a wildcard or not
	 */
	public void setWildcard(boolean isWildcard) {
		this.wildcard = isWildcard;
	}

	/**
	 * Returns whether the tile is new for a play, or if it existed already
	 * @return true if the tile is new, false if it is old
	 */
	public boolean isNewlyPlaced() {
		return newlyPlaced;
	}

	/**
	 * Returns the character for this tile
	 * @return The character for this tile
	 */
	public char getLetter() {
		return letter;
	}

	/**
	 * Returns whether the tile is a wildcard tile or not
	 * @return true if the tile is wild, false if it is not
	 */
	public boolean isWildcard() {
		return wildcard;
	}
}
