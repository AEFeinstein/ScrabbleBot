package com.gelakinetic.scrabblebot;

public class ReverseScrabbleTrie extends ScrabbleTrie {

	@Override
	protected String processWord(String word) {
		/* Reverse the string */
		int length = word.length();
		String reverse = "";
		for (int i = length - 1; i >= 0; i--) {
			reverse += word.charAt(i);
		}
		return reverse;
	}
}
