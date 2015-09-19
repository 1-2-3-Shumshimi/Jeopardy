package jeopardy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;

public class JeopardyGame {

	private int roundNum;
	private Category[] categories1;
	private Category[] categories2;
	private Category finalJeopardy;
	private String qResponseTemp;
	private String qTextTemp;
	private int qValueTemp;
	private boolean qIsDDTemp;

	/**
	 * This constructor is the first mode of the Jeopardy Game which reads in a random 
	 * actual past Jeopardy game from an online database.
	 * @throws IOException
	 */
	public JeopardyGame() throws IOException{

		this.categories1 = new Category[6];
		this.categories2 = new Category[6];

		String stringURL = "http://www.j-archive.com/showgame.php?game_id="; // database URL
		int random = (int)(Math.random()*4912)+1;
		stringURL = stringURL.concat("" + random);

		URL url = new URL(stringURL);

		// Get the input stream through URL Connection
		URLConnection con = url.openConnection();
		InputStream is = con.getInputStream();

		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line = null;
		qResponseTemp = "";
		qTextTemp = "";
		qValueTemp = 0;
		qIsDDTemp = false;

		this.roundNum = 1; // 1 for jeopardy round, 2 for double jeopardy, and 3 for final

		// read each line and write to System.out
		while ((line = br.readLine()) != null) {

			// Determine which round we are looking at
			if (line.startsWith("<div id=", 12)){
				if (line.contains("jeopardy_round"))
					roundNum = 1;
				if (line.contains("double_jeopardy_round"))
					roundNum = 2;
				if (line.contains("final_jeopardy_round"))
					roundNum = 3;
				System.out.println(roundNum);
			}

			// Creating categories
			else if (line.startsWith("category_name", 17)){ 
				// looking for line "<tr><td class="category_name">[name of category]</td></tr>

				String title = processLine(line, 32);
				System.out.println(title);

				// getting next line with "category_comments"
				line = br.readLine();
				String comment = "";
				if (line.contains("category_comments"))
					comment = processLine(line, 36);
				System.out.println(comment);
				this.addCategory(new Category(title, comment));	
			}

			// Setting up questions
			else if (line.startsWith("<div onmouseover", 6)){
				// looking for the line "<div onmouseover="toggle...correct_response"

				int j = 0; // Checks front index of substring
				while (!line.startsWith("correct_response", j) && j < line.length()){
					j++;
				}

				int i = 109 + 2*roundNum; // Checks end index of substring
				while (!line.startsWith("&lt;/em", i) && i < line.length()){
					i++;
				}
				//					System.out.println(line);
				qResponseTemp = processLine(line.substring(j+26, i), 0);
				System.out.println(qResponseTemp);
			}

			else if (line.startsWith("clue_value_daily_double", 23)){
				// looking for the line "<td class="clue_value_daily_double">...

				qValueTemp = Integer.parseInt(processLine(line, 53).replaceAll(",", "")); 
				//cleans up the line so that there are no HTML or commas 

				qIsDDTemp = true;

				System.out.println(qValueTemp);
				System.out.println("This is a daily double");

				// getting the "clue_text" 8 lines down
				for (int clueT = 0; clueT < 8; clueT++){
					line = br.readLine();
				}					
				if (line.contains("clue_text"))
					qTextTemp = processLine(line, 41+roundNum);

				System.out.println(qTextTemp);
			}
			else if (line.startsWith("clue_value", 23)){
				// looking for line "<td class="clue_value">[amount]</td>

				qValueTemp = Integer.parseInt(processLine(line, 36).replaceAll(",", ""));
				//cleans up the line so that there are no HTML or commas 

				qIsDDTemp = false;

				System.out.println(qValueTemp);

				// getting the "clue_text" 8 lines down
				for (int clueT = 0; clueT < 8; clueT++){
					line = br.readLine();
					//						System.out.println(line);
				}
				if (line.contains("clue_text"))
					qTextTemp = processLine(line, 41+roundNum);

				System.out.println(qTextTemp);

			}
			else if (roundNum == 3) {
				if (line.startsWith("<div onmouseover")){

					int j = 0; // Checks front index of substring
					while (!line.startsWith("correct_response", j) && j < line.length()){
						j++;
					}

					int i = j; // Checks end index of substring
					while (!line.startsWith("&lt;/em", i) && i < line.length()){
						i++;
					}

					String finalResponse = processLine(line.substring(j+27, i), 0);
					System.out.println("final response is " + finalResponse);

					// getting the "category_name" 2 lines down
					line = br.readLine();
					line = br.readLine();
					String finalCatName = "";
					String finalCatComment = "";
					String finalCatText = "";

					if (line.contains("category_name")){
						finalCatName = processLine(line, 34);
						System.out.println("final category name is: "+ finalCatName);

						// getting "category_comments" 
						line = br.readLine();
						if (line.contains("category_comments")){
							finalCatComment = processLine(line, 38);
							System.out.println("final category comment is: " + finalCatComment);
						}

						// getting "clue_text" 9 lines down
						for (int x = 0; x < 9; x++){
							line = br.readLine();
						}
						if (line.contains("clue_text")){
							finalCatText = processLine(line, 39);
							System.out.println("final category text is: " + finalCatText);
						}

						finalJeopardy = new Category(finalCatName, finalCatComment);
						finalJeopardy.addQuestion(new QA(finalCatText, finalResponse, 0, false));
					}
				}
			}
			addQuestion(); // Adds a question to the next category if conditions are met
		}
	}

	/**
	 * This constructor is the second mode of the Jeopardy Game, which reads in a file (of predetermined
	 * format)
	 * @param file
	 * @throws IOException 
	 */
	public JeopardyGame(File file) throws IOException{
		
		this.categories1 = new Category[6];
		this.categories2 = new Category[6];
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String line = null;
		int catNum = -1;
		while ((line = br.readLine()) != null) {
			
			// determine which round we are looking at
			if (line.startsWith("Round: ")){
				roundNum = Integer.parseInt(line.substring(7, 8));
				catNum = -1;
			}
			
			// setup the category
			else if (line.startsWith("Category: ")){
				String catName = line.substring(9);
				line = br.readLine();
				String catComment = line.substring(8);
				if (roundNum == 3){
					finalJeopardy = new Category(catName, catComment);
				} else {
					addCategory(new Category(catName, catComment));
				}
				catNum++;
			}
			
			// setup questions
			else if (line.startsWith("Q")){
				String q = line.substring(4);
				line = br.readLine();
				String a = line.substring(4);
				int qVal = Integer.parseInt(line.substring(1, 2))*roundNum*200;
				QA question = new QA(q, a, qVal, false);
				if (roundNum == 1){
					categories1[catNum].addQuestion(question);
				} else if (roundNum == 2){
					categories2[catNum].addQuestion(question);
				} else {
					finalJeopardy.addQuestion(question);
				}
				
			}
		}
	}
	
	/**
	 * Reads in a line and a start index. Returns a string from the start index that removes all 
	 * HTML formatting
	 * @param line
	 * @param startIndex
	 * @return
	 */
	public static String processLine(String line, int startIndex){

		try {
			return Jsoup.parse(line.substring(startIndex, line.length())).text();
		} catch (Exception e) {
			return line;
		}
	}

	/**
	 * Adds the category to the next available index of the category arrays.
	 * Adds to categories1 if it is reading from round 1; categories 2 if it is reading from round 2
	 * @param cat the category that is being added
	 */
	public void addCategory(Category cat){
		if (roundNum == 1){
			for (int i = 0; i < categories1.length; i++){
				if (categories1[i] == null){
					categories1[i] = cat;
					break;
				}
			}
		} else if (roundNum == 2){
			for (int i = 0; i < categories2.length; i++){
				if (categories2[i] == null){
					categories2[i] = cat;
					categories2[i].setDJ(true);
					break;
				}
			}
		}
	}

	/**
	 * Only runs if the temporary question values text and response are nonempty
	 * Adds the question to the next available slot of the categories, filling all the zero indicies
	 * of the categories first, then the first, then second, and so forth. If a question is successfully added,
	 * then all the temporary question values will be reset.
	 */
	public void addQuestion(){
		if (!qResponseTemp.isEmpty() && !qTextTemp.isEmpty()){

			QA newQ = new QA(qTextTemp, qResponseTemp, qValueTemp, qIsDDTemp);

			int minNumQuestions = 7;
			Category minLengthCat;
			// Single Jeopardy condition
			if (categories2[0] == null){
				minLengthCat = categories1[0];
				for (int i = 0; i < categories1.length; i++){
					if (categories1[i].getNumQuestions() < minNumQuestions){
						minNumQuestions = categories1[i].getNumQuestions();
						minLengthCat = categories1[i];
					}
				}
			} else { // Double Jeopardy condition
				minLengthCat = categories2[0];
				for (int i = 0; i < categories2.length; i++){
					if (categories2[i].getNumQuestions() < minNumQuestions){
						minNumQuestions = categories2[i].getNumQuestions();
						minLengthCat = categories2[i];
					}
				}
			}
			minLengthCat.addQuestion(newQ);
			qTextTemp = "";
			qResponseTemp = "";
			qValueTemp = 0;
			qIsDDTemp = false;
		}
	}

	/**
	 * A getter with the categories (with their questions) as a bundle.
	 * @return Type double Category array
	 */
	public Category[][] getCategories(){
		Category[] catArrayFJ = {finalJeopardy};
		Category[][] catArrays = {categories1, categories2, catArrayFJ};
		return catArrays;
	}

}
