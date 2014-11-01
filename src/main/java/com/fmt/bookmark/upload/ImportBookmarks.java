package com.fmt.bookmark.upload;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fmt.common.FileUtilities;
import com.fmt.common.URL;
import com.fmt.rest.service.BookmarkDatabase;

/**
 * class for Parsing HREFs from HTML.
 **/
public class ImportBookmarks {

	/**
	 * inserts bookmarks into database table.
	 * @param username user to import links for
	 * @param pageName page to import links into
	 * @param bookmarks list of link names and URLs to insert into database
	 * @return whether database insert was successful
	 **/
	public static boolean insertBookmarks(String username, String pageName, Hashtable<String, String> bookmarks) {
		boolean success= false;
		
		if(!bookmarks.isEmpty() && !username.isEmpty()) {
			BookmarkDatabase db= new BookmarkDatabase();
			
			List<String> existingPages= db.getPageNames(username);
			if(existingPages.contains(pageName)) {
				pageName+= Math.random();
			}
			
			db.addPage(username, pageName);
			final int paraPos= 0;
			final String paraName= "imported"+Math.random();
			db.addParagraph(username, pageName, paraName, paraPos);
			
			int linkPos= 1;
			for(Entry<String, String > bookmark : bookmarks.entrySet()) {
				db.addLink(username, pageName, paraName, paraPos, bookmark.getKey(), bookmark.getValue(), linkPos++);
			}
		}
		
		return success;
	}
	
	public static void main(String[] args) {
//		importPage("ftaylor92");
	}
	
	/**
	 * converts HTML text into a list of links and inserts list of links into database.
	 * @param username user to import links for
	 * @param html HTML string to import links from
	 * @return a list of link names and link URLs found in HTML string
	 **/
	public static Hashtable<String, String> getBookmarks(String username, String html) {
		Hashtable<String, String> urls= new Hashtable<String, String>();
		String pageName= "importedPage"+ Math.random();
		
		final String lHtml= html.toLowerCase();
		
		Pattern pattern = Pattern.compile("<title>(.*)</title>"); //".*<a.+href=(.+)>(.*)</a>.*");
		Matcher matcher = pattern.matcher(lHtml);
		while (matcher.find()) {
			System.out.print("Start index: " + matcher.start());
			System.out.println(" End index: " + matcher.end() + " ");
			pageName= matcher.group(1);
		}
		System.out.println("pageName="+ pageName);
		
		pattern = Pattern.compile("<a[^>]+href=[ \\\'\\\"]+([^\\\'\\\"]*)[^>]+>([^<]+)</a>"); //".*<a.+href=(.+)>(.*)</a>.*");
		matcher = pattern.matcher(lHtml);
		while (matcher.find()) {
			System.out.print("Start index: " + matcher.start());
			System.out.println(" End index: " + matcher.end() + " ");
			String href= matcher.group(1);
			String name= matcher.group(2);
			System.out.println("href="+ href);
			System.out.println("name="+ name);
			System.out.println(matcher.group());
			urls.put(name, href);
		}
		
		//insert into database
		insertBookmarks(username, pageName, urls);
		
		return urls;
	}
	
	/**
	 * imports links from a URL into a table associated with user.
	 * @param username user to import links for
	 * @param htmlUrl URL to import links from
	 * @return whether import was successful
	 **/
	public static boolean importPage(String username, String htmlUrl) {
		boolean success= false;
		
//		final File htmlFile= new File("/home/ftaylor92/Dropbox/Public/public-web-site/linx/bookmarks.html");
//		final String htmlUrl= "http://dl.dropboxusercontent.com/u/688127/public-web-site/linx/bookmarks.html";
		
//		try {
			//final String html= getPage(htmlFile);
			final String html= URLDecoder.decode(URL.getUrl(htmlUrl)).replaceAll("&#58;", ":");
			
			Hashtable<String, String> bookmarks= getBookmarks(username, html);
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
		
//		return false; //ex.getMessage();
			
		return success;
	}
	
	/**
	 * imports links in a file into a table associated with user.
	 * @param username user to import links for
	 * @param htmlFile file to import links from
	 * @return whether import was successful
	 **/
	public static boolean importFile(String username, File htmlFile) {
		boolean success= false;
		
		//final File htmlFile= new File("/home/ftaylor92/Dropbox/Public/public-web-site/linx/bookmarks.html");
		//final String htmlUrl= "http://dl.dropboxusercontent.com/u/688127/public-web-site/linx/bookmarks.html";
		
		try {
			final String html= getPage(htmlFile);
			//final String html= URLDecoder.decode(URL.getUrl(htmlUrl)).replaceAll("&#58;", ":");
			
			Hashtable<String, String> bookmarks= getBookmarks(username, html);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
//		return false; //ex.getMessage();
			
		return success;
	}

	/**
	 * Returns file as a string without LF or CRs
	 * @param htmlpage file to import
	 * @return file contents
	 * @throws IOException
	 **/
	public static String getPage(File htmlpage) throws IOException {

		//get html file lines
		List<String> fLines= FileUtilities.getFileLines(htmlpage);

		StringBuilder wholeFile= new StringBuilder();
		for(String line: fLines) {
			wholeFile.append(line.trim());
		}

		return wholeFile.toString();
	}


}
