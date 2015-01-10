package com.fmt.rest.service;

import java.net.URLDecoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.fmt.database.HerokuConnection;

/** REST-WS interface to bookmark database. **/
@Path("/data")
public class BookmarkDatabase {
	
	/**
	 * GET role, given site, user, pass
	 * @param user username
	 * @param pass password
	 * @param site site associated with user
	 * @return role, status and message in com.fmt.password.Contact
	 **/
	@GET
	//@HttpMethod("Get")
	@Produces({ MediaType.APPLICATION_JSON })	//MediaType.APPLICATION_JSON
	public Response getLinx(
			@QueryParam("user") String user,
			@QueryParam("page") String pageName,
			@QueryParam("password") String pass,
			@QueryParam("site") String site,
			@QueryParam("linkUrl") String linkUrl,
			@QueryParam("linkName") String linkName,
			@QueryParam("paragraphName") String paragraphName,
			@QueryParam("paragraphPosition") String paragraphPosition,
			@QueryParam("linkPosition") String linkPosition,
			@QueryParam("action") String action) {
		Status stat= Response.Status.OK;
		System.out.println("linkPosition=("+linkPosition+")");
		System.out.printf("GET(getLinx-BDB): user: %s, pass: %s, site: %s, pageName: %s, paragraphName: %s, action: %s, paragraphPosition: %s, linkPosition: %s, linkUrl: %s, linkName: %s\n", user, pass, site, pageName, paragraphName, action, paragraphPosition, linkPosition, linkUrl, linkName);
		
		CacheControl cc = new CacheControl();
		cc.setMaxAge(60);
		cc.setNoCache(true);
		
		List<String> linx= null;

		if(!(null == user || user.isEmpty())) {
			if(!(null == linkName || linkName.isEmpty())) {
				int lnPos= -1;
				try {
					System.out.println("lnPos-pre=("+lnPos+")");
					if(!(null == linkPosition || linkPosition.isEmpty() || linkPosition.equals("null"))) {
						lnPos= Integer.parseInt(linkPosition); 
					}
					System.out.println("lnPos-post=("+lnPos+")");
				} catch(NumberFormatException ex) {}
				if(addLink(user, pageName, paragraphName, Integer.parseInt(paragraphPosition), linkName, linkUrl, lnPos)) {
					linx= new ArrayList<>();
					linx.add("success");
				} else {
					linx= new ArrayList<>();
					linx.add("fail");
				}
			} else if(null == pageName || pageName.isEmpty()) {
				linx= getPageNames(user);
			} else if(!(null == pageName || pageName.isEmpty() || null == paragraphName || paragraphName.isEmpty())) {
				linx= getParagraphLinkNames(user, pageName, paragraphName.substring(0, paragraphName.indexOf("---")));
			} else if(!(null == pageName || pageName.isEmpty())) {
				linx= getParagraphNames(user, pageName);
			}
		}
		
		if(null == linx) {
			linx= new ArrayList<String>();
			linx.add(String.format("failed: username=%s, page=%s", user, pageName));
		}

		ResponseBuilder rb = Response.status(stat).entity(linx);
		System.out.println("Response: "+ linx.toString());
		return rb.cacheControl(cc).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods","GET").build();
	}
	
	/**
	 * returns list of page names in |-separated format.  ex: | link | link_name | paragraph_name | page_name | user | position | paragraph_position |
	 * @param user username
	 * @return names of all the pages in user's account
	**/
	public List<String> getPageNames(String user) {
		final List<String> pageNames= new ArrayList<>();
		//Add Link
		System.out.printf("SELECT page_name FROM bookmark_linx WHERE user=%s GROUP BY page_name ORDER BY page_name;\n", user);
		String sql= "SELECT page_name FROM bookmark_linx WHERE user=? GROUP BY page_name ORDER BY page_name;";
		
		try {
			PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
			preparedStatement.setString(1, user);
			ResultSet resultSet= preparedStatement.executeQuery();
			
			while(resultSet.next()) {
				pageNames.add(resultSet.getString("page_name"));
			}
			
			resultSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		HerokuConnection.close();

		return pageNames;
	}
	
	/**
	 * returns list of paragraph names in |-separated format.  ex: | link | link_name | paragraph_name | page_name | user | position | paragraph_position |
	 * @param user username
	 * @param pageName name of page
	 * @return names of all the paragraphs on page
	**/
	public List<String> getParagraphNames(String user, String pageName) {
		List<String> paragraphNames= new ArrayList<>();
		//Add Link
		System.out.printf("SELECT paragraph_name, paragraph_position FROM bookmark_linx WHERE user=%s AND page_name=%s GROUP BY paragraph_name ORDER BY paragraph_position;\n", user, pageName);
		String sql= "SELECT paragraph_name, paragraph_position FROM bookmark_linx WHERE user=? AND page_name=? GROUP BY paragraph_name ORDER BY paragraph_position;";
		
		try {
			PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, pageName);
			ResultSet resultSet= preparedStatement.executeQuery();
			
			while(resultSet.next()) {
				paragraphNames.add(resultSet.getString("paragraph_name")+"---"+resultSet.getInt("paragraph_position"));
			}
			
			resultSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		HerokuConnection.close();

		return paragraphNames;
	}
	
	/**
	 * returns list of paragraph names in |-separated format.  ex: | link | link_name | paragraph_name | page_name | user | position | paragraph_position |
	 * @param user username
	 * @param pageName name of page
	 * @return names of all the paragraphs on page
	**/
	public List<String> getParagraphLinkNames(String user, String pageName, String paragraphName) {
		List<String> linkNames= new ArrayList<>();
		//Add Link
		System.out.printf("SELECT link_name FROM bookmark_linx WHERE user='%s' AND page_name='%s' AND paragraph_name='%s' ORDER BY position;\n", user, pageName, paragraphName);
		String sql= "SELECT link_name FROM bookmark_linx WHERE user=? AND page_name=? AND paragraph_name=? ORDER BY position;";
		
		try {
			PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, pageName);
			preparedStatement.setString(3, paragraphName);
			ResultSet resultSet= preparedStatement.executeQuery();
			
			while(resultSet.next()) {
				//link_name
				linkNames.add(resultSet.getString("link_name"));
			}
			
			resultSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		HerokuConnection.close();

		return linkNames;
	}
	
	/** adds a page to user's account.
	 * @param user username
	 * pageName name of page to add
	 * @boolean whether page was successfully added
	**/
	public boolean addPage(String user, String pageName) {
		boolean success= false;
		
		try {
			PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement("INSERT INTO bookmark_page (user_id, page_name) VALUES ((SELECT id FROM rest_users WHERE user_name=? AND site_name=?),?)");
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, "com.fmt.bookmarks");
			preparedStatement.setString(3, pageName);
			
			/*preparedStatement.setString(3, "firstParagraph");
			preparedStatement.setInt(4, 0);
			preparedStatement.setInt(5, 0);
			preparedStatement.setString(6, "blankEntry");
			preparedStatement.setString(7, "");*/
			success= !preparedStatement.execute();
			preparedStatement.close();
			
			preparedStatement= HerokuConnection.getConnection().prepareStatement("SELECT * FROM bookmark_page WHERE user_id=(SELECT id FROM rest_users WHERE user_name=? AND site_name=?) AND page_name=?");
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, "com.fmt.bookmarks");
			preparedStatement.setString(3, pageName);
			ResultSet resultSet= preparedStatement.executeQuery();
			
			if(resultSet.next()) {
				final int pageId= resultSet.getInt("id");

				if(addParagraph(user, pageName, "firstParagraph", 1)) {
					if(addLink(user, pageName, "firstParagraph", 1, "blankEntry", "", 1)) {
						success= true;
					}
				}
			}
			
			resultSet.close();
			preparedStatement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Statement statement = null;

		try{
		    statement = HerokuConnection.getConnection().createStatement();

		    statement.addBatch(String.format("INSERT INTO bookmark_page (user_id, page_name) VALUES ((SELECT id FROM rest_users WHERE user_name='%s' AND site_name='com.fmt.bookmarks')", user, pageName));
		    statement.addBatch("update people set firstname='Eric' where id=456");
		    statement.addBatch("update people set firstname='May'  where id=789");

		    int[] recordsAffected = statement.executeBatch();
		} catch (SQLException ex) {
		} finally {
			try {
			    if(statement != null) statement.close();
			} catch (SQLException ex) {}
		}
		
		/*System.out.printf("INSERT INTO bookmark_linx(user, page_name, paragraph_name, paragraph_position, position, link_name, link) VALUES (%s, %s, %s, %d, %d, %s, %s)\n", user, pageName, "firstParagraph", 0, 0, "blankEntry", "");
		final String sql= "INSERT INTO bookmark_linx(user, page_name, paragraph_name, paragraph_position, position, link_name, link) VALUES (?, ?, ?, ?, ?, ?, ?);";

		try {
			PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, pageName);
			preparedStatement.setString(3, "firstParagraph");
			preparedStatement.setInt(4, 0);
			preparedStatement.setInt(5, 0);
			preparedStatement.setString(6, "blankEntry");
			preparedStatement.setString(7, "");
			success= !preparedStatement.execute();
			preparedStatement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
		
		HerokuConnection.close();
		
		return success;
	}
	
	/** deletes a page from user's account.
	 * @param user username
	 * @param pageName name of page to delete
	 * @boolean whether page was successfully deleted
	**/
	public boolean deletePage(String user, String pageName) {
		boolean success= false;
		
		System.out.printf("DELETE FROM bookmark_linx WHERE user=%s AND page_name=%s;\n", user, pageName);
		final String sql= "DELETE FROM bookmark_linx WHERE user=? AND page_name=?;";

		try {
			PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, pageName);
			success= !preparedStatement.execute();
			preparedStatement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		HerokuConnection.close();
		return success;
	}
	
	/** changes the name of a page in user's account.
	 * @param user username
	 * @param oldPageName name of page to change
	 * @param pageName new name of page
	 * @boolean whether page name was successfully change
	**/
	public boolean editPage(String user, String pageName, String oldPageName) {
		boolean success= false;
		
		System.out.printf("UPDATE bookmark_linx SET page_name=%s WHERE user=%s AND page_name=%s;\n", pageName, user, oldPageName);
		final String sql= "UPDATE bookmark_linx SET page_name=? WHERE user=? AND page_name=?;";

		try {
			PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
			preparedStatement.setString(1, pageName);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, oldPageName);
			success= !preparedStatement.execute();
			preparedStatement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		HerokuConnection.close();
		
		return success;
	}
	
	/** adds a paragraph to user's account.
	 * @param user username
	 * @param pageName name of page to add paragraph to
	 * @param paragraphName name of paragraph to add
	 * @param paragraphPosition position in paragraph list to add this paragraph into
	 * @boolean whether paragraph was successfully added
	**/
	public boolean addParagraph(String user, String pageName, String paragraphName, int paragraphPosition) {
		boolean success= false;
		//Add Paragraph
		
		System.out.printf("SELECT paragraph_position FROM bookmark_linx WHERE user=%s AND page_name=%s ORDER BY paragraph_position DESC;\n", user, pageName);
		String sql= "SELECT paragraph_position FROM bookmark_linx WHERE user=? AND page_name=? ORDER BY paragraph_position DESC;";

		try {
			PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, pageName);
			ResultSet resultSet= preparedStatement.executeQuery();
			
			while(resultSet.next()) {
				int para_pos= resultSet.getInt("paragraph_position");
				if(para_pos >= paragraphPosition) {
					System.out.printf("UPDATE bookmark_linx SET paragraph_position=%d WHERE user=%s AND page_name=%s AND paragraph_position=%s;\n", para_pos+1, user, pageName, para_pos);
					sql= "UPDATE bookmark_linx SET paragraph_position=? WHERE user=? AND page_name=? AND paragraph_position=?;";
					PreparedStatement preparedStatement2= HerokuConnection.getConnection().prepareStatement(sql);
					preparedStatement2.setInt(1, para_pos+1);
					preparedStatement2.setString(2, user);
					preparedStatement2.setString(3, pageName);
					preparedStatement2.setInt(4, para_pos);
					success= !preparedStatement2.execute();
					preparedStatement2.close();
				}
			}
			resultSet.close();
			preparedStatement.close();
			
			System.out.printf("INSERT INTO bookmark_linx (user, page_name, paragraph_name, paragraph_position, position, link_name, link) VALUES (%s, %s, %s, %d, %d, %s, %s);\n", user, pageName, paragraphName, paragraphPosition, 0, "blankEntry", "");
			sql= "INSERT INTO bookmark_linx (user, page_name, paragraph_name, paragraph_position, position, link_name, link) VALUES (?, ?, ?, ?, ?, ?, ?);";
			PreparedStatement preparedStatement3= HerokuConnection.getConnection().prepareStatement(sql);
			preparedStatement3.setString(1, user);
			preparedStatement3.setString(2, pageName);
			preparedStatement3.setString(3, paragraphName);
			preparedStatement3.setInt(4, paragraphPosition);
			preparedStatement3.setInt(5, 0);
			preparedStatement3.setString(6, "blankEntry");
			preparedStatement3.setString(7, "");
			success= !preparedStatement3.execute();
			preparedStatement3.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		HerokuConnection.close();

		return success;
	}
	
	/** adds a link to user's account.
	 * @param user username
	 * @param pageName name of page to add link into
	 * @param paragraphName name of paragraph to add link into
	 * @param paragraphPosition position of paragraph to add link into
	 * @param linkName name of link
	 * @param linkUrl URL of link
	 * @param linkPosition position in paragraph to add link before
	 * @boolean whether link was successfully added
	**/
	public boolean addLink(String user, String pageName, String paragraphName, int paragraphPosition, String linkName, String linkUrl, int linkPosition) {
		boolean success= false;
		String sql= "SELECT MAX(position) as maxpos FROM bookmark_linx WHERE user=? AND page_name=? AND paragraph_position=?;";

		if(-1 == linkPosition) {
			linkPosition= 0;
			
			try {
				PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
				preparedStatement.setString(1, user);
				preparedStatement.setString(2, pageName);
				preparedStatement.setInt(3, paragraphPosition);
				ResultSet resultSet= preparedStatement.executeQuery();
				
				if(resultSet.next()) {
					linkPosition= 1+ resultSet.getInt("maxpos");
				}

				success= addLink(user, pageName, paragraphName, paragraphPosition, linkName, linkUrl, linkPosition);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
		
			//Add Link
			System.out.printf("addLink(user: %s, pageName: %s, paragraphName: %s, paragraphPosition: %s, linkName: %s, linkUrl: %s, linkPosition: %s\n", user, pageName, paragraphName, paragraphPosition, linkName, linkUrl, linkPosition);
			System.out.printf("SELECT position,paragraph_name FROM bookmark_linx WHERE user=%s AND page_name=%s AND paragraph_position=%d ORDER BY paragraph_position ASC, position DESC;\n", user, pageName, paragraphPosition);
			sql= "SELECT position,paragraph_name FROM bookmark_linx WHERE user=? AND page_name=? AND paragraph_position=? ORDER BY paragraph_position ASC, position DESC;";
			linkUrl= URLDecoder.decode(linkUrl);
			
			try {
				PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
				preparedStatement.setString(1, user);
				preparedStatement.setString(2, pageName);
				preparedStatement.setInt(3, paragraphPosition);
				ResultSet resultSet= preparedStatement.executeQuery();
				
				while(resultSet.next()) {
					if(null == paragraphName || paragraphName.length() == 0)	paragraphName= 	resultSet.getString("paragraph_name");
					int link_pos= resultSet.getInt("position");
					if(link_pos >= linkPosition) {
						System.out.printf("UPDATE bookmark_linx SET position=%d WHERE user=%s AND page_name=%s AND paragraph_position=%d AND position=%s;\n", link_pos+1, user, pageName, paragraphPosition, link_pos);
						sql= "UPDATE bookmark_linx SET position=? WHERE user=? AND page_name=? AND paragraph_position=? AND position=?;";
						PreparedStatement preparedStatement2= HerokuConnection.getConnection().prepareStatement(sql);
						preparedStatement2.setInt(1, link_pos+1);
						preparedStatement2.setString(2, user);
						preparedStatement2.setString(3, pageName);
						preparedStatement2.setInt(4, paragraphPosition);
						preparedStatement2.setInt(5, link_pos);
						success= !preparedStatement2.execute();
						preparedStatement2.close();
					}
				}
				resultSet.close();
				preparedStatement.close();
				
				System.out.printf("INSERT INTO bookmark_linx (user, page_name, paragraph_name, paragraph_position, position, link_name, link) VALUES (%s, %s, %s, %d, %d, %s, %s);\n", user, pageName, paragraphName, paragraphPosition, linkPosition, linkName, linkUrl);
				sql= "INSERT INTO bookmark_linx (user, page_name, paragraph_name, paragraph_position, position, link_name, link) VALUES (?, ?, ?, ?, ?, ?, ?);";
				PreparedStatement preparedStatement3= HerokuConnection.getConnection().prepareStatement(sql);
				preparedStatement3.setString(1, user);
				preparedStatement3.setString(2, pageName);
				preparedStatement3.setString(3, paragraphName);
				preparedStatement3.setInt(4, paragraphPosition);
				preparedStatement3.setInt(5, linkPosition);
				preparedStatement3.setString(6, linkName);
				preparedStatement3.setString(7, linkUrl);
				success= !preparedStatement3.execute();
				preparedStatement3.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			HerokuConnection.close();
		}
		return success;
	}
	
	/** deletes a paragraph from user's account.
	 * @param user username
	 * @param pageName name of page to deletes paragraph from
	 * @param paragraphPosition position of paragraph in paragraph list
	 * @boolean whether paragraph was successfully deleted
	**/
	public boolean deleteParagragh(String user, String pageName, int paragraphPosition) {
		boolean success= false;
		//Delete Paragraph
		System.out.printf("DELETE FROM bookmark_linx WHERE user=%s AND page_name=%s AND paragraph_position=%d;\n", user, pageName, paragraphPosition);
		final String sql= "DELETE FROM bookmark_linx WHERE user=? AND page_name=? AND paragraph_position=?;";

		try {
			PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, pageName);
			preparedStatement.setInt(3, paragraphPosition);
			success= !preparedStatement.execute();
			preparedStatement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		HerokuConnection.close();
		
		return success;
	}
	
	/** deletes a link from user's account.
	 * @param user username
	 * @param pageName name of page to delete link from
	 * @param paragraphPosition position of paragraph to delete link from
	 * @param linkPosition position in paragraph to delete link from
	 * @boolean whether link was successfully deleted
	**/
	public boolean deleteLink(String user, String pageName, int paragraphPosition, int linkPosition) {
		boolean success= false;
		//Delete Link
		System.out.printf("DELETE FROM bookmark_linx WHERE user=%s AND page_name=%s AND paragraph_position=%d AND position=%d;\n", user, pageName, paragraphPosition, linkPosition);
		final String sql= "DELETE FROM bookmark_linx WHERE user=? AND page_name=? AND paragraph_position=? AND position=?;";

		try {
			PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, pageName);
			preparedStatement.setInt(3, paragraphPosition);
			preparedStatement.setInt(4, linkPosition);
			success= !preparedStatement.execute();
			preparedStatement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		HerokuConnection.close();

		return success;
	}
	//TODO: Add User
	
	/** changes the name of a paragraph in user's account.
	 * @param user username
	 * @param pageName name of page where paragraph is located
	 * @param oldParagraphName name of paragraph to change
	 * @param paragraphName new name of paragraph
	 * @param paragraphPosition position of paragraph in paragraph list
	 * @boolean whether page name was successfully change
	**/
	public boolean editParagraph(String user, String pageName, int paragraphPosition, String paragraphName, String oldParagraphName) {
		boolean success= false;
		//Edit Paragraph
		
		System.out.printf("UPDATE bookmark_linx SET paragraph_Name=%s WHERE user=%s AND page_name=%s AND paragraph_position=%s;\n", paragraphName, user, pageName, paragraphPosition);
		final String sql= "UPDATE bookmark_linx SET paragraph_Name=? WHERE user=? AND page_name=? AND paragraph_position=?;";

		try {
			PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
			preparedStatement.setString(1, paragraphName);
			preparedStatement.setString(2, user);
			preparedStatement.setString(3, pageName);
			preparedStatement.setInt(4, paragraphPosition);
			success= !preparedStatement.execute();
			preparedStatement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		HerokuConnection.close();
		
		return success;
	}
	
	/** changes the name and URL of a link in user's account.
	 * @param user username
	 * @param pageName name of page where link is located
	 * @param paragraphPosition position of paragraph in paragraph list where link is located
	 * @param linkName new name of link
	 * @param linkUrl new URL of link
	 * @param linkPosition position in paragraph of this link
	 * @boolean whether link change was successful
	**/
	public boolean editLink(String user, String pageName, int paragraphPosition, int linkPosition, String linkName, String linkUrl) {
		boolean success= false;
		//Edit Link
		
		System.out.printf("UPDATE bookmark_linx SET link_name=%s, link=%s WHERE user=%s AND page_name=%s AND paragraph_position=%d AND position=%d;\n", linkName, linkUrl, user, pageName, paragraphPosition, linkPosition);
		final String sql= "UPDATE bookmark_linx SET link_name=?, link=? WHERE user=? AND page_name=? AND paragraph_position=? AND position=?;";
		linkUrl= URLDecoder.decode(linkUrl);
		
		try {
			PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
			preparedStatement.setString(1, linkName);
			preparedStatement.setString(2, linkUrl);
			preparedStatement.setString(3, user);
			preparedStatement.setString(4, pageName);
			preparedStatement.setInt(5, paragraphPosition);
			preparedStatement.setInt(6, linkPosition);
			success= !preparedStatement.execute();
			preparedStatement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		HerokuConnection.close();
		
		return success;
	}
	
	/**
	 * moves a paragraph up or down the list of displayed paragraphs.
	 * @param user user's account
	 * @param pageName name of page containing the paragraphs
	 * @param paragraphName name of paragraph to move
	 * @param paragraphPosition position of paragraph to move
	 * @param up whether to move paragraph up or down
	 * @boolean whether move was successful
	**/
	public boolean upParagraph(String user, String pageName, String paragraphName, int paragraphPosition, boolean up) {
		boolean success= false;
		
		String pName1= null;
		int pPos1= -1;
		int pPos2= -1;
		
		
		//Up Paragraph
		System.out.printf("SELECT paragraph_position, paragraph_name FROM bookmark_linx WHERE user='%s' AND page_name='%s' AND paragraph_position %s %d ORDER BY paragraph_position %s;\n", user, pageName, up ? "<" : ">", paragraphPosition, up ? "DESC" : "ASC");
		String sql= String.format("SELECT paragraph_position, paragraph_name FROM bookmark_linx WHERE user=? AND page_name=? AND paragraph_position %s ? ORDER BY paragraph_position %s;", up ? "<" : ">", up ? "DESC" : "ASC");

		try {
			PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, pageName);
			preparedStatement.setInt(3, paragraphPosition);
			ResultSet resultSet= preparedStatement.executeQuery();
			while(resultSet.next()) {
				pName1= resultSet.getString("paragraph_name");
				pPos1= resultSet.getInt("paragraph_position");
				System.out.printf("lPos1 %d %s\n", pPos1, pName1);
				break;
			}
			resultSet.close();
			preparedStatement.close();
			
			System.out.printf("SELECT paragraph_position, paragraph_name FROM bookmark_linx WHERE user='%s' AND page_name='%s' AND paragraph_position=%d;\n", user, pageName, paragraphPosition);
			sql= "SELECT paragraph_position, paragraph_name FROM bookmark_linx WHERE user=? AND page_name=? AND paragraph_position=?;";
			preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, pageName);
			preparedStatement.setInt(3, paragraphPosition);
			resultSet= preparedStatement.executeQuery();
			while(resultSet.next()) {
				//pName2= resultSet.getString("paragraph_name");
				pPos2= resultSet.getInt("paragraph_position");
				System.out.printf("lPos2 %d %s\n", pPos2, resultSet.getString("paragraph_name"));
			}
			resultSet.close();
			preparedStatement.close();
			
			if(-1 != pPos1 && -1 != pPos2) {
				System.out.printf("UPDATE bookmark_linx SET paragraph_position=%d WHERE user=%s AND page_name=%s AND paragraph_position=%d AND paragraph_name=%s;\n", pPos1, user, pageName, paragraphPosition, paragraphName);
				sql= "UPDATE bookmark_linx SET paragraph_position=? WHERE user=? AND page_name=? AND paragraph_position=? AND paragraph_name=?;";
				preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
				preparedStatement.setInt(1, pPos1);
				preparedStatement.setString(2, user);
				preparedStatement.setString(3, pageName);
				preparedStatement.setInt(4, paragraphPosition);
				preparedStatement.setString(5, paragraphName);
				success= !preparedStatement.execute();
				preparedStatement.close();
				
				System.out.printf("UPDATE bookmark_linx SET paragraph_position=%d WHERE user=%s AND page_name=%s AND paragraph_position=%d AND paragraph_name=%s;\n", paragraphPosition, user, pageName, pPos1, pName1);
				sql= "UPDATE bookmark_linx SET paragraph_position=? WHERE user=? AND page_name=? AND paragraph_position=? AND paragraph_name=?;";
				preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
				preparedStatement.setInt(1, paragraphPosition);
				preparedStatement.setString(2, user);
				preparedStatement.setString(3, pageName);
				preparedStatement.setInt(4, pPos1);
				preparedStatement.setString(5, pName1);
				success= !preparedStatement.execute();
				preparedStatement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		HerokuConnection.close();
		
		return success;
	}
	
	/**
	 * moves a link up or down the list of displayed links in a paragraph.
	 * @param user user's account
	 * @param pageName name of page containing the links
	 * @param paragraphPosition position of paragraph which contains links
	 * @param linkName name of link
	 * @param linkPosition position in paragraph of this link
	 * @param left whether to move link to the left or right
	 * @boolean whether move was successful
	**/
	public boolean rightLink(String user, String pageName, int paragraphPosition, String linkName, int linkPosition, boolean left) {
		boolean success= false;
		
		String lName1= null;
		int lPos1= -1;
		int lPos2= -1;
		
		
		//Up Paragraph
		System.out.printf("SELECT position, link_name FROM bookmark_linx WHERE user='%s' AND page_name='%s' AND paragraph_position=%d AND position %s %d ORDER BY position %s;\n", user, pageName, paragraphPosition, left ? "<" : ">", linkPosition, left ? "DESC" : "ASC");
		String sql= String.format("SELECT position, link_name FROM bookmark_linx WHERE user=? AND page_name=? AND paragraph_position=? AND position %s ? ORDER BY position %s;", left ? "<" : ">", left ? "DESC" : "ASC");

		try {
			PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, pageName);
			preparedStatement.setInt(3, paragraphPosition);
			preparedStatement.setInt(4, linkPosition);
			ResultSet resultSet= preparedStatement.executeQuery();
			while(resultSet.next()) {
				lName1= resultSet.getString("link_name");
				lPos1= resultSet.getInt("position");
				System.out.printf("lPos1 %d %s\n", lPos1, lName1);
				break;
			}
			resultSet.close();
			preparedStatement.close();
			
			System.out.printf("SELECT position, link_name FROM bookmark_linx WHERE user='%s' AND page_name='%s' AND paragraph_position=%d AND position=%d;\n", user, pageName, paragraphPosition, linkPosition);
			sql= "SELECT position, link_name FROM bookmark_linx WHERE user=? AND page_name=? AND paragraph_position=? AND position=?;";
			preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, pageName);
			preparedStatement.setInt(3, paragraphPosition);
			preparedStatement.setInt(4, linkPosition);
			resultSet= preparedStatement.executeQuery();
			while(resultSet.next()) {
				//lName2= resultSet.getString("link_name");
				lPos2= resultSet.getInt("position");
				System.out.printf("lPos2 %d %s\n", lPos2, resultSet.getString("link_name"));
			}
			resultSet.close();
			preparedStatement.close();
			
			if(-1 != lPos1 && -1 != lPos2) {
				System.out.printf("UPDATE bookmark_linx SET position=%d WHERE user='%s' AND page_name='%s' AND paragraph_position=%d AND link_name='%s' AND position=%d;\n", lPos1, user, pageName, paragraphPosition, linkName, linkPosition);
				sql= "UPDATE bookmark_linx SET position=? WHERE user=? AND page_name=? AND paragraph_position=? AND link_name=? AND position=?;";
				preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
				preparedStatement.setInt(1, lPos1);
				preparedStatement.setString(2, user);
				preparedStatement.setString(3, pageName);
				preparedStatement.setInt(4, paragraphPosition);
				preparedStatement.setString(5, linkName);
				preparedStatement.setInt(6, linkPosition);
				success= !preparedStatement.execute();
				preparedStatement.close();
				
				System.out.printf("UPDATE bookmark_linx SET position=%d WHERE user='%s' AND page_name='%s' AND paragraph_position=%d AND link_name='%s' AND position=%d;\n", linkPosition, user, pageName, paragraphPosition, lName1, lPos1);
				sql= "UPDATE bookmark_linx SET position=? WHERE user=? AND page_name=? AND paragraph_position=? AND link_name=? AND position=?;";
				preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
				preparedStatement.setInt(1, linkPosition);
				preparedStatement.setString(2, user);
				preparedStatement.setString(3, pageName);
				preparedStatement.setInt(4, paragraphPosition);
				preparedStatement.setString(5, lName1);
				preparedStatement.setInt(6, lPos1);
				success= !preparedStatement.execute();
				preparedStatement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		HerokuConnection.close();
		
		return success;
	}

}
