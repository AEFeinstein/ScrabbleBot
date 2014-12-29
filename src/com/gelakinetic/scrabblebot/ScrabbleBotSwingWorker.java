package com.gelakinetic.scrabblebot;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;

public class ScrabbleBotSwingWorker extends SwingWorker<Void, Void> {

	private ScrabbleBot mScrabbleBot;
	private ScrabbleTile[][] mScrabbleBoard;
	private ArrayList<Character> mRack;
	private JTextPane mOutputTextPane;
	private JFrame mFrame;

	public ScrabbleBotSwingWorker(JFrame frame, JTextPane outputTextPane) {
		mFrame = frame;
		mOutputTextPane = outputTextPane;
	}

	@Override
	protected void done() {
		// TODO Auto-generated method stub

		super.done();
	}

	@Override
	protected void process(List<Void> chunks) {
		// TODO Auto-generated method stub
		super.process(chunks);
	}

	@Override
	protected Void doInBackground() throws Exception {
		mFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			mScrabbleBot.findPlays(mScrabbleBoard, mRack, mOutputTextPane);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mFrame.setCursor(Cursor.getDefaultCursor());

		return null;
	}

	public void execute(ScrabbleBot scrabbleBot,
			ScrabbleTile[][] scrabbleBoard, ArrayList<Character> rackAL) {
		mScrabbleBoard = scrabbleBoard;
		mRack = rackAL;
		mScrabbleBot = scrabbleBot;
		this.execute();
	}

}
