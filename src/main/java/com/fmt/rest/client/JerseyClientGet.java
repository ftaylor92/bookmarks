package com.fmt.rest.client;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/** Jersey REST_WS wrapper. **/
public class JerseyClientGet {
	final static String BASE_PASS_URL= "http://password.fmtmac.cloudbees.net";
	//final static String BASE_BOOK_URL= "https://fmt-bookmarks.herokuapp.com";
	final static String BASE_BOOK_URL= "http://localhost:8080/fmt-bookmarks";

	final static String GET_EIGHT_URL_PATTERN= BASE_BOOK_URL+ "/rest/eight";

	final static String CREATE_ACCT_URL= BASE_PASS_URL+ "/rest/password?action=POST&username=%s&password=%s&site=com.fmt.bookmarks&role=user";
	final static String ADD_BOOKMARK_URL= BASE_BOOK_URL+ "/links?edit=true&user=%s&password=%s&page=%s&action=ADDLINK&link_url=%s&link_name=%s&paragraph_name=%s&oldPageName=%s&paragraph_pos=%d&link_pos=%d&desktop=false&format=%s";
	final static String GET_PAGES_URL= BASE_BOOK_URL+ "/rest/data/?password=%s&user=%s&site=%s&action=%s";
	final static String GET_PARAGRAPHS_URL= BASE_BOOK_URL+ "/rest/data/?password=%s&user=%s&page=%s&site=%s&action=%s";
	final static String GOTO_PAGES_URL= BASE_BOOK_URL+ "/page?user=%s&password=%s";

	/**
	 * main. for testing.
	 * 
	 * @param args
	 **/
	public static void main(String[] args) {
		getJavaEightWebService("ftaylor92", "ftaylor92");
		
		//getPageNames("ftaylor92", "ftaylor92");

		//getParagraphNames("ftaylor92", "ftaylor92", "articles");
	}

	/**
	 * Returns list of page names.
	 * @param user username
	 * @param password password
	 * @return list of page names
	 */
	public static List<String> getJavaEightWebService(String user, String password) {
		List<String> pages= new ArrayList<>();

		String url= String.format(GET_EIGHT_URL_PATTERN);
		System.out.println("url: "+ url);
		//new HttpGetter(HttpGetter.actions.getPageNames,  url, parent).execute();
		try {

			Client client = ClientBuilder.newClient();

			WebTarget target= client.target(url);
			
			Response response= target.request(MediaType.APPLICATION_JSON).get(Response.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			String output = (String)response.getEntity();

			System.out.println("Output from Server .... \n");
			System.out.println(output);

			boolean ok= (output.startsWith("[") && output.endsWith("]"));
			if(ok)	output= output.substring(1, output.length()-1 );
			//else return pages;
			String[] entries= output.split(",");

			for(String entry : entries) {
				String pared= entry.replaceAll("\"", "");
				if(!pared.isEmpty()) {
					pages.add(pared);
				}
			}


		} catch (Exception e) {

			e.printStackTrace();

		}

		return pages;
	}

	/**
	 * Returns list of page names.
	 * @param user username
	 * @param password password
	 * @return list of page names
	 */
	public static List<String> getPageNames(String user, String password) {
		List<String> pages= new ArrayList<>();

		String url= String.format(GET_PAGES_URL, password, user, "com.fmt.bookmarks", "GET");
		System.out.println("url: "+ url);
		//new HttpGetter(HttpGetter.actions.getPageNames,  url, parent).execute();
		try {

			Client client = ClientBuilder.newClient();

			WebTarget target= client.target(url);
			
			Response response= target.request(MediaType.APPLICATION_JSON).get(Response.class);
			
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			String output = (String)response.getEntity();

			System.out.println("Output from Server .... \n");
			System.out.println(output);

			boolean ok= (output.startsWith("[") && output.endsWith("]"));
			if(ok)	output= output.substring(1, output.length()-1 );
			//else return pages;
			String[] entries= output.split(",");

			for(String entry : entries) {
				String pared= entry.replaceAll("\"", "");
				if(!pared.isEmpty()) {
					pages.add(pared);
				}
			}


		} catch (Exception e) {

			e.printStackTrace();

		}

		return pages;
	}

	/**
	 * Gets paragraph names
	 * @param user username
	 * @param password password
	 * @param pageName page to get paragraphs from
	 * @return map of paragraphs and their positions
	 */
	public static Map<Integer, String> getParagraphNames(String user, String password, String pageName) {
		Map<Integer, String> paragraphs= new LinkedHashMap<Integer, String>();

		String url= String.format(GET_PARAGRAPHS_URL, password, user, URLEncoder.encode(pageName), "com.fmt.bookmarks", "GET");
		System.out.println("url: "+ url);
		//new HttpGetter(HttpGetter.actions.getParagraphNames,  url, parent).execute();

		try {

			Client client = ClientBuilder.newClient();

			WebTarget target= client.target(url);
			
			Response response= target.request(MediaType.APPLICATION_JSON).get(Response.class);
			
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			String output = (String)response.getEntity();

			System.out.println("Output from Server .... \n");
			System.out.println(output);

			boolean ok= (output.startsWith("[") && output.endsWith("]"));
			if(ok)	output= output.substring(1, output.length()-1 );
			//else return paragraphs;
			String[] entries= output.split(",");

			paragraphs= new LinkedHashMap<Integer, String>();
			for(String entry : entries) {
				String pared= entry.replaceAll("\"", "");
				if(!pared.isEmpty()) {
					String[] mapping= pared.split("---");
					System.out.println("mapping[0]-"+ mapping[0]);
					paragraphs.put(Integer.parseInt(mapping[1]), mapping[0]);
				}

			}


		} catch (Exception e) {

			e.printStackTrace();

		}

		return paragraphs;
	}

}
