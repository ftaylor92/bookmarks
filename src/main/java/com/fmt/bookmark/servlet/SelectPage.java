package com.fmt.bookmark.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fmt.bookmark.intl.Strings;
import com.fmt.bookmark.upload.ImportBookmarks;
import com.fmt.database.HerokuConnection;
import com.fmt.rest.service.BookmarkDatabase;
//import javax.servlet.http.HttpSession;

/**
 * Select Table servlet.
 **/
@SuppressWarnings("serial")
public class SelectPage extends HttpServlet {
	
	final static String BASE_URL= "https://fmt-bookmarks.herokuapp.com";//"http://localhost:8080/bookmarks";

	/**
	 * given a user, return list of pages associated with user's account.
	 * @param username user name of account
	 * @return list of page names in user's account
	 **/
	public List<String> queryForPages(String username) {
		System.out.printf("SELECT page_name FROM bookmark_linx WHERE user=%s GROUP BY page_name ORDER BY page_name;\n", username);
		final String sql= "SELECT page_name FROM bookmark_page WHERE user_id=(SELECT id FROM rest_users WHERE user_name=? AND site_name=?) GROUP BY page_name ORDER BY page_name;";
		
		List<String> tables= new ArrayList<>();//HerokuConnection.getSpringConnection().query(sql, new Object[]{username}, (row,rowNum) -> row.getString("page_name"));
		try {
			// Statements allow to issue SQL queries to the database
			//statement = connection.createStatement();
			PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, "com.fmt.bookmarks");
			// Result set get the result of the SQL query
			//resultSet = statement.executeQuery(sql);
			ResultSet resultSet= preparedStatement.executeQuery();

			//writeResultSet(resultSet);
			while (resultSet.next()) {
				System.out.println("page: "+ resultSet.getString("page_name"));
				tables.add(resultSet.getString("page_name"));
			}
			resultSet.close();
			preparedStatement.close();
			HerokuConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			tables.add(e.getMessage());
		}
		
		return tables;
	}
	
	/**
	 * returns HTML for links to users' pages.
	 * @param username user name of account
	 * @param password pawword for username
	 * @return HTML string for links to users' pages
	 **/
	public String createPage(String username, String password) {
		String html= "";
		
		html+= "<html>\n";
		html+= "<head>\n";
		html+= "<title>"+Strings.LinkPages+"</title>\n";
		
		html+= "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n";
		html+= "<meta name=\"viewport\" content=\"width=device-width, initial-scale=0.75\">\n";
		html+= "<link rel=\"stylesheet\" href=\"./jquery/jquery.mobile.css\" />\n";
		html+= "<script src=\"./jquery/jquery.js\"></script>\n";
		html+= "<script src=\"./jquery/jquery.mobile.js\"></script>\n";

		html+= "<script src=\"./edit.js\"></script>\n";
		
		html+= "</head>\n";
		html+= "<body><br/>\n";
		
		if(null == username || username.length() == 0) {
			html+= "No username param given in URL<br/>";
		} else {
			final String bookToolBar="Bookmark pages more easily. Drag this bookmarklet to the Bookmarks bar of your browser: <a style='background-color:#dddddd;border:1px groove #999; padding:5px;padding-top:0px;padding-bottom:2px; text-decoration:none; margin-top:5px' class=kd-button href='javascript:(function(){var a=window,b=document,c=encodeURIComponent,d=a.open(\""+BASE_URL+"/edit?link_name=\"+c(b.title)+\"&link_url=\"+c(b.location)+\"&user="+username+"&page=&password="+password+"&paragraph_pos=&link_pos=0&action=ADDFROMTOOLBARLINK&paragraph_name=\",\"PopFrameless\",\"height=550,width=355,scrollbars=no,titlebar=no,toolbar=no,menubar=no,resizable=no,status=no\");a.setTimeout(function(){d.focus()},300)})();'>Complete Bookmarker</a>";

			html+= String.format(Strings.click+ " <img src='img/plus.png' onclick='window.open(\"edit?user=%s&page=%s&action=%s\",\"PopFrameless\",\"height=200,width=355,scrollbars=no,titlebar=no,toolbar=no,menubar=no,resizable=no,status=no\")'/> "+Strings.toAddPage+" <br/><br/>%s<br/><br/>Pages:<br/>\n", username, "", "ADDPAGE", bookToolBar);
			for(String page : queryForPages(username)) {
				html+= String.format("<a href='./links?user=%s&page=%s&edit=true&format=%s'>%s</a> \n", username, page, "straight", page);
				html+= String.format("[<img src='img/edit.png' onclick='window.open(\"edit?user=%s&page=%s&action=%s\",\"PopFrameless\",\"height=200,width=355,scrollbars=no,titlebar=no,toolbar=no,menubar=no,resizable=no,status=no\")'/>", username, page, "EDITPAGE");
				html+= String.format("<img src='img/plus.png' onclick='window.open(\"edit?user=%s&page=%s&action=%s\",\"PopFrameless\",\"height=200,width=355,scrollbars=no,titlebar=no,toolbar=no,menubar=no,resizable=no,status=no\")'/>", username, page, "ADDPAGE");
				html+= String.format("<img src='img/minus.png' onclick='addLink(%s, \"%s\", \"%s\", \"%s\", \"%s\", %d, %d, \"%s\", \"%s\")' />]<br/>\n", "null", page, "", username, page, -1, -1, "DELETEPAGE", "");
				//html+= String.format("<img src='img/minus.png' onclick='window.location.assign(\"./page?user=%s&page=%s&action=%s\",\"PopFrameless\",\"height=100,width=355,scrollbars=no,titlebar=no,toolbar=no,menubar=no,resizable=no,status=no\")'/>]<br/>\n", username, page, "DELETEPAGE");
			}
		}
		
		//import
		html+= "<hr/><br/>\n";
//	html+= "<form method=\"POST\" action=\"upload\" enctype=\"multipart/form-data\">\n";
//    html+= "Select file to upload: <input type=\"file\" name=\"uploadFile\" />\n";
//    //html+= "<br/><br/>\n";
//    html+= "<input type=\"submit\" value=\"Upload\" />\n";
//    html+= "</form>\n";
		html+= "Select File of Bookmarks to import Links from:";
		html+= "<button data-inline=\"true\" onclick=\"window.location.replace('./select?user="+username+"');\" >Import File</button>\n";


		html+= "<br/>--or--<br/><br/>\n";

		html+= "Select URL of Web Page of Bookmarks to Import Links from: <input style=\"width: 500px;\" type=\"url\" id=\"\"uploadUrl\"\" class=\"\"uploadUrl\"\" name=\"uploadUrl\" />\n";
		html+= "<button data-inline=\"true\" onclick=\"var nurl=escape($('input[name=uploadUrl]').val());window.location.replace('./page?user="+username+"&page=x&action=IMPORTPAGE&import='+nurl);\" >Import URL</button>\n";
		//html+= "<!-- button data-inline=\"true\" onclick=\"window.location.replace('./links?edit=true&user='+username+'&page='+pageName+'&action='+action+'&link_url='+link_url+'&link_name='+link_name+'&paragraph_name='+paragraphName+'&oldPageName='+pageName+'&paragraph_pos='+paragraphPosition+'&link_pos='+linkPosition\" / -->\n";		

		html+= "</body>\n";
		html+= "</html>\n";
		
		return html;
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String user= (String)request.getParameter("user");
		System.out.printf("user: %s\n", user);
		doGet(request, response);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//final HttpSession session = request.getSession(true);
		
		final String user= (String)request.getParameter("user"); //session.getAttribute("username");
		final String password= (String)request.getParameter("password");
		String page= (String)request.getParameter("page");
		if(null != page)	page= page.replaceAll(",", "-");
		String oldPage= (String)request.getParameter("oldPageName");
		if(null != oldPage)	oldPage= oldPage.replaceAll(",", "-");
		final String action= (String)request.getParameter("action");
		String importPage= (String)request.getParameter("import");
		System.out.println(action);
		if(null != action && action.endsWith("PAGE")) {
			boolean success= false;
			BookmarkDatabase db= new BookmarkDatabase();
			
			if(action.startsWith("ADD")) {
				success= db.addPage(user, page);
			}
			else if(action.startsWith("EDIT")) {
				success= db.editPage(user, page, oldPage);
			}
			else if(action.startsWith("IMPORT")) {
				if(null != importPage)	importPage= URLDecoder.decode(importPage);
				System.out.printf("u: %s url: %s\n", user, importPage);
				success= ImportBookmarks.importPage(user, importPage);
			}
			else if(action.startsWith("DELETE")) {
				success= db.deletePage(user, page);
			} else {
				System.out.printf("%s for PAGE isn't IMPORT, ADD, EDIT or DELETE\n", action);
			}
			
			if(!success) {
				System.out.printf("%s failed\n", action);
			}
		}
		
		response.getWriter().println(createPage(user, password));
	}
}
