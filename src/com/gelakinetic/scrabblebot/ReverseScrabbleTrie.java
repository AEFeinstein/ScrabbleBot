package com.gelakinetic.scrabblebot;

public class ReverseScrabbleTrie extends ScrabbleTrie {

	@Override
	protected String processWord(String word) {
		/* Reverse the string */
		return ScrabbleBot.reverse(word);
	}
}
