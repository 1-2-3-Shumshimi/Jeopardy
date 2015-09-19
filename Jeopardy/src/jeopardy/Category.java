package jeopardy;

public class Category {

	private boolean isDoubleJeopardy;
	private QA[] questions;
	private String title;
	private String comments;
	
	// Version from database
	public Category(String title, String comments) {
		
		this.title = title;
		this.comments = comments;
		this.questions = new QA[5];
		this.isDoubleJeopardy = false;
	}
	
	public void addQuestion(QA q){
		for (int i = 0; i < this.questions.length; i++){
			if (this.questions[i] == null){
				this.questions[i] = q;
				break;
			}
		}
	}
	
	public void printQuestions(){
		for (int i = 0; i < questions.length; i++){
			try {
				System.out.println(questions[i].toString());
			} catch (Exception e) {
				// Do nothing
			}
		}
	}
	
	public int getNumQuestions(){
		int count = 0;
		for (int i = 0; i < questions.length; i++){
			if (questions[i] != null){
				count++;
			}
		}
		return count;
	}
	
	public void setDJ(boolean isDJ){
		isDoubleJeopardy = isDJ;
	}
	
	public String toString(){
		return "title: " + title + "\n" + "comments: " + comments;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getComments(){
		return comments;
	}
	
	public QA[] getQuestions(){
		return questions;
	}
	
	public boolean getDJ(){
		return isDoubleJeopardy;
	}
}
