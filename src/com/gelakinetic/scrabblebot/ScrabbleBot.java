package com.gelakinetic.scrabblebot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JTextPane;

public class ScrabbleBot {

	static Coord tripleWords[] = {
		new Coord(0, 0),
		new Coord(0, 7),
		new Coord(0, 14),
		new Coord(7, 0),
		new Coord(7, 14),
		new Coord(14, 0),
		new Coord(14, 7),
		new Coord(14, 14),
	};

	static Coord tripleLetters[] = {
		new Coord(5, 1),
		new Coord(9, 1),
		new Coord(1, 5),
		new Coord(5, 5),
		new Coord(9, 5),
		new Coord(13, 5),
		new Coord(1, 9),
		new Coord(5, 9),
		new Coord(9, 9),
		new Coord(13, 9),
		new Coord(5, 13),
		new Coord(9, 13),

	};

	static Coord doubleWords[] = {
		new Coord(1, 1),
		new Coord(2, 2),
		new Coord(3, 3),
		new Coord(4, 4),

		new Coord(7, 7),

		new Coord(10, 10),
		new Coord(11, 11),
		new Coord(12, 12),
		new Coord(13, 13),

		new Coord(13, 1),
		new Coord(12, 2),
		new Coord(11, 3),
		new Coord(10, 4),

		new Coord(4, 10),
		new Coord(3, 11),
		new Coord(2, 12),
		new Coord(1, 13),
	};

	static Coord doubleLetters[] = {
		new Coord(3,0),
		new Coord(11,0),

		new Coord(6,2),
		new Coord(8,2),

		new Coord(0,3),
		new Coord(7,3),
		new Coord(14,3),

		new Coord(2,6),
		new Coord(6,6),
		new Coord(8,6),
		new Coord(12,6),

		new Coord(3,7),
		new Coord(11,7),

		new Coord(2,8),
		new Coord(6,8),
		new Coord(8,8),
		new Coord(12,8),

		new Coord(0,11),
		new Coord(7,11),
		new Coord(14,11),

		new Coord(6,12),
		new Coord(8,12),

		new Coord(3,14),
		new Coord(11,14),
	};

	ScrabbleTrie fTrie;
	ScrabbleTrie rTrie;

	/**
	 * Constructor
	 * @throws IOException
	 */
	public ScrabbleBot() throws IOException {
		fTrie = new ForwardScrabbleTrie();
		fTrie.LoadDictionary();
		rTrie = new ReverseScrabbleTrie();
		rTrie.LoadDictionary();		
	}

	/**
	 * Given a board and a rack, find all the plays
	 * 
	 * @param scrabbleBoard Must be lowercase!
	 * @param rackAL
	 * @param mOutputTextPane
	 */
	public void findPlays(ScrabbleTile[][] scrabbleBoard, ArrayList<Character> rackAL,
			JTextPane mOutputTextPane) {

		ArrayList<ScrabblePlay> plays = new ArrayList<ScrabblePlay>();

		findIntersectingWords(scrabbleBoard, rackAL, plays);
		System.out.println("plays: " + plays.size());
		findPrefixesAndSuffixes(scrabbleBoard, rackAL, plays);
		System.out.println("plays: " + plays.size());
		findAddonWords(scrabbleBoard, rackAL, plays);
		System.out.println("plays: " + plays.size());

		Collections.sort(plays);
		String allPlays = "";
		for(ScrabblePlay play : plays) {
			allPlays += play + "\n";
		}
		mOutputTextPane.setText(allPlays);
		mOutputTextPane.setCaretPosition(0);
	}

	/**
	 * 
	 * @param scrabbleBoard
	 * @param rackAL
	 * @param plays 
	 * @return
	 */
	private void findAddonWords(ScrabbleTile[][] scrabbleBoard, ArrayList<Character> rackAL, ArrayList<ScrabblePlay> plays) {

		HashMap<Character, ArrayList<String>> potentialAddons = new HashMap<Character, ArrayList<String>>(6);

		/* For each tile in the rack */
		for(int i = 0; i < rackAL.size(); i++) {
			/* Find and store all rack-only anagrams containing that letter */
			char tmp = rackAL.get(i);
			if(tmp == '*') {
				for(int j = 0; j < 26; j++) {
					char tmpWildcard = (char) ('a' + j);
					/* Find words for this potential letter */
					if(!potentialAddons.keySet().contains(tmpWildcard)) {
						potentialAddons.put(tmpWildcard, fTrie.findWords(null, rackAL, tmpWildcard));
					}
				}
			}
			else {
				if(!potentialAddons.keySet().contains(tmp)) {
					potentialAddons.put(tmp, fTrie.findWords(null, rackAL, tmp));
				}
			}
		}
		
		/* For each two letter word, see if we can use it to anchor a rack-only anagram */
		int numOldPlays = plays.size();
		for(int i = 0; i < numOldPlays; i++) {
			ScrabblePlay sp = plays.get(i);
			if(sp.word.length() == 2) {
				int addonX = 0, addonY = 0;

				/* Make a temporary board */
				ScrabbleTile scrabbleBoardTemp[][] = new ScrabbleTile[15][15];
				ArrayCopy2D(scrabbleBoard, scrabbleBoardTemp, 15, 15);

				/* Lay the two letter word on the temp board, manually */
				if(scrabbleBoardTemp[sp.origin.x][sp.origin.y].isEmpty()) {
					scrabbleBoardTemp[sp.origin.x][sp.origin.y] = new ScrabbleTile(sp.word.charAt(0), true);
					addonX = sp.origin.x;
					addonY = sp.origin.y;
					if (!rackAL.contains(sp.word.charAt(0))) {
						scrabbleBoardTemp[sp.origin.x][sp.origin.y].setWildcard(true);
					}
				}

				switch(sp.direction) {
					case ScrabblePlay.HORIZONTAL: {
						if(scrabbleBoardTemp[sp.origin.x + 1][sp.origin.y].isEmpty()) {
							scrabbleBoardTemp[sp.origin.x + 1][sp.origin.y] = new ScrabbleTile(sp.word.charAt(1), true);
							addonX = sp.origin.x + 1;
							addonY = sp.origin.y;
							if (!rackAL.contains(sp.word.charAt(1))) {
								scrabbleBoardTemp[sp.origin.x + 1][sp.origin.y].setWildcard(true);
							}
						}
						break;
					}
					case ScrabblePlay.VERTICAL: {
						if(scrabbleBoardTemp[sp.origin.x][sp.origin.y + 1].isEmpty()) {
							scrabbleBoardTemp[sp.origin.x][sp.origin.y + 1] = new ScrabbleTile(sp.word.charAt(1), true);
							addonX = sp.origin.x;
							addonY = sp.origin.y + 1;
							if (!rackAL.contains(sp.word.charAt(1))) {
								scrabbleBoardTemp[sp.origin.x][sp.origin.y + 1].setWildcard(true);
							}
						}
						break;
					}
				}

				/* Check the list of rack-only anagrams against the new letter in the two letter word */
				ArrayList<String> choices = potentialAddons.get(scrabbleBoardTemp[addonX][addonY].getLetter());

				/* For each word, see if it requires a wildcard, and replace it if necessary */
				scoreAllWords(choices, scrabbleBoardTemp, rackAL, plays, addonX, addonY, 1);
			}
		}
	}

	/**
	 * 
	 * @param scrabbleBoard
	 * @param rackAL
	 * @param plays 
	 * @return
	 */
	private void findIntersectingWords(ScrabbleTile[][] scrabbleBoard, ArrayList<Character> rackAL, ArrayList<ScrabblePlay> plays) {
		
		HashMap<Character, ArrayList<String>> potentialIntersections = new HashMap<Character, ArrayList<String>>(26);
		for(int i = 0; i < 26; i++) {
			char tmp = (char) ('a' + i);
			/* Add a potential letter */
			rackAL.add(tmp);

			/* Find words for this potential letter */
			potentialIntersections.put(tmp, fTrie.findWords(null, rackAL, tmp));

			/* Remove the potential letter */
			rackAL.remove(rackAL.size()-1);
		}
		
		/* For each space on the board, if there is a character there, see if it can be an anchor for an intersecting word */
		for(int y=0; y < 15; y++) {
			for(int x = 0; x < 15; x++) {
				if(!scrabbleBoard[x][y].isEmpty()) {
					ArrayList<String> choices = potentialIntersections.get(scrabbleBoard[x][y].getLetter());
					scoreAllWords(choices, scrabbleBoard, rackAL, plays, x, y, 0);
				}
			}
		}
	}

	private void scoreAllWords(ArrayList<String> choices, ScrabbleTile[][] scrabbleBoard, ArrayList<Character> rackAL, ArrayList<ScrabblePlay> plays, int x, int y, int tilesAlreadyPlaced) {
		/* For each word, see if it requires a wildcard, and replace it if necessary */
		for(String word : choices) {
			ScrabblePlay sp = scoreWord(scrabbleBoard, rackAL, word, x, y, tilesAlreadyPlaced);

			if(sp != null && sp.score > 0) {
				if(!plays.contains(sp)) {
					plays.add(sp);
				}				
			}
		}
	}

	/**
	 * 
	 * @param scrabbleBoard
	 * @param rackAL
	 * @param plays 
	 * @return 
	 * @return
	 */
	private void findPrefixesAndSuffixes(ScrabbleTile[][] scrabbleBoard, ArrayList<Character> rackAL, ArrayList<ScrabblePlay> plays) {
		
		/* For each word on the board, search for prefixes and suffixes */
		int prefixSpace, suffixSpace;
		
		/* Sweep across all columns */
		for(int x = 0; x < 15; x++) {
			/* Start a blank word */
			String word = "";
			prefixSpace = 0;
			suffixSpace = 0;
			for(int y = 0; y < 15; y++) {
				if(!scrabbleBoard[x][y].isEmpty()) {
					word += scrabbleBoard[x][y].getLetter();
				}
				if (scrabbleBoard[x][y].isEmpty() || y == 14) {
					if(word.isEmpty()) {
						prefixSpace++;
					}
					else {
						if(word.length() > 1) {
							suffixSpace = 0;
							while((y+suffixSpace) < 15 && scrabbleBoard[x][(y+suffixSpace)].isEmpty()) {
								suffixSpace++;
							}
							checkPrefixSuffix(word, prefixSpace, suffixSpace, scrabbleBoard, rackAL, plays, x, y - word.length());
						}
						word = "";
						prefixSpace = 1;
					}					
				}
			}
		}
		
		/* Sweep across all rows */
		for(int y = 0; y < 15; y++) {
			/* Start a blank word */
			String word = "";
			prefixSpace = 0;
			suffixSpace = 0;
			for(int x = 0; x < 15; x++) {
				if(!scrabbleBoard[x][y].isEmpty()) {
					word += scrabbleBoard[x][y].getLetter();
				}
				if (scrabbleBoard[x][y].isEmpty() || x == 14) {
					if(word.isEmpty()) {
						prefixSpace++;
					}
					else {
						if(word.length() > 1) {
							suffixSpace = 0;
							while((x+suffixSpace) < 15 && scrabbleBoard[x+suffixSpace][y].isEmpty()) {
								suffixSpace++;
							}
							checkPrefixSuffix(word, prefixSpace, suffixSpace, scrabbleBoard, rackAL, plays, x - word.length(), y);
						}
						word = "";
						prefixSpace = 1;
					}					
				}
			}
		}
	}

	/**
	 * 
	 * @param word
	 * @param prefixSpace
	 * @param suffixSpace
	 * @param rackAL
	 * @return
	 */
	private void checkPrefixSuffix(String word, int prefixSpace,
			int suffixSpace, ScrabbleTile scrabbleBoard[][], ArrayList<Character> rackAL, ArrayList<ScrabblePlay> plays, int x, int y) {
		
		ArrayList<String> forward = fTrie.findPostfix(word, prefixSpace, rackAL);
		scoreAllWords(forward, scrabbleBoard, rackAL, plays, x, y, 0);
		
		ArrayList<String> backward = rTrie.findPostfix(reverse(word), prefixSpace, rackAL);
		for(int i = 0; i < backward.size(); i++) {
			backward.add(reverse(backward.remove(0)));
		}
		scoreAllWords(backward, scrabbleBoard, rackAL, plays, x, y, 0);
	}

	public static String reverse(String word) {
		int length = word.length();
		String reverse = "";
		for (int i = length - 1; i >= 0; i--) {
			reverse += word.charAt(i);
		}
		return reverse;
	}

	/**
	 * Given a word and an anchor tile, verify that it fits and find the highest score for the best anchor
	 * TODO broken for prefix / suffix case
	 * 
	 * @param scrabbleBoard
	 * @param word
	 * @param x
	 * @param y
	 * @return
	 */
	private ScrabblePlay scoreWord(ScrabbleTile[][] scrabbleBoard, ArrayList<Character> rackAL, String word, int x,
			int y, int tilesAlreadyPlaced) {

		ScrabbleTile[][] scrabbleBoardTemp = new ScrabbleTile[15][15];
		
		String wildcardMask = new String(word);
		
		ArrayList<Character>rackALcopy = new ArrayList<Character>(rackAL);
		rackALcopy.add(scrabbleBoard[x][y].getLetter());
		for(int i = 0; i < word.length(); i++) {
			if(rackALcopy.contains(word.charAt(i))) {
				rackALcopy.remove((Character)word.charAt(i));
			}
			else {
				wildcardMask = wildcardMask.substring(0,i)+'*'+wildcardMask.substring(i+1);
				rackALcopy.remove((Character)'*');
			}
		}
		
		/* find the letter which intersects the existing and new word */
		char anchorChar = scrabbleBoard[x][y].getLetter();
		ArrayList<Integer> indices = new ArrayList<Integer>();

		int lastIndex = 0;
		while(lastIndex != -1) {
			int index = word.indexOf(anchorChar, lastIndex);
			if(index != -1) {
				indices.add(index);
			}
			else {
				break;
			}
			lastIndex = index + 1;
		}

		/* For each anchor ScrabbleTile in the word, see if it fits, and score it */
		int largestScore = -1;
		int bestDirection = 0;
		Coord horzOrigin, vertOrigin, bestOrigin = null;
		int lettersPlaced; /* for the wacky case where we try to lay a word and the whole thing already exists */
		
		for(int anchor : indices) {
			int scoreHorizontal = 0, scoreVertical = 0;

			/* Try Horizontal */
			try {

				ArrayCopy2D(scrabbleBoard, scrabbleBoardTemp, 15, 15);
				lettersPlaced = tilesAlreadyPlaced;
				horzOrigin = new Coord(x - anchor, y);
				for(int i = 0; i < word.length(); i++) {
					if(i != anchor) {
						if(!scrabbleBoard[x + i - anchor][y].isEmpty()) {
							/* Perhaps the tile we need for the word is alreay there? */
							if(scrabbleBoard[x + i - anchor][y].getLetter() != word.charAt(i)) {
								scoreHorizontal = -1;
								break;
							}
						}
						else {
							scrabbleBoardTemp[x + i - anchor][y] = new ScrabbleTile(word.charAt(i), true);
							lettersPlaced++;
							if(wildcardMask.charAt(i) == '*') {
								scrabbleBoardTemp[x + i - anchor][y].setWildcard(true);
							}
						}
					}
				}

				if(scoreHorizontal == 0 && (lettersPlaced - tilesAlreadyPlaced) > 0) {
					scoreHorizontal = verifyAndScore(scrabbleBoardTemp, lettersPlaced);
				}

				if(scoreHorizontal > largestScore) {
					bestOrigin = horzOrigin;
					bestDirection = ScrabblePlay.HORIZONTAL;
					largestScore = scoreHorizontal;
				}
			} catch(ArrayIndexOutOfBoundsException e) {
				/* off the board */
			}

			/* Try Vertical */
			try {

				ArrayCopy2D(scrabbleBoard, scrabbleBoardTemp, 15, 15);
				lettersPlaced = tilesAlreadyPlaced;
				vertOrigin = new Coord(x, y - anchor);
				for(int i = 0; i < word.length(); i++) {
					if(i != anchor) {
						if(!scrabbleBoard[x][y + i - anchor].isEmpty()) {
							/* Perhaps the tile we need for the word is alreay there? */
							if(scrabbleBoard[x + i - anchor][y].getLetter() != word.charAt(i)) {
								scoreVertical = -1;
								break;
							}
						}
						else {
							scrabbleBoardTemp[x][y + i - anchor] = new ScrabbleTile(word.charAt(i), true);
							lettersPlaced++;
							if(wildcardMask.charAt(i) == '*') {
								scrabbleBoardTemp[x][y + i - anchor].setWildcard(true);
							}
						}
					}
				}

				if(scoreVertical == 0 && (lettersPlaced-tilesAlreadyPlaced) > 0) {
					scoreVertical = verifyAndScore(scrabbleBoardTemp, lettersPlaced);
				}
				if(scoreVertical > largestScore) {
					bestOrigin = vertOrigin;
					bestDirection = ScrabblePlay.VERTICAL;
					largestScore = scoreVertical;
				}

			} catch(ArrayIndexOutOfBoundsException e) {
				/* off the board */
			}
		}
		
		if(largestScore > 0) {
			return new ScrabblePlay(bestOrigin, bestDirection, word, largestScore);
		}
		return null;
	}

	/**
	 * Scans the board in horizontal and vertical directions for all words, scores any new ones, returns the total score of this play
	 * 
	 * @param board
	 * @return
	 */
	private int verifyAndScore(ScrabbleTile[][] board, int tilesPlaced) {

		int totalScore = 0;
		int wordScore = 0;
		int wordMultiplier;
		boolean isNewWord;

		String word;

		/* Sweep across all columns */
		for(int x = 0; x < 15; x++) {
			/* Start a blank word */
			word = "";
			isNewWord = false;
			wordScore = 0;
			wordMultiplier = 0;

			for(int y = 0; y < 15; y++) {
				/* If the current tile has a ScrabbleTile, append it to the current word */
				if(!board[x][y].isEmpty()) {
					word += board[x][y].getLetter();
					wordScore += tileValue(board[x][y], x, y);
					if(wordScore >= 300) {
						wordMultiplier += 3;
						wordScore -= 300;
					}
					else if(wordScore >= 200) {
						wordMultiplier += 2;
						wordScore -= 200;						
					}
					if(board[x][y].isNewlyPlaced()) {
						isNewWord = true;
					}
				}
				if(board[x][y].isEmpty() || y == 14){
					/* If we hit a blank space, check the word if we have one, then clear it */
					if(word.length() > 1) {
						if(null == fTrie.isAWord(word)) {
							/* Word verification fail */
							return 0;
						}
						else if(isNewWord) {
							/* Word is the real deal, score it */
							if(wordMultiplier > 0) {
								wordScore *= wordMultiplier;
							}
							totalScore += wordScore;
						}
					}
					word = "";
					isNewWord = false;
					wordScore = 0;
					wordMultiplier = 0;
				}
			}
		}

		/* Sweep across all rows */
		for(int y = 0; y < 15; y++) {
			/* Start a blank word */
			word = "";
			isNewWord = false;
			wordScore = 0;
			wordMultiplier = 0;

			for(int x = 0; x < 15; x++) {
				/* If the current tile has a ScrabbleTile, append it to the current word */
				if(!board[x][y].isEmpty()) {
					word += board[x][y].getLetter();
					wordScore += tileValue(board[x][y], x, y);
					if(wordScore >= 300) {
						wordMultiplier += 3;
						wordScore -= 300;
					}
					else if(wordScore >= 200) {
						wordMultiplier += 2;
						wordScore -= 200;						
					}
					if(board[x][y].isNewlyPlaced()) {
						isNewWord = true;
					}
				}
				if(board[x][y].isEmpty() || x == 14){
					/* If we hit a blank space, check the word if we have one, then clear it */
					if(word.length() > 1) {
						if(null == fTrie.isAWord(word)) {
							/* Word verification fail */
							return 0;
						}
						else if(isNewWord) {
							/* Word is the real deal, score it */
							if(wordMultiplier > 0) {
								wordScore *= wordMultiplier;
							}
							totalScore += wordScore;
						}
					}
					word = "";
					isNewWord = false;
					wordScore = 0;
					wordMultiplier = 0;
				}
			}
		}
		
		if(tilesPlaced == 7) {
			totalScore += 50;
		}
		return totalScore;
	}

	/**
	 * Helper function to copy a 2D array
	 * 
	 * @param scrabbleBoard
	 * @param scrabbleBoardTemp
	 * @param rows
	 * @param cols
	 */
	private void ArrayCopy2D(ScrabbleTile[][] scrabbleBoard,
			ScrabbleTile[][] scrabbleBoardTemp, int rows, int cols) {
		for(int i = 0; i < rows; i++)
		{
			System.arraycopy(scrabbleBoard[i], 0, scrabbleBoardTemp[i], 0, cols);
		}
	}

	/**
	 * Returns the value of a tile when scoring a word. It kind of hacks a return for double & triple word scores
	 * 
	 * @param tile
	 * @param x
	 * @param y
	 * @return
	 */
	private int tileValue(ScrabbleTile tile, int x, int y) {
		int val = tile.getValue();

		if(tile.isNewlyPlaced()) {
			if(contains(new Coord(x,y), doubleLetters)) {
				val *= 2;
			}
			if(contains(new Coord(x,y), tripleLetters)) {
				val *= 3;
			}
			if(contains(new Coord(x,y), doubleWords)) {
				val += 200;
			}
			if(contains(new Coord(x,y), tripleWords)) {
				val += 300;
			}
		}
		return val;
	}


	public static boolean contains(Coord coord, Coord[] tripleWords2) {
		for(Coord c : tripleWords2) {
			if(c.equals(coord)) {
				return true;
			}
		}
		return false;
	}
}
