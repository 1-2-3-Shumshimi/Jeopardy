package jeopardy;

public class QA {

	private String question;
	private String answer;
	private int value;
	private boolean isDailyDouble;
	private boolean isAnswered;
	private boolean ansCorrect;
	
	public QA (String q, String a, int value, boolean dd){
		this.question = q;
		this.answer = a;
		this.value = value;
		this.isDailyDouble = dd;
		this.isAnswered = false;
		this.ansCorrect = false;
	}
	
	public String toString(){
		return question + " " + answer + " " + value + " is daily double? - " + isDailyDouble;
	}
	
	public String getQuestion(){
		return question;
	}
	
	public String getAnswer(){
		return answer;
	}
	
	public boolean isDailyDouble(){
		return isDailyDouble;
	}
	
	public boolean getIsCorrect(){
		if (!isAnswered){
			System.out.println("The question " + toString() + " has not been answered yet!");
		}
		return ansCorrect;
	}
	
	public boolean getIsAnswered(){
		return isAnswered;
	}
	
	public void justAnswered(){
		isAnswered = true;
	}
	
	public int getValue(){
		return value;
	}
	
	public void setValue(int newValue){
		value = newValue;
	}
	
}
