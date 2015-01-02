package com.gelakinetic.scrabblebot;

public class ScrabblePlay implements Comparable<ScrabblePlay> {
	
	private static final Coord BOARD_CENTER = new Coord(7,7);
	public static final int HORIZONTAL = 1;
	public static final int VERTICAL = 2;
	
	Coord origin;
	int direction;
	
	String word;
	int score;
	
	/**
	 * Constructor
	 * 
	 * @param origin Where this word starts
	 * @param direction The direction this word is placed
	 * @param word The word
	 * @param score The score from this play
	 */
	public ScrabblePlay(Coord origin, int direction, String word, int score) {
		this.origin = origin;
		this.direction = direction;
		
		this.word = word;
		this.score = score;
	}

	/**
	 * Used to sort ScrabblePlays. It tries to maximize score, then minimize word length,
	 * then place the word closest to the center of the board
	 * 
	 * @param other Another ScrabblePlay
	 */
	@Override
	public int compareTo(ScrabblePlay other) {
		if(this.score > other.score) {
			return -1;
		}
		else if(this.score < other.score) {
			return 1;
		}
		else {
			if(this.word.length() < other.word.length()) {
				return -1;
			}
			else if(this.word.length() > other.word.length()) {
				return 1;
			}
			else {
				if(this.largestOutlier() < other.largestOutlier()) {
					return -1;
				}
				else if(this.largestOutlier() > other.largestOutlier()) {
					return 1;
				}
				else {
					return 0;
				}
			}
		}
	}

	/**
	 * Returns the largest distance from an edge of this ScrabblePlay to the center of the board
	 * 
	 * @return The distance, a double
	 */
	private double largestOutlier() {
		Coord finish = new Coord(origin.x, origin.y);
		if(direction == HORIZONTAL) {
			finish.x += word.length() - 1;
		}
		else {
			finish.y += word.length() - 1;
		}
		return Math.max(origin.distFrom(BOARD_CENTER), finish.distFrom(BOARD_CENTER));
	}

	/**
	 * Returns a string representation of this ScrabblePlay, for human readability
	 * 
	 * @return The score, word, and where to place it
	 */
	@Override
	public String toString() {
		return String.format("%d: %s (%d,%d) %s", score, word, origin.x, origin.y,
				(direction == HORIZONTAL)? "horizontal" : "vertical");
	}

	/**
	 * Used to make sure no duplicate plays are added to the set of plays
	 * 
	 * @param obj An object to check against this ScrabblePlay
	 * @return true if the play is the same (word and location), false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ScrabblePlay) {
			try {
			return (this.word.equals(((ScrabblePlay)obj).word)) &&
					(this.origin.equals(((ScrabblePlay)obj).origin)) &&
					(this.direction == (((ScrabblePlay)obj).direction));
			}
			catch(Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
}
