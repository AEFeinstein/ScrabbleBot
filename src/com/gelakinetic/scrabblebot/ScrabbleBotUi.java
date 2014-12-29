package com.gelakinetic.scrabblebot;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ScrabbleBotUi {

	private static final String AUTO_SAVE = "auto.sav";
	private JTextField mRackTextField;
	private JTextPane mOutputTextPane;
	private JPanel mScrabblePanel;
	private JFrame mFrame;
	private ScrabbleBot mScrabbleBot;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
		        try {
					UIManager.setLookAndFeel(
					        UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (UnsupportedLookAndFeelException e1) {
					e1.printStackTrace();
				}
		        
				try {
					new ScrabbleBotUi();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ScrabbleBotUi() {

		try {
			mScrabbleBot = new ScrabbleBot();
		} catch (IOException e2) {
			System.exit(0);
		}
		
		mFrame = new JFrame();
		mFrame.setBounds(100, 100, 700, 768);
		mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{448, 0};
		gridBagLayout.rowHeights = new int[]{260, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		mFrame.getContentPane().setLayout(gridBagLayout);

		mFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			/**
			 * Save the state of the board in a file
			 */
			@Override
			public void windowClosing(WindowEvent winEvt) {
				try {
					saveState(new File(AUTO_SAVE));
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		});

		this.mScrabblePanel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		mFrame.getContentPane().add(this.mScrabblePanel, gbc_panel);
		this.mScrabblePanel.setLayout(new GridLayout(16, 16, 0, 0));

		this.mRackTextField = new JTextField();
		this.mRackTextField.setDocument(new JTextFieldLimit(7));
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 0;
		gbc_textField_1.gridy = 1;
		mFrame.getContentPane().add(this.mRackTextField, gbc_textField_1);
		this.mRackTextField.setColumns(10);

		JButton btnNewButton = new JButton("Cheat!"); 
		btnNewButton.addActionListener(new ActionListener() {

			/**
			 * Grab the board & rack from the UI, send them to the ScrabbleBot for processing
			 */
			@Override
			public void actionPerformed(ActionEvent ae) {

				int x, y;
				ScrabbleTile scrabbleBoard[][] = new ScrabbleTile[15][15];

				for(y=1; y < 16; y++) {
					for(x = 1; x < 16; x++) {
						try {
							String tile = ((JTextField)ScrabbleBotUi.this.mScrabblePanel.getComponent(x + y*16)).getText().toLowerCase();
							scrabbleBoard[x-1][y-1] = new ScrabbleTile(tile.charAt(0), false);
							if(tile.length() > 1) {
								if(tile.charAt(1) == '*') {
									scrabbleBoard[x-1][y-1].setWildcard(true);									
								}
							}
						}
						catch(StringIndexOutOfBoundsException e) {
							scrabbleBoard[x-1][y-1] = new ScrabbleTile((char) 0, false);
						}
					}
				}

				ArrayList<Character> rackAL = new ArrayList<Character>();
				String rack = ScrabbleBotUi.this.mRackTextField.getText();
				for(int i = 0; i < rack.length(); i++) {
					rackAL.add(rack.charAt(i));
				}

		        (new ScrabbleBotSwingWorker(mFrame, mOutputTextPane)).execute(mScrabbleBot, scrabbleBoard, rackAL);
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 2;
		mFrame.getContentPane().add(btnNewButton, gbc_btnNewButton);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		mFrame.getContentPane().add(scrollPane, gbc_scrollPane);

		this.mOutputTextPane = new JTextPane();
		scrollPane.setViewportView(this.mOutputTextPane);

		JMenuBar menuBar = new JMenuBar();
		//Build the first menu.
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);

		//a group of JMenuItems
		JMenuItem menuItem = new JMenuItem("Save");
		menuItem.setMnemonic(KeyEvent.VK_S);
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//Create a file chooser
				JFileChooser fc = new JFileChooser();
				fc.setDialogType(JFileChooser.SAVE_DIALOG);
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				//In response to a button click:
				fc.setFileFilter(new SaveFileFilter());
				int returnVal = fc.showSaveDialog(mScrabblePanel);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();

					String fname = file.getName();
					if(fname.length() < 4 || !fname.substring(fname.length() - 4).equalsIgnoreCase(".sav")){
						file = new File(file.getAbsolutePath() + ".sav");
					}

					try {
						saveState(file);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		menu.add(menuItem);

		menuItem = new JMenuItem("Load");
		menuItem.setMnemonic(KeyEvent.VK_L);
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//Create a file chooser
				JFileChooser fc = new JFileChooser();
				fc.setDialogType(JFileChooser.OPEN_DIALOG);
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				//In response to a button click:
				fc.setFileFilter(new SaveFileFilter());
				int returnVal = fc.showOpenDialog(mScrabblePanel);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						loadState(fc.getSelectedFile());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		menu.add(menuItem);

		menuBar.add(menu);

		mFrame.setJMenuBar(menuBar);

		Font newTexFont = new Font("Serif", Font.BOLD, 12);
		JTextField textField = null;
		for(int y = 0; y < 16; y++) {
			for(int x = 0; x < 16; x++) {
				if(x == 0 && y == 0) {
					JLabel lbl = new JLabel();
					mScrabblePanel.add(lbl);
				}
				else if(x == 0) {
					JLabel lbl = new JLabel();
					lbl.setHorizontalAlignment(SwingConstants.RIGHT);
					lbl.setText(y - 1 + "");
					mScrabblePanel.add(lbl);
				}
				else if (y == 0) {
					JLabel lbl = new JLabel();
					lbl.setHorizontalAlignment(SwingConstants.CENTER);
					lbl.setText(x - 1 + "");
					mScrabblePanel.add(lbl);
				}
				else {
					textField = new JTextField();
					textField.setDocument(new JTextFieldLimit(2));
					textField.setFont(newTexFont);
					if(ScrabbleBot.contains(new Coord(x-1, y-1), ScrabbleBot.tripleWords)) {
						textField.setBackground(new Color(0xFFA500));
					}
					else if(ScrabbleBot.contains(new Coord(x-1, y-1), ScrabbleBot.tripleLetters)) {
						textField.setBackground(new Color(0x90ee90));
					}
					else if(ScrabbleBot.contains(new Coord(x-1, y-1), ScrabbleBot.doubleWords)) {
						textField.setBackground(new Color(0xFF8080));
					}
					else if(ScrabbleBot.contains(new Coord(x-1, y-1), ScrabbleBot.doubleLetters)) {
						textField.setBackground(new Color(0xadd8e6));
					}
					mScrabblePanel.add(textField);
				}
			}
		}

		try {
			loadState(new File(AUTO_SAVE));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		mFrame.setVisible(true);
		System.out.println();

		mFrame.setBounds(100, 100, textField.getHeight() * 16, 768);
	}

	private void loadState(File file) throws IOException {
		int y, x;

		String savedState[];
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		this.mRackTextField.setText(br.readLine());
		savedState = new String[15];
		for(int i = 0; i < 15; i++) {
			savedState[i] = br.readLine();
		}
		br.close();

		for(y=1; y < 16; y++) {
			for(x = 1; x < 16; x++) {
				String tile = savedState[y-1].charAt(x-1)+"";
				if(!tile.equals("-")) {
					if(tile.toUpperCase().equals(tile)) {
						((JTextField)(mScrabblePanel.getComponent(x + y*16))).setText(tile.toLowerCase() + "*");						
					}
					else {
						((JTextField)(mScrabblePanel.getComponent(x + y*16))).setText(tile);
					}
				}
			}
		}
	}

	private void saveState(File file) throws IOException {
		int y, x;
		ScrabbleTile scrabbleBoard[][] = new ScrabbleTile[15][15];

		for(y=1; y < 16; y++) {
			for(x = 1; x < 16; x++) {
				try {
					String tile = ((JTextField)ScrabbleBotUi.this.mScrabblePanel.getComponent(x + y*16)).getText();
					scrabbleBoard[x-1][y-1] = new ScrabbleTile(tile.charAt(0), false);
					if(tile.length() == 2 && tile.charAt(1) == '*') {
						scrabbleBoard[x-1][y-1].setWildcard(true);
					}
				}
				catch(StringIndexOutOfBoundsException e) {
					scrabbleBoard[x-1][y-1] = new ScrabbleTile((char) 0, false);
				}
			}
		}

		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(ScrabbleBotUi.this.mRackTextField.getText() + "\n"); 
		for(y=0; y < 15; y++) {
			for(x = 0; x < 15; x++) {
				if(scrabbleBoard[x][y].isEmpty()) {
					bw.write("-"); 
				}
				else {
					if(scrabbleBoard[x][y].isWildcard()) {
						bw.write((scrabbleBoard[x][y].getLetter()+"").toUpperCase());
					}
					else {
						bw.write(scrabbleBoard[x][y].getLetter());
					}
				}
			}
			bw.write("\n"); 
		}
		bw.close();
	}
}
