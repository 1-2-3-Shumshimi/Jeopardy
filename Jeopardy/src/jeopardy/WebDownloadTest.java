package jeopardy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class WebDownloadTest {

	public static void main(String[] args) throws IOException {

		String stringURL = "http://www.j-archive.com/showgame.php?game_id=";
		int random = (int)(Math.random()*4912)+1;
		stringURL = stringURL.concat("" + random);
		
		// Make a URL to the web page
		URL url = new URL(stringURL);

		// Get the input stream through URL Connection
		URLConnection con = url.openConnection();
		InputStream is =con.getInputStream();

		// Once you have the Input Stream, it's just plain old Java IO stuff.

		// For this case, since you are interested in getting plain-text web page
		// I'll use a reader and output the text content to System.out.

		// For binary content, it's better to directly read the bytes from stream and write
		// to the target file.


		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line = null;

		// read each line and write to System.out
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}
}
