package jeopardy;

public class Player {

	private int score;
	private final String name;
	
	public Player (String playerName){
		name = playerName;
	}
	
	/**
	 * Updates the player's score based on whether he or she answered the question correctly or not
	 * @param question the question in question
	 * @param isCorrect whether the player answered the question correctly
	 */
	public void updateScore(QA question, boolean isCorrect){
		if (isCorrect){
			score = score + question.getValue();
		} else {
			score = score - question.getValue();
		}
	}
	
	/**
	 * Updates the player's score with the indicated value (used for final Jeopardy)
	 * @param value
	 * @param isCorrect
	 */
	public void updateScore(int value, boolean isCorrect){
		if (isCorrect){
			score = score + value;
		} else {
			score = score - value;
		}
	}
	
	public String getName(){
		return name;
	}
	
	public String toString(){
		return name;
	}
	
	public int getScore(){
		return score;
	}
}
