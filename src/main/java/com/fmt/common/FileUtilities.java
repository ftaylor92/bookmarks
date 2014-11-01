/**
 * 
 */
package com.fmt.common;

import java.io.*;
import java.util.*;

/**
 * collection of file utility functions.
**/
public class FileUtilities {
	/**
	 * returns a list of a text file's text lines
	 * @param aFile file to get lines of text from
	 * @return a list of all the text lines from the text file
	 **/
	public static List<String> getFileLines( File aFile ) throws IOException
	{
		List<String> lines= new ArrayList<String>(100);

		String aLine;
		BufferedReader in= new BufferedReader(new FileReader(aFile));

		while(in.ready())
		{
			aLine= in.readLine();
			lines.add(aLine);
		}
		in.close();

		return lines;
	}

	/** writes file lines to a file. 
	 * @param aFile file to write to
	 * @param lines the lines to write out to tis file
	**/
	public static void writeFileLines(File aFile, String lines) throws IOException {
		FileOutputStream fos= new FileOutputStream(aFile);
		PrintWriter pw= new PrintWriter(aFile);
		pw.printf(lines);
		pw.flush();
		pw.close();
		fos.flush();
		fos.close();
	}
}
