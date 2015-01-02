package com.gelakinetic.scrabblebot;

import java.util.ArrayList;

public class ScrabbleTrieNode {
	private char mScrabbleTile;
	private ArrayList<Character> mRack;
	private ArrayList<ScrabbleTrieNode> mChildren;
	private ScrabbleTrieNode mParent;
	private boolean mIsAWord;

	public ScrabbleTrieNode(char c, ScrabbleTrieNode parent) {
		mScrabbleTile = c;
		mRack = new ArrayList<Character>();
		mChildren = new ArrayList<ScrabbleTrieNode>();
		mParent = parent;
		mIsAWord = false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ScrabbleTrieNode) {
			return ((ScrabbleTrieNode) obj).mScrabbleTile == this.mScrabbleTile;
		} else if (obj instanceof Character) {
			return ((char) obj) == this.mScrabbleTile;
		}
		return false;
	}

	/**
	 * Returns true if this node has the given ScrabbleTile as a child, false
	 * otherwise
	 * 
	 * @param currentChar
	 * @return
	 */
	public boolean childrenContains(char currentChar) {
		for (ScrabbleTrieNode child : mChildren) {
			if (child.equals(currentChar)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * TODO
	 * 
	 * @param b
	 */
	public void setIsAWord(boolean b) {
		mIsAWord = b;
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public boolean IsAWord() {
		return mIsAWord;
	}

	/**
	 * Returns the node defined by the given ScrabbleTile
	 * 
	 * @param currentChar
	 * @return
	 */
	public ScrabbleTrieNode getChildNode(Character currentChar) {
		for (ScrabbleTrieNode child : mChildren) {
			if (child.equals(currentChar)) {
				return child;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param scrabbleTrieNode
	 */
	public void addChild(ScrabbleTrieNode scrabbleTrieNode) {
		this.mChildren.add(scrabbleTrieNode);
	}

	public void clearRacksRecursive() {
		this.mRack.clear();
		for (ScrabbleTrieNode child : mChildren) {
			child.clearRacksRecursive();
		}
	}

	public void setRack(ArrayList<Character> rack) {
		this.mRack.clear();
		this.mRack.addAll(rack);
	}

	public ArrayList<String> findWordsRecursive(ArrayList<String> words, char necessaryChar) {
		/* For all ScrabbleTiles in the rack */
		for (char c : mRack) {
			/* If that ScrabbleTile is a potential next letter */
			for (ScrabbleTrieNode child : mChildren) {
				if (child.equals(c) || c == '*') {
					/*
					 * Set the child's rack as the current rack, minus the used
					 * tile
					 */
					child.setRack(mRack);
					child.removeCharFromRack(c);

					/* Note if this is a full word */
					if (child.mIsAWord &&
							!words.contains(child.getWord()) &&
							(child.getWord().contains(necessaryChar+"") || necessaryChar == 0 || necessaryChar == '*')) {
						words.add(child.getWord());
					}

					/* Keep looking */
					child.findWordsRecursive(words, necessaryChar);
				}
			}
		}
		return words;
	}

	private String getWord() {

		String word = "";
		ScrabbleTrieNode currentNode = this;

		/* Build the word from this node upward */
		word += currentNode.mScrabbleTile;
		while (currentNode.mParent != null) {
			currentNode = currentNode.mParent;
			if (currentNode.mScrabbleTile > 0) {
				word += currentNode.mScrabbleTile;
			}
		}

		return ScrabbleBot.reverse(word);
	}

	private void removeCharFromRack(Character c) {
		mRack.remove(c);
	}

}
