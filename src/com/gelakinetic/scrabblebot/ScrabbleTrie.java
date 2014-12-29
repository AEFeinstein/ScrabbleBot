package com.gelakinetic.scrabblebot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public abstract class ScrabbleTrie {

	ScrabbleTrieNode root;

	/**
	 * Load a dictionary into this Trie
	 * 
	 * @param f
	 *            The file which holds the dictionary
	 * @throws IOException
	 *             If something goes wrong
	 */
	public void LoadDictionary() throws IOException {
		root = new ScrabbleTrieNode((char) 0, null);

		InputStream is = ScrabbleTrie.class.getClassLoader().getResourceAsStream("ospd4.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		String word;
		while ((word = br.readLine()) != null) {
			AddWord(processWord(word.toLowerCase()));
		}
		br.close();
	}

	/**
	 * Overridden to make forward and reverse tries
	 * @param word The word to process
	 * @return Either the word, or the word reversed
	 */
	protected abstract String processWord(String word);

	/**
	 * Add a word to the trie, marking that it is a word
	 * 
	 * @param word The word to add to the trie
	 */
	private void AddWord(String word) {
		ScrabbleTrieNode currentNode = root;
		char currentChar;
		for (int i = 0; i < word.length(); i++) {
			/* Grab the next Character in the word */
			currentChar = word.charAt(i);

			/* Make sure that Character exist in the trie */
			if (!currentNode.childrenContains(currentChar)) {
				/* add a new child */
				currentNode.addChild(new ScrabbleTrieNode(currentChar,
						currentNode));
			}

			/* move to that child */
			currentNode = currentNode.getChildNode(currentChar);

			/* Tag the end of the word */
			if (i == word.length() - 1) {
				/* mark it */
				currentNode.setIsAWord(true);
			}
		}
	}

	/**
	 * Return a list of possible words constructed from the give Characters
	 * 
	 * @param rack The rack to build words from
	 * @param necessaryChar The character that must be in the formed words
	 * @return A list of valid words that can be built with the constraints
	 */
	public ArrayList<String> findWords(ArrayList<Character> rack, char necessaryChar) {
		root.clearRacksRecursive();
		root.setRack(rack);

		ArrayList<String> words = new ArrayList<String>();
		root.findWordsRecursive(words, necessaryChar);

		return words;
	}

	/**
	 * Quick check to see if a word is valid
	 * 
	 * @param word The word to check
	 * @return true if the word is valid, false otherwise
	 */
	public boolean isAWord(String word) {
		
		ScrabbleTrieNode currentNode = root;
		for(int i = 0; i < word.length(); i++) {
			
			currentNode = currentNode.getChildNode(word.charAt(i));
			
			if(currentNode == null) {
				return false;
			}
		}
		return currentNode.IsAWord();
	}
}
