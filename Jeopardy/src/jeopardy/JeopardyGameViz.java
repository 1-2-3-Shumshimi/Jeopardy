package jeopardy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class JeopardyGameViz extends JFrame{

	private int currentRound;
	private JeopardyGame game;
	private JPanel masterPane;
	private JPanel gamePane;
	private JPanel titlePane;
	private JPanel questionPane;
	private JPanel answerPane;
	private JPanel scorePane;
	private JPanel finalJeopardyPane;
	private JPanel finalJeopardyQuestion;
	private JPanel finalJeopardyAnswer;
	private JPanel finalPane;
	private File file;
	private Player[] players;
	private Category[] cat1;
	private Category[] cat2;
	private Category finalJ;
	private int[] finalJWagers;
	private String[] finalJAnswers;
	private final Font smallFont = new Font("SansSerif", Font.PLAIN, 14);
	private final Font mediumFont = new Font("SansSerif", Font.BOLD, 18);
	private final Font bigFont = new Font("SansSerif", Font.BOLD, 48);
	private final int[] cat1PV = {200, 400, 600, 800, 1000};
	private final int[] cat2PV = {400, 800, 1200, 1600, 2000};

	public JeopardyGameViz() throws IOException{

		// Initialize the master
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		masterPane = new JPanel(new BorderLayout());
		setBounds(100, 100, 900, 600);

		currentRound = 1;

		// Make title screen
		makeTitlePane();
		masterPane.add(titlePane, BorderLayout.CENTER);


		setContentPane(masterPane);

	}

	/**
	 * A method that creates the main menu. Here the player can start a Jeopardy game by either 
	 * choosing from an actual Jeopardy game or importing their own text file. There is also
	 * the option of setting the number of players and whether there will be daily doubles or not.
	 * @throws IOException 
	 */
	public void makeTitlePane() throws IOException {

		titlePane = new JPanel(new BorderLayout());

		JPanel logo = new JPanel();
		JLabel logoPic = new JLabel(new ImageIcon(ImageIO.read(new File("src/jeopardy/jeopardy_logo.png"))));
		logo.add(logoPic);
		titlePane.add(logo, BorderLayout.CENTER);

		// Adding the UI for the title page
		JPanel titleUI = new JPanel();

		JLabel numTeamsLab = new JLabel("Number of teams: ");
		numTeamsLab.setFont(mediumFont);

		String[] numTeamList = {"1", "2", "3", "4", "5", "6"};
		JComboBox numTeams = new JComboBox(numTeamList);
		numTeams.setSelectedIndex(-1);

		JTextArea messages = new JTextArea();
		messages.setEditable(false);
		messages.setLineWrap(true);
		messages.setWrapStyleWord(true);
		messages.setText("");

		JButton fileImportBtn = new JButton("Import .txt file");
		fileImportBtn.setVisible(false);
		fileImportBtn.addActionListener(
				new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent arg0) {

						JFileChooser fileChooser = new JFileChooser();
						int result = fileChooser.showOpenDialog(null);
						if (result == JFileChooser.APPROVE_OPTION){
							file = fileChooser.getSelectedFile();
							messages.setText(messages.getText() + "\n" + "File imported!");
						}
					}
				});

		JLabel modeLab = new JLabel("Mode: ");
		modeLab.setFont(mediumFont);

		String[] modeList = {"Imported Game", "Random Jeopardy"};
		JComboBox modes = new JComboBox(modeList);
		modes.setSelectedIndex(-1);
		modes.addItemListener(
				new ItemListener(){

					@Override
					public void itemStateChanged(ItemEvent e) {

						if (e.getStateChange() == ItemEvent.SELECTED && e.getItem().equals("Imported Game")) {
							fileImportBtn.setVisible(true);
						} else if (fileImportBtn.isVisible()){
							fileImportBtn.setVisible(false);
						}	
					}
				});

		JButton startBtn = new JButton("Start Jeopardy!");
		startBtn.addActionListener(
				new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent arg0) {

						if ((modes.getSelectedIndex() == -1 || numTeams.getSelectedIndex() == -1)||
								(modes.getSelectedItem().equals("Imported Game") && file == null)){
							messages.setText(messages.getText() + "\n" + "There is an error, try again.");
						} 
						else if (modes.getSelectedItem().equals("Imported Game")){
							try {
								game = new JeopardyGame(file);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} 
						else { // random Jeopardy version
							try {
								game = new JeopardyGame();
							} catch (IOException e) {
								e.printStackTrace();
							} 
						}
						setCategories(game);
						players = new Player[numTeams.getSelectedIndex() + 1];

						// Ask for the player's names
						JOptionPane askNamesOption = new JOptionPane();
						JTextField[] playerTextFields = new JTextField[players.length];
						JPanel namePane = new JPanel();
						namePane.setLayout(new GridLayout(players.length, 2, 4, 4));
						for (int i = 0; i < players.length; i++){
							namePane.add(new JLabel("Team " + (i+1) + "'s name: "));
							playerTextFields[i] = new JTextField(12);
							namePane.add(playerTextFields[i]);
						}

						int result = JOptionPane.showConfirmDialog(null, namePane, 
								"What are your team names?", JOptionPane.OK_CANCEL_OPTION);
						if (result == JOptionPane.OK_OPTION) {
							for (int i = 0; i < players.length; i++){
								players[i] = new Player(playerTextFields[i].getText());
							}
						}

						makeGamePane(1);
						makeScorePane();
						masterPane.remove(titlePane);
						masterPane.add(gamePane, BorderLayout.CENTER);
						masterPane.add(scorePane, BorderLayout.SOUTH);
						masterPane.updateUI();

					}

				});

		titleUI.add(numTeamsLab);
		titleUI.add(numTeams);
		titleUI.add(modeLab);
		titleUI.add(modes);
		titleUI.add(fileImportBtn);
		titleUI.add(startBtn);
		titleUI.add(messages);

		titlePane.add(titleUI, BorderLayout.SOUTH);

		// Storing the category data (after making a selection)
		// Set number of players

	}

	/**
	 * A method that creates the game board and its player interactions. It is a UI made up of
	 * a grid where the top row are the categories and under it, the questions that are associated
	 * with that category. 
	 * @param roundNum Determines which Category array to use
	 */
	public void makeGamePane(int roundNum){

		gamePane = new JPanel(new BorderLayout());

		JPanel questionGrid = new JPanel(new GridLayout(6, 6, 6, 6));
		questionGrid.setBorder(new EmptyBorder(5, 5, 5, 5));
		JLabel[][] questionCoord = new JLabel[6][6];

		// is it DJ?
		Category[] useCat;
		int[] pointValues;
		if (roundNum == 1){
			useCat = cat1;
			pointValues = cat1PV;
		} else {
			useCat = cat2;
			pointValues = cat2PV;
		}

		// Add the categories
		for (int i = 0; i < 6; i++){
			questionCoord[i][0] = new JLabel();
			questionCoord[i][0].setOpaque(true);
			questionCoord[i][0].setBackground(Color.BLUE);

			questionCoord[i][0].setForeground(Color.WHITE);
			questionCoord[i][0].setHorizontalAlignment(SwingConstants.CENTER);
			questionCoord[i][0].setFont(mediumFont);

			boolean hasComments = !useCat[i].getComments().equals("");
			String textTitle = "";
			if (hasComments){ // adds the asterisk to let users know to look for a comment, which is stored as an mouse listener
				textTitle = "<html><body style='width: " + questionCoord[0][0].getBounds().getWidth() + "px'>" + useCat[i].getTitle() + "*";
				final int x = i;
				final String finalTextTitle = textTitle;
				questionCoord[i][0].addMouseListener(
						new MouseListener(){

							@Override
							public void mouseClicked(MouseEvent arg0) {
								// Do nothing
							}

							@Override
							public void mouseEntered(MouseEvent arg0) {
								// Change to text comment
								String textComment = "<html><body style='width: " + questionCoord[0][0].getBounds().getWidth() + "px'>" + useCat[x].getComments();
								questionCoord[x][0].setFont(smallFont);
								questionCoord[x][0].setText(textComment);
							}

							@Override
							public void mouseExited(MouseEvent arg0) {
								questionCoord[x][0].setFont(mediumFont);
								questionCoord[x][0].setText(finalTextTitle);
							}

							@Override
							public void mousePressed(MouseEvent arg0) {
								// Do nothing
							}

							@Override
							public void mouseReleased(MouseEvent arg0) {
								// Do nothing
							}
						});
			} else {
				textTitle = "<html><body style='width: " + questionCoord[0][0].getBounds().getWidth() + "px'>" + useCat[i].getTitle();
			}

			try {
				questionCoord[i][0].setText(textTitle);
			} catch (Exception e) {
				questionCoord[i][0].setText("Category " + i);
			}

			questionGrid.add(questionCoord[i][0]);
		}

		// Add the point values
		for (int j = 1; j < 6; j++){
			for (int i = 0; i < 6; i++){
				questionCoord[i][j] = new JLabel();
				questionCoord[i][j].setOpaque(true);
				questionCoord[i][j].setBackground(Color.BLUE);

				questionCoord[i][j].setForeground(Color.WHITE);
				questionCoord[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				questionCoord[i][j].setFont(bigFont);
//				System.out.println("cat: " + i + " and answer: " + j);
				if (!useCat[i].getQuestions()[j-1].getIsAnswered())
					questionCoord[i][j].setText("" + pointValues[j-1]);

				// Add question action listeners

				final int x = i;
				final int y = j;
				questionCoord[i][j].addMouseListener(
						new MouseListener(){

							@Override
							public void mouseClicked(MouseEvent arg0) {

								QA useQ = useCat[x].getQuestions()[y-1];
								// Daily double condition
								if (useQ.isDailyDouble()){

									JOptionPane dailyDoubleWager = new JOptionPane();
									JPanel wagerPane = new JPanel(new GridLayout(2, 2, 4, 4));

									JLabel wager = new JLabel("Wager: ");
									wager.setVisible(false);

									SpinnerModel wageModel;
									if (currentRound == 1){
										wageModel = new SpinnerNumberModel(0, 0, 1000, 100);
									} else {
										wageModel = new SpinnerNumberModel(0, 0, 2000, 100);
									}
									JSpinner wageAmount = new JSpinner(wageModel);
									wageAmount.setVisible(false);

									JLabel team = new JLabel("Team: ");

									String[] teamOptions = new String[players.length];
									for (int i = 0; i < players.length; i++){
										teamOptions[i] = players[i].getName();
									}
									JComboBox teamNames = new JComboBox(players);
									teamNames.setSelectedIndex(-1);
									teamNames.addItemListener(
											new ItemListener(){

												@Override
												public void itemStateChanged(ItemEvent e) {

													if (e.getStateChange() == ItemEvent.SELECTED) {
														Player selectedPlayer = (Player) teamNames.getSelectedItem();
														if (selectedPlayer.getScore() > 1000*currentRound){
															SpinnerModel newWageModel = new SpinnerNumberModel(useQ.getValue(), 0, selectedPlayer.getScore(), 100);
															wageAmount.setModel(newWageModel);
														}
														wager.setVisible(true);
														wageAmount.setVisible(true);
													} else {
														wager.setVisible(false);
														wageAmount.setVisible(false);
													}
												}

											});

									wagerPane.add(team);
									wagerPane.add(teamNames);
									wagerPane.add(wager);
									wagerPane.add(wageAmount);
									dailyDoubleWager.add(wagerPane);

									while (true){
										int result = JOptionPane.showConfirmDialog(null, wagerPane, 
												"DAILY DOUBLE", JOptionPane.OK_CANCEL_OPTION);
										if (result == JOptionPane.OK_OPTION && teamNames.getSelectedIndex() != -1) {
											useQ.setValue((int)wageAmount.getValue());
											System.out.println("question value is now: " + useQ.getValue());
											break;
										}
									}


								}

								makeQuestionPane(useCat[x], y-1);
								masterPane.remove(gamePane);
								masterPane.remove(scorePane);
								masterPane.add(questionPane);
								masterPane.updateUI();
							}

							@Override
							public void mouseEntered(MouseEvent arg0) {
								// Do nothing								
							}

							@Override
							public void mouseExited(MouseEvent arg0) {
								// Do nothing								
							}

							@Override
							public void mousePressed(MouseEvent arg0) {
								// Do nothing
							}

							@Override
							public void mouseReleased(MouseEvent arg0) {
								// Do nothing
							}
						});

				questionGrid.add(questionCoord[i][j]);
			}
		}

		gamePane.add(questionGrid, BorderLayout.CENTER);

	}

	/**
	 * Creates the panel where the screen shows the text of the selected question. 
	 * Takes into account the daily double by having a pop-up window asking for the 
	 * bet value amount. User then clicks next to see the answer. A pop-up window will
	 * ask whether the player got it correct or not. The player's score will be adjusted
	 * accordingly.
	 * @param cat
	 * @param questionIndex
	 */
	public void makeQuestionPane(Category cat, int questionIndex) {

		questionPane = new JPanel(new BorderLayout());

		QA useQ = cat.getQuestions()[questionIndex];
		useQ.justAnswered();

		JLabel qLab = new JLabel();

		qLab.setOpaque(true);
		qLab.setHorizontalAlignment(SwingConstants.CENTER);
		qLab.setAlignmentX(Component.CENTER_ALIGNMENT);
		qLab.setBackground(Color.BLUE);
		qLab.setForeground(Color.WHITE);
		qLab.setFont(bigFont);

		qLab.setText("<html><body style='width: " + masterPane.getBounds().getWidth()/1.5 + "px'>" + useQ.getQuestion());

		qLab.addMouseListener(
				new MouseListener(){

					@Override
					public void mouseClicked(MouseEvent arg0) {

						makeAnswerPane(useQ);
						masterPane.remove(questionPane);
						masterPane.add(answerPane);
						masterPane.updateUI();

					}

					@Override
					public void mouseEntered(MouseEvent arg0) {
						// Do nothing
					}

					@Override
					public void mouseExited(MouseEvent arg0) {
						// Do nothing
					}

					@Override
					public void mousePressed(MouseEvent arg0) {
						// Do nothing
					}

					@Override
					public void mouseReleased(MouseEvent arg0) {
						// Do nothing
					}

				});

		questionPane.add(qLab);

	}

	public void makeAnswerPane(QA question){

		answerPane = new JPanel(new BorderLayout());

		JLabel aLab = new JLabel();

		aLab.setOpaque(true);
		aLab.setHorizontalAlignment(SwingConstants.CENTER);
		aLab.setAlignmentX(Component.CENTER_ALIGNMENT);
		aLab.setBackground(Color.BLUE);
		aLab.setForeground(Color.WHITE);
		aLab.setFont(bigFont);
		aLab.setText("<html><body style='width: " + masterPane.getBounds().getWidth()/1.5 + "px'>" + question.getAnswer());

		aLab.addMouseListener(
				new MouseListener(){

					@Override
					public void mouseClicked(MouseEvent e) {

						// Update player
						JOptionPane updatePlayer = new JOptionPane();
						JPanel updatePlayerPane = new JPanel(new GridLayout(players.length, 2, 4, 4));
						
						String[] options = {"No answer", "Correct", "Wrong"};
						JComboBox[] optionList = new JComboBox[players.length];
						for (int i = 0; i < players.length; i++){
							optionList[i] = new JComboBox(options);
							optionList[i].setSelectedIndex(0);
						}
						
						for (int i = 0; i < players.length; i++){
							updatePlayerPane.add(new JLabel(players[i].getName() + ": "));
							updatePlayerPane.add(optionList[i]);
						}
						
						updatePlayer.add(updatePlayerPane);
						
						while (true){
							int result = JOptionPane.showConfirmDialog(null, updatePlayerPane, 
									"Responses", JOptionPane.OK_CANCEL_OPTION);
							if (result == JOptionPane.OK_OPTION) {
								for (int i = 0; i < optionList.length; i++){
									if (optionList[i].getSelectedIndex() == 1){
										players[i].updateScore(question, true);
									} else if (optionList[i].getSelectedIndex() == 2){
										players[i].updateScore(question, false);
									}
								}
								break;
							}
						}
						
						// Go to game pane
						masterPane.remove(answerPane);
						updateRound();
						if (currentRound == 3){
							makeFinalJeopardyPane();
							masterPane.add(finalJeopardyPane, BorderLayout.CENTER);
							masterPane.updateUI();
						} else {
							makeGamePane(currentRound);
							makeScorePane();
							masterPane.add(gamePane);
							masterPane.add(scorePane, BorderLayout.SOUTH);
							masterPane.updateUI();
						}
					}

					@Override
					public void mouseEntered(MouseEvent arg0) {
						// Do nothing
					}

					@Override
					public void mouseExited(MouseEvent arg0) {
						// Do nothing
					}

					@Override
					public void mousePressed(MouseEvent arg0) {
						// Do nothing
					}

					@Override
					public void mouseReleased(MouseEvent arg0) {
						// Do nothing
					}

				});

		answerPane.add(aLab);
	}
	
	/**
	 * The UI that shows players the current scores
	 */
	public void makeScorePane(){
		scorePane = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
		
		JButton titleBtn = new JButton("Title Page");
		titleBtn.addActionListener(
				new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent arg0) {
						try {
							makeTitlePane();
						} catch (IOException e) {
							e.printStackTrace();
						}
						masterPane.remove(gamePane);
						masterPane.remove(scorePane);
						file = null;
						players = null;
						game = null;
						masterPane.add(titlePane);
						masterPane.updateUI();
						
					}
					
				});
		
		scorePane.add(titleBtn);
		
		for (int i = 0; i < players.length; i++){
			JLabel label = new JLabel(players[i].getName() + ": " + players[i].getScore());
			if (players.length > 3){
				label.setFont(mediumFont);
			} else {
				label.setFont(bigFont);
			}
			scorePane.add(label);
		}
		
		JButton skipRoundBtn = new JButton("Skip Round");
		
		skipRoundBtn.addActionListener(
				new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent arg0) {
						currentRound++;
						if (currentRound == 2){
							masterPane.remove(gamePane);
							makeGamePane(2);
							masterPane.add(gamePane, BorderLayout.CENTER);
							masterPane.updateUI();
						} else {
							masterPane.remove(gamePane);
							masterPane.remove(scorePane);
							makeFinalJeopardyPane();
							masterPane.add(finalJeopardyPane, BorderLayout.CENTER);
							masterPane.updateUI();
						}
						
					}
					
				});
		
		scorePane.add(skipRoundBtn);
	}
	
	/**
	 * Sets up the JPanel for Final Jeopardy wagers and shows the Final Jeopardy category
	 */
	public void makeFinalJeopardyPane(){
		
		finalJeopardyPane = new JPanel();
		finalJeopardyPane.setBackground(Color.BLUE);
		finalJeopardyPane.setLayout(new BoxLayout(finalJeopardyPane, BoxLayout.PAGE_AXIS));
		finalJeopardyPane.setOpaque(true);
		finalJeopardyPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JLabel headerLab = new JLabel("The category for Final Jeopardy is: ");
		headerLab.setOpaque(true);
		headerLab.setFont(mediumFont);
		headerLab.setHorizontalAlignment(SwingConstants.CENTER);
		headerLab.setAlignmentX(Component.CENTER_ALIGNMENT);
		headerLab.setBackground(Color.BLUE);
		headerLab.setForeground(Color.WHITE);
		
		JLabel centerLab = new JLabel(finalJ.getTitle());
		centerLab.setOpaque(true);
		centerLab.setFont(bigFont);
		centerLab.setHorizontalAlignment(SwingConstants.CENTER);
		centerLab.setAlignmentX(Component.CENTER_ALIGNMENT);
		centerLab.setBackground(Color.BLUE);
		centerLab.setForeground(Color.WHITE);
		
		finalJeopardyPane.add(Box.createRigidArea(new Dimension(0, 50)));
		finalJeopardyPane.add(headerLab);
		finalJeopardyPane.add(Box.createRigidArea(new Dimension(0, 150)));
		finalJeopardyPane.add(centerLab);
		finalJeopardyPane.add(Box.createRigidArea(new Dimension(0, 150)));

		JButton toWagerBtn = new JButton("Wager on Final Jeopardy");
		toWagerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		toWagerBtn.addActionListener(

				new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {

						JOptionPane askWagerOption = new JOptionPane();
						JPasswordField[] amountsWaged = new JPasswordField[players.length];
						JPanel scoreAndWager = new JPanel(new GridLayout(players.length, 2, 4, 4));
						for (int i = 0; i < players.length; i++){
							JLabel playerLab = new JLabel(players[i].getName() + "'s score: " + players[i].getScore());
							scoreAndWager.add(playerLab);

							if (players[i].getScore() < 0){
								scoreAndWager.add(new JLabel("A player with a negative score cannot play Final Jeopardy"));
								amountsWaged[i] = null;
							} else {
								amountsWaged[i] = new JPasswordField();
								amountsWaged[i].setToolTipText("The amount that can be waged range from 0 to " + players[i].getScore());
								scoreAndWager.add(amountsWaged[i]);
							}
						}

						// Must see if the players made valid wagers
						boolean error = false;
						finalJWagers = new int[amountsWaged.length];
						do {
							int result = JOptionPane.showConfirmDialog(null, scoreAndWager, 
									"Final Jeopardy Wagers", JOptionPane.OK_CANCEL_OPTION);
							if (result == JOptionPane.OK_OPTION) {
								error = false;
								for (int i = 0; i < amountsWaged.length; i++){
									if (amountsWaged[i] != null){
										try {
											String wagerString = String.valueOf(amountsWaged[i].getPassword());
											finalJWagers[i] = Integer.parseInt(wagerString);
											if (finalJWagers[i] > players[i].getScore() || finalJWagers[i] < 0){
												error = true;
												amountsWaged[i].setBackground(Color.RED);
											} else {
												amountsWaged[i].setBackground(Color.WHITE);
											}
										} catch (NumberFormatException e1) {
											error = true;
											amountsWaged[i].setBackground(Color.RED);
										}
									}
								}
							} else {
								error = true;
							}
						} while (error);

						makeFinalJeopardyQuestionPane(finalJ.getQuestions()[0]);
						masterPane.remove(finalJeopardyPane);
						masterPane.add(finalJeopardyQuestion);
						masterPane.updateUI();

					}	
				});
		
		finalJeopardyPane.add(toWagerBtn);
	}

	/**
	 * Sets up the JPabel for the Final Jeopardy question. This includes the hidden answers to the
	 * question as well.
	 */
	public void makeFinalJeopardyQuestionPane(QA question){
		
		finalJeopardyQuestion = new JPanel(new BorderLayout());
		finalJeopardyQuestion.setBackground(Color.BLUE);
		finalJeopardyQuestion.setOpaque(true);
		
		JLabel qLab = new JLabel();
		qLab.setText("<html><body style='width: " + masterPane.getBounds().getWidth()/1.5 + "px'>" + question.getQuestion());
		qLab.setFont(bigFont);
		qLab.setHorizontalAlignment(SwingConstants.CENTER);
		qLab.setAlignmentX(Component.CENTER_ALIGNMENT);
		qLab.setOpaque(true);
		qLab.setForeground(Color.WHITE);
		qLab.setBackground(Color.BLUE);
		finalJeopardyQuestion.add(qLab, BorderLayout.CENTER);
		
		qLab.addMouseListener(
				new MouseListener(){

					@Override
					public void mouseClicked(MouseEvent arg0) {


						JOptionPane askAnswers = new JOptionPane();
						JPasswordField[] givenAnswers = new JPasswordField[players.length];
						JPanel teamAnswers = new JPanel(new GridLayout(players.length, 2, 4, 4));
						for (int i = 0; i < players.length; i++){
							JLabel playerLab = new JLabel(players[i].getName() + ": ");
							teamAnswers.add(playerLab);

							if (players[i].getScore() < 0){
								teamAnswers.add(new JLabel("A player with a negative score cannot play Final Jeopardy"));
								givenAnswers[i] = null;
							} else {
								givenAnswers[i] = new JPasswordField();
								givenAnswers[i].setToolTipText("The amount that can be waged range from 0 to " + players[i].getScore());
								teamAnswers.add(givenAnswers[i]);
							}
						}

						// Must see if the players made valid wagers
						boolean error = false;
						finalJAnswers = new String[givenAnswers.length];
						do {
							int result = JOptionPane.showConfirmDialog(null, teamAnswers, 
									"Final Jeopardy Answers", JOptionPane.OK_CANCEL_OPTION);
							if (result == JOptionPane.OK_OPTION) {
								error = false;
								for (int i = 0; i < givenAnswers.length; i++){
									if (givenAnswers[i] != null){
										String wagerString = String.valueOf(givenAnswers[i].getPassword());
										finalJAnswers[i] = wagerString;
										if (finalJAnswers[i].length() == 0){
											error = true;
											givenAnswers[i].setBackground(Color.RED);
										} else {
											givenAnswers[i].setBackground(Color.WHITE);
										}
									}
								}
							} else {
								error = true;
							}
						} while (error);

						makeFinalJeopardyAnswerPane(finalJ.getQuestions()[0]);
						masterPane.remove(finalJeopardyQuestion);
						masterPane.add(finalJeopardyAnswer);
						masterPane.updateUI();
						
					}

					@Override
					public void mouseEntered(MouseEvent arg0) {
						//do nothing
					}
					@Override
					public void mouseExited(MouseEvent arg0) {
						//do nothing
					}
					@Override
					public void mousePressed(MouseEvent arg0) {
						//do nothing
					}
					@Override
					public void mouseReleased(MouseEvent arg0) {
						//do nothing
					}
					
				});
	}
	
	/**
	 * Sets up the Final Jeopardy answer and asks the user which teams got their answer correct.
	 * @param question
	 */
	public void makeFinalJeopardyAnswerPane(QA question){
		
		finalJeopardyAnswer = new JPanel(new BorderLayout());

		JLabel aLab = new JLabel();
		aLab.setText("<html><body style='width: " + masterPane.getBounds().getWidth()/1.5 + "px'>" + question.getAnswer());
		aLab.setFont(bigFont);
		aLab.setHorizontalAlignment(SwingConstants.CENTER);
		aLab.setAlignmentX(Component.CENTER_ALIGNMENT);
		aLab.setOpaque(true);
		aLab.setForeground(Color.WHITE);
		aLab.setBackground(Color.BLUE);
		finalJeopardyAnswer.add(aLab, BorderLayout.CENTER);
		
		aLab.addMouseListener(
				new MouseListener(){

					@Override
					public void mouseClicked(MouseEvent e) {
						// Scoring final Jeopardy
						JOptionPane updatePlayer = new JOptionPane();
						JPanel updatePlayerPane = new JPanel(new GridLayout(players.length, 2, 4, 4));
						
						String[] options = {"Correct", "Wrong"};
						JComboBox[] optionList = new JComboBox[players.length];
						for (int i = 0; i < players.length; i++){
							if (players[i].getScore() < 0){
								optionList[i] = null;
							} else {
								optionList[i] = new JComboBox(options);
								optionList[i].setSelectedIndex(-1);
							}
						}
						
						for (int i = 0; i < players.length; i++){
							if (optionList[i] == null){
								updatePlayerPane.add(new JLabel(players[i].getName() + " did not play."));
								updatePlayerPane.add(new JLabel());
							} else {
								updatePlayerPane.add(new JLabel(players[i].getName() + "'s answer was: " + finalJAnswers[i]));
								updatePlayerPane.add(optionList[i]);
							}
						}
						
						updatePlayer.add(updatePlayerPane);
						
						boolean error = false;
						do {
							int result = JOptionPane.showConfirmDialog(null, updatePlayerPane, 
									"Final Jeopardy Scoring", JOptionPane.OK_CANCEL_OPTION);
							if (result == JOptionPane.OK_OPTION) {
								error = false;
								for (int i = 0; i < optionList.length; i++){
									if (optionList[i] != null){
										if (optionList[i].getSelectedIndex() == -1){
											error = true;
										} else if (optionList[i].getSelectedIndex() == 0){
											players[i].updateScore(finalJWagers[i], true);
										} else {
											players[i].updateScore(finalJWagers[i], false);
										}
									}
								}
							} else {
								error = true;
							}
						} while (error);
						
						makeFinalPane();
						masterPane.remove(finalJeopardyAnswer);
						masterPane.add(finalPane);
						masterPane.updateUI();
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						// Do nothing
						
					}

					@Override
					public void mouseExited(MouseEvent e) {
						// Do nothing
						
					}

					@Override
					public void mousePressed(MouseEvent e) {
						// Do nothing
						
					}

					@Override
					public void mouseReleased(MouseEvent e) {
						// Do nothing
						
					}
					
				});
	}

	/**
	 * Sets-up the pane that shows the users the final scores and declare the winner
	 */
	public void makeFinalPane(){
		finalPane = new JPanel();
		finalPane.setLayout(new BoxLayout(finalPane, BoxLayout.PAGE_AXIS));
		finalPane.setOpaque(true);
		finalPane.setBackground(Color.BLUE);
		
		Player winner = players[0];
		for (int i = 1; i < players.length; i++){
			if (players[i].getScore() > winner.getScore())
				winner = players[i];
		}
		
		JLabel headerLab = new JLabel("Our Jeopardy Champion is: " + winner.getName() + "!");
		headerLab.setOpaque(true);
		headerLab.setFont(bigFont);
		headerLab.setHorizontalAlignment(SwingConstants.CENTER);
		headerLab.setAlignmentX(Component.CENTER_ALIGNMENT);
		headerLab.setBackground(Color.BLUE);
		headerLab.setForeground(Color.WHITE);
		
		JLabel centerLab = new JLabel("Final Scores: ");
		centerLab.setOpaque(true);
		centerLab.setFont(mediumFont);
		centerLab.setHorizontalAlignment(SwingConstants.CENTER);
		centerLab.setAlignmentX(Component.CENTER_ALIGNMENT);
		centerLab.setBackground(Color.BLUE);
		centerLab.setForeground(Color.WHITE);
		
		JLabel[] playerLab = new JLabel[players.length];
		for (int i = 0; i < players.length; i++){
			playerLab[i] = new JLabel(players[i].getName() + "..............." + players[i].getScore());
			playerLab[i].setOpaque(true);
			playerLab[i].setFont(mediumFont);
			playerLab[i].setHorizontalAlignment(SwingConstants.CENTER);
			playerLab[i].setAlignmentX(Component.CENTER_ALIGNMENT);
			playerLab[i].setBackground(Color.BLUE);
			playerLab[i].setForeground(Color.WHITE);
		}
		
		finalPane.add(Box.createRigidArea(new Dimension(0, 50)));
		finalPane.add(headerLab);
		finalPane.add(Box.createRigidArea(new Dimension(0, 75)));
		finalPane.add(centerLab);
		for (int i = 0; i < players.length; i++){
			finalPane.add(Box.createRigidArea(new Dimension(0, 10)));
			finalPane.add(finalPane.add(playerLab[i]));
		}
		finalPane.add(Box.createRigidArea(new Dimension(0, 75)));

		JButton titleBtn = new JButton("Play Again");
		titleBtn.addActionListener(
				new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent arg0) {
						try {
							makeTitlePane();
						} catch (IOException e) {
							e.printStackTrace();
						}
						masterPane.remove(finalPane);
						file = null;
						players = null;
						game = null;
						masterPane.add(titlePane);
						masterPane.updateUI();
						
					}
					
				});
		titleBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		finalPane.add(titleBtn);
	}
	
	/**
	 * Initializes category arrays and final Jeopardy category taken from the inputed game
	 * @param game the inputed game
	 */
	public void setCategories(JeopardyGame game){
		Category[][] categories = game.getCategories();
		cat1 = categories[0];
		cat2 = categories[1];
		finalJ = categories[2][0];
	}
	
	/**
	 * Check to see if all the questions answered in the current round have all been answered. If so, add 1 to currentRound
	 */
	public void updateRound(){
		boolean needUpdate = true;
		Category[] useCat;
		if (currentRound == 1){
			useCat = cat1;
		} else {
			useCat = cat2;
		}
		int i = 0;
		while (needUpdate){
			if (i == useCat.length){
				break;
			}
			for (int j = 0; j < useCat[i].getNumQuestions(); j++){
				if (!useCat[i].getQuestions()[j].getIsAnswered())
					needUpdate = false;
			}
			i++;
		}
		if (needUpdate)
			currentRound++;
	}
}
