package com.fmt.bookmark.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;

import com.fmt.bookmark.intl.Strings;
import com.fmt.bookmark.orm.BookmarkPage;
import com.fmt.bookmark.orm.BookmarkPage.Link;
import com.fmt.bookmark.orm.BookmarkPage.Paragraph;
import com.fmt.database.HerokuConnection;
import com.fmt.rest.service.BookmarkDatabase;

/**
 * Select Table servlet.
 **/
@SuppressWarnings("serial")
public class SelectLinx extends HttpServlet {
	public static final String SEPARATOR= " | ";

	/**
	 * given a user and a page name, return list of paragraphs on the page.
	 * @param username user name of account
	 * @param pageName name of page to get paragraphs from
	 * @return list of paragraph names in user's account
	 **/
	public static BookmarkPage queryForParagraphs(String user, String pageName) {
		final JdbcTemplate springPg= HerokuConnection.getSpringConnection();
		
		final BookmarkPage table= new BookmarkPage();
		table.name= pageName;
		
		final int dbPageId= springPg.queryForObject("SELECT id FROM bookmark_page WHERE user_id=(SELECT id FROM rest_users WHERE user_name=? AND site_name=?) AND page_name=?", new Object[]{user, "com.fmt.bookmarks", pageName}, Integer.class);
		
		List<Integer> paragraphIds= springPg.query("SELECT id from bookmark_paragraphs WHERE page_id=? ORDER BY position ASC", new Object[]{dbPageId}, (row, rowNum) -> row.getInt("id"));

		paragraphIds.forEach(pgId -> {
			final Paragraph pgPara= springPg.queryForObject("SELECT * FROM bookmark_paragraphs WHERE id=? ORDER BY position ASC", new Object[]{pgId}, (row, pgn) -> new BookmarkPage.Paragraph(row.getInt("id"), row.getInt("position"), row.getString("name")));

			springPg.query("SELECT * FROM bookmark_linx WHERE paragraph_id=? ORDER BY position ASC", new Object[]{pgId}, (row, rowNum) -> new Link(row.getInt("position"), row.getString("link_url"), row.getString("name"))).forEach(ln -> {
				pgPara.linx.add(ln);
			});

			table.paragraphs.add(pgPara);
		});
		/*System.out.printf("SELECT paragraph_name, paragraph_position, link_name, link, position FROM bookmark_linx WHERE user=%s AND page_name=%s ORDER BY paragraph_position, position;\n", user, pageName);
		final String sqlLinx= "SELECT paragraph_name, paragraph_position, link_name, link, position FROM bookmark_linx WHERE user=? AND page_name=? ORDER BY paragraph_position, position;";

		try {
			// Statements allow to issue SQL queries to the database
			//sParagraph= connection.prepareStatement(sqlParagraphs);
			PreparedStatement sLinx= CloudbeesConnection.getConnection().prepareStatement(sqlLinx);
			sLinx.setString(1, user);
			sLinx.setString(2, pageName);
			// Result set get the result of the SQL query
			ResultSet resultSet= sLinx.executeQuery();

			table.populate(resultSet);
			System.out.println("table.paragraphs.size(): "+ table.paragraphs.size());

			resultSet.close();
			sLinx.close();
			CloudbeesConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			tables.add(e.getMessage());
		}*/

		return table;
	}

	/**
	 * returns HTML for links to users' paragraphs.
	 * @param username user name of account
	 * @param password pawword for username
	 * @param forEdit whether to display in editing mode
	 * @param format which format in which to display paragraphs
	 * @param noJQuery whether to not include jquery libraries
	 * @return HTML string for links to users' paragraphs
	 **/
	public String createPage(String username, String pagename, String format, boolean forEdit, boolean noJQuery) {
		
		boolean blocks= format.equals("blocks");
		boolean trapese= format.equals("trapese");
		int blockColumn= 0;
		
		String html= "";

		html+= "<html>\n";
		html+= "<head>\n";
		html+= String.format("<title>%s "+Strings.Links+"</title>\n", pagename);
		
		html+= "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n";
		html+= "<link rel=\"shortcut icon\" href=\"./icon.ico\"/>\n";
		if(!noJQuery) {	
			html+= "<meta name=\"viewport\" content=\"width=device-width, initial-scale=0.75\">\n";
			html+= "<link rel=\"stylesheet\" href=\"./jquery/jquery.mobile.css\" />\n";
			html+= "<script src=\"./jquery/jquery.js\"></script>\n";
			html+= "<script src=\"./jquery/jquery.mobile.js\"></script>\n";		}
		html+= "<script src=\"./edit.js\"></script>\n";
		
		html+= "</head>\n";
		html+= String.format("<body %s>\n", blocks ? "background=\"http://www.ceoexpress.com/graphics/newlook/background2.gif\"" : "bgcolor=\"lightyellow\"");

		if(forEdit)	html+= String.format("<br/> "+Strings.seeorbookmark+" <a href='./plinks?user=%s&page=%s&edit=false&format=%s&private=false'>"+Strings.straight+"</a>, <a href='./plinks?user=%s&page=%s&edit=false&format=%s&private=false'>"+Strings.blocks+"</a>, <a href='./plinks?user=%s&page=%s&edit=false&format=%s&private=false'>"+Strings.trapese+"</a> "+Strings.versions+"<br/>\n", username, pagename, "straight", username, pagename, "blocks", username, pagename, "trapese");
		if(forEdit)	html+= String.format("Private Versions: <a href='./links?user=%s&page=%s&edit=false&format=%s&private=true'>"+Strings.straight+"</a>, <a href='./links?user=%s&page=%s&edit=false&format=%s&private=true'>"+Strings.blocks+"</a>, <a href='./links?user=%s&page=%s&edit=false&format=%s&private=true'>"+Strings.trapese+"</a><br/><hr/>"+Strings.youreditor+"<br/><a href='./page?user=%s'><img src='img/left-arrow.png' />"+Strings.backtopages+"</a><br/><br/>\n", username, pagename, "straight", username, pagename, "blocks", username, pagename, "trapese", username);
		
		if(null == pagename || pagename.length() == 0) {
			html+= Strings.nousernamegiven+ "<br/>\n";
		} else {
			BookmarkPage table= queryForParagraphs(username, pagename);
			System.out.println("table.paragraphs.size(): "+ table.paragraphs.size());
			if(blocks) html+= "<table width=\"100%\" cellpadding=\"10\">\n";	// border=\"1\"
			for(Paragraph paragraph : table.paragraphs) {
				String openAll= "";
				if(blocks && 0 == blockColumn) {
					html+= "<tr>\n";
				}
				//edit
				String editor= "";
				if(forEdit) {
					editor+= String.format("[<img src='img/edit.png' onclick='window.open(\"edit?user=%s&page=%s&paragraph_pos=%d&action=%s&paragraph_name=%s\",\"PopFrameless\",\"height=200,width=355,scrollbars=no,titlebar=no,toolbar=no,menubar=no,resizable=no,status=no\")'/>", username, pagename, paragraph.position, "EDITPG", paragraph.name);
					editor+= String.format("<img src='img/plus.png' onclick='window.open(\"edit?user=%s&page=%s&paragraph_pos=%d&action=%s\",\"PopFrameless\",\"height=200,width=355,scrollbars=no,titlebar=no,toolbar=no,menubar=no,resizable=no,status=no\")'/>", username, pagename, paragraph.position, "ADDPG");
					//editor+= String.format("<img src='img/minus.png' onclick='window.location.replace(\"./links?user=%s&page=%s&paragraph_pos=%d&action=%s&edit=true\",\"PopFrameless\",\"height=100,width=355,scrollbars=no,titlebar=no,toolbar=no,menubar=no,resizable=no,status=no\")'/>", username, pagename, paragraph.position, "DELETEPG");
					editor+= String.format("<img src='img/minus.png' onclick='addLink(%s, \"%s\", \"%s\", \"%s\", \"%s\", %d, %d, \"%s\", \"%s\", \"%s\")' />", "null", "", "", username, pagename, paragraph.position, -1, "DELETEPG", paragraph.name, format);
					editor+= String.format("<img src='img/up-arrow.png' onclick='addLink(%s, \"%s\", \"%s\", \"%s\", \"%s\", %d, %d, \"%s\", \"%s\", \"%s\")' />", "null", "", "", username, pagename, paragraph.position, -1, "UPPG", paragraph.name, format);
					editor+= String.format("<img src='img/down-arrow.png' onclick='addLink(%s, \"%s\", \"%s\", \"%s\", \"%s\", %d, %d, \"%s\", \"%s\", \"%s\")' />]", "null", "", "", username, pagename, paragraph.position, -1, "DOWNPG", paragraph.name, format);

				}

				if(blocks) html+= "<td>\n";
				html+= String.format("%s%s: %s%s\n", trapese ? "" : blocks ? "" : "<br/>", paragraph.name, editor, trapese ? "" : blocks ? "<br/>" : "<br/>");
				if(trapese) html+= "<ul>\n";
				for(Link link : paragraph.linx) {
					//'PopFrameless','height=310,width=310,scrollbars=yes,titlebar=no,toolbar=no,menubar=no,resizable=no,status=no'); 
					//W1.moveTo(450,400);
					//System.out.println("paragraph.linx.size(): "+ paragraph.linx.size());
					openAll+= String.format("window.open('%s');", link.link);
					
					//edit
					editor= "";
					if(forEdit) {
						editor+= String.format("[<img src='img/edit.png' onclick='window.open(\"edit?link_name=%s&link_url=%s&user=%s&page=%s&paragraph_pos=%d&link_pos=%d&action=%s&paragraph_name=%s\",\"PopFrameless\",\"height=300,width=355,scrollbars=no,titlebar=no,toolbar=no,menubar=no,resizable=no,status=no\")'/>", link.name, URLEncoder.encode(link.link), username, pagename, paragraph.position, link.position, "EDITLINK", paragraph.name);
						editor+= String.format("<img src='img/plus.png' onclick='window.open(\"edit?link_name=%s&link_url=%s&user=%s&page=%s&paragraph_pos=%d&link_pos=%d&action=%s&paragraph_name=%s\",\"PopFrameless\",\"height=300,width=355,scrollbars=no,titlebar=no,toolbar=no,menubar=no,resizable=no,status=no\")'/>", link.name, URLEncoder.encode(link.link), username, pagename, paragraph.position, link.position, "ADDLINK", paragraph.name);
						//editor+= String.format("<img src='img/minus.png' onclick='window.location.replace(\"./links?link_name=%s&link_url=%s&user=%s&page=%s&paragraph_pos=%d&link_pos=%d&action=%s&edit=true\",\"PopFrameless\",\"height=100,width=355,scrollbars=no,titlebar=no,toolbar=no,menubar=no,resizable=no,status=no\")'/>", link.name, URLEncoder.encode(link.link), username, pagename, paragraph.position, link.position, "DELETELINK");
						editor+= String.format("<img src='img/minus.png' onclick='addLink(%s, \"%s\", \"%s\", \"%s\", \"%s\", %d, %d, \"%s\", \"%s\", \"%s\")' />", "null", link.name, URLEncoder.encode(link.link), username, pagename, paragraph.position, link.position, "DELETELINK", paragraph.name, format);
						editor+= String.format("<img src='img/%s-arrow.png' onclick='addLink(%s, \"%s\", \"%s\", \"%s\", \"%s\", %d, %d, \"%s\", \"%s\", \"%s\")' />", trapese ? "up" : "left", "null", URLEncoder.encode(link.name), link.link, username, pagename, paragraph.position, link.position, "LEFTLINK", paragraph.name, format);
						editor+= String.format("<img src='img/%s-arrow.png' onclick='addLink(%s, \"%s\", \"%s\", \"%s\", \"%s\", %d, %d, \"%s\", \"%s\", \"%s\")' />]", trapese ? "down" : "right", "null", link.name, URLEncoder.encode(link.link), username, pagename, paragraph.position, link.position, "RIGHTLINK", paragraph.name, format);
					}

					if(link.link.isEmpty()) { 
						html+= String.format("%s%s%s%s\n", trapese ? "<li>" : "", link.name, editor, trapese ? "</li>" : SEPARATOR);
					} else {
						html+= String.format("%s<a href='%s'>%s</a>%s%s\n", trapese ? "<li>" : "", link.link, link.name, editor, trapese ? "</li>" : SEPARATOR);
					}
					
				}
				blockColumn++;
				html+= String.format("<a href=\"\" onclick=\"%sreturn true;\"><small>["+Strings.openall+"]</small></a>", openAll);
				if(blocks) html+= "</td>\n";
				if(trapese) html+= "</ul>\n";
				if(blocks && 3 == blockColumn) {
					html+= "</tr>\n";
					blockColumn= 0;
				}
			}


			//html+= e.getMessage()+"<br/>\n";
			if(blocks) html+= "</table>\n";
		}
		
		//edit page
		//if(private)
		{
		String openScript=String.format("var givenPw= prompt('"+Strings.enterPassword+"','');window.location.replace('./links?user=%s&page=%s&edit=true&format=%s&password='+givenPw);return false;", username, pagename, format);
		html+= String.format("<br /><br /><a href=\"\" onclick=\"%s\">"+Strings.editthispage+"</a> | <a href=\"http://www.tinyurl.com/simple-bookmarks\">Bookmarks Site</a>", openScript);
		}
		
		html+= "</body>\n";
		html+= "</html>\n";

		return html;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String user= (String)request.getParameter("user"); //session.getAttribute("username");
		final String page= (String)request.getParameter("page");
		//final boolean pvt= Boolean.parseBoolean(request.getParameter("private"));
		String format= (String)request.getParameter("format");
		if(null == format)	format= "straight";
		String linkUrl= request.getParameter("link_url");
		if(null != linkUrl)	linkUrl= URLDecoder.decode(linkUrl);
		String linkName= (String)request.getParameter("link_name");
		if(null != linkName)	linkName= linkName.replaceAll(",", "-");
		String paragraphName= (String)request.getParameter("paragraph_name");
		if(null != paragraphName)	paragraphName= paragraphName.replaceAll(",", "-");
		final String oldParagraphName= (String)request.getParameter("old_paragraph_name");
		final String action= (String)request.getParameter("action");
		int linkPos= -1;
		int paragraphPos= -1;
		try {
			linkPos= Integer.parseInt((String)request.getParameter("link_pos"));
		} catch (NumberFormatException e) {
			//no err
		}
		try {
			paragraphPos= Integer.parseInt((String)request.getParameter("paragraph_pos"));
		} catch (NumberFormatException e) {
			//no err
		}
		final boolean forEdit= Boolean.parseBoolean(request.getParameter("edit"));
		final boolean forDesktop= Boolean.parseBoolean(request.getParameter("desktop"));

		if(null != action) {
			boolean success= false;
			BookmarkDatabase db= new BookmarkDatabase();

			if(action.endsWith("PG")) {

				if(action.startsWith("ADD")) {
					success= db.addParagraph(user, page, paragraphName, paragraphPos);
				}
				if(action.startsWith("EDIT")) {
					success= db.editParagraph(user, page, paragraphPos, paragraphName, oldParagraphName);
				}
				if(action.startsWith("DELETE")) {
					success= db.deleteParagragh(user, page, paragraphPos);
				}
				if(action.startsWith("UP")) {
					success= db.upParagraph(user, page, oldParagraphName, paragraphPos, true);
				}
				if(action.startsWith("DOWN")) {
					success= db.upParagraph(user, page, oldParagraphName, paragraphPos, false);
				}
			} else if(action.endsWith("LINK")) {
				if(action.startsWith("ADD")) {
					success= db.addLink(user, page, paragraphName, paragraphPos, linkName, linkUrl, linkPos);
				}
				if(action.startsWith("EDIT")) {
					success= db.editLink(user, page, paragraphPos, linkPos, linkName, linkUrl);
				}
				if(action.startsWith("DELETE")) {
					success= db.deleteLink(user, page, paragraphPos, linkPos);
				}
				if(action.startsWith("LEFT")) {
					success= db.rightLink(user, page, paragraphPos, linkName, linkPos, true);
				}
				if(action.startsWith("RIGHT")) {
					success= db.rightLink(user, page, paragraphPos, linkName, linkPos, false);
				}
			}

			if(!success) {
				System.out.printf("%s failed\n", action);
			}
		}
		
		/*if(pvt) {
			String message= "";
	        String role= null;
	        final HttpSession session = request.getSession();
	        if(null != session)	{
	        	role= (String)session.getAttribute("role");
	        }
	        else {
	        	message+= "session is null";
	        }
	        
			if(null == role || !role.contains("user")) {
				if(null == role) {
	        		message+= ", role is null";
	        	} else if(!role.contains("user")) {
	        		message+= ", role is "+ role;
	        	}
				response.getWriter().write(String.format("Invalid User, %s\n\nPlease Login at https://fmt-bookmarks.herokuapp.com", message.replaceAll(", ,", ",")));
				return;
			}
		}*/

		response.getWriter().println(createPage(user, page, format, forEdit, forDesktop));
	}
}
