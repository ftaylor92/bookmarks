package com.fmt.bookmark.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fmt.bookmark.intl.Strings;
import com.fmt.rest.client.JerseyClientGet;

/**
 * Edit Link form.
 **/
@SuppressWarnings("serial")
public class EditLink extends HttpServlet {

	/**
	 * returns HTML for create, edit and delete links, pages and paragraphs.
	 * @param username user name
	 * @param password password
	 * @param linkName name of link to edit
	 * @param linkUrl URL of link
	 * @param pageName name of page to add link to
	 * @param paragraphPosition position of paragraph to add link to
	 * @param paragraphName name of paragraph to add link to
	 * @param linkPosition position of link in paragraph to add link before
	 * @param action action to perform
	 * @return HTML string for Edit Links page
	 **/
	public String createPage(String username, String password, String linkName, String linkUrl, String pageName, int paragraphPosition, int linkPosition, String action, String paragraphName) {
		System.out.printf("name: %s URL: %s\n", linkName, linkUrl);
		
		//redo parameters
		boolean fromToolbar= action.equals("ADDFROMTOOLBARLINK");
		boolean add= action.startsWith("ADD");
		boolean delete= action.startsWith("DELETE");
		boolean paragraph= action.endsWith("PG");
		boolean page= action.endsWith("PAGE");
		
		
		if(add) {
			if(page)		pageName= "";
			if(paragraph)	paragraphName= "";
			if(!fromToolbar) {
				linkName= "";
				linkUrl= "";
			}
		}
		
		String html= "";
		
		html+= "<html>\n";
		html+= "<head>\n";
		html+= "<title>"+Strings.Edit+"</title>\n";
		
		html+= "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n";
		html+= "<meta name=\"viewport\" content=\"width=device-width, initial-scale=0.75\">\n";
		html+= "<link rel=\"stylesheet\" href=\"./jquery/jquery.mobile.css\" />\n";
		html+= "<script src=\"./jquery/jquery.js\"></script>\n";
		html+= "<script src=\"./jquery/jquery.mobile.js\"></script>\n";
		
		html+= "<script src=\"./edit.js\"></script>\n";
		
		html+= "</head>\n";
		html+= "<body>\n";

		html+= "<script>\n";
		html+= "$(document).ready(function() {\n";
		html+= "$.ajaxSetup({ cache: false, contentType: \"application/json; charset=utf-8\", dataType:\"json\"});\n";
		html+= "});\n";
		html+="var pgnme=\"\";\n";
		html+="var lnkpos=-1;\n";
		html+= "</script>\n";
		
		//style=\"width: 500px;\"
		// style=\"width: 100px;\"
		String buttonTxt= "";
		
		if(fromToolbar) {
			html+= "Page name: <select name=\"pagename\" class=\"pagename\" id=\"pagename\" onchange=\"pgnme=this.options[this.selectedIndex].value;getAndChangeParagraphs('"+username+"','"+password+"',this.options[this.selectedIndex].value)\">\n";
			List<String> pages= JerseyClientGet.getPageNames(username, password);
			html+= String.format("<option value=\"%s\">%s</option>\n", "Select Page", "Select Page");
			for(String pageTitle : pages) {
				//html+= String.format("<option selected value=\"%s\">%s</option>", args);
				html+= String.format("<option value=\"%s\">%s</option>\n", pageTitle, pageTitle);
			}
			html+= "</select><br/>\n";
			
			html+= "Paragraph name: <select name=\"paragraphname\" class=\"paragraphname\" id=\"paragraphname\" onchange=\"getAndChangeParagraphLinks('"+username+"','"+password+"',pgnme,this.options[this.selectedIndex].value)\">\n";
			/*Map<Integer, String> paragraphs= JerseyClientGet.getParagraphNames(username, password, pageName);
			for(String pageTitle : paragraphs) {
				//html+= String.format("<option selected value=\"%s\">%s</option>", args);
				html+= String.format("<option value=\"%s\">%s</option>", pageTitle, pageTitle);
			}*/
			html+= "</select><br/>\n";
			
			html+= "Add Link Before Entry: <select name=\"paralinkname\" class=\"paralinkname\" id=\"paralinkname\" onchange=\"lnkpos=this.selectedIndex\">\n";
			/*Map<Integer, String> paragraphs= JerseyClientGet.getParagraphNames(username, password, pageName);
			for(String pageTitle : paragraphs) {
				//html+= String.format("<option selected value=\"%s\">%s</option>", args);
				html+= String.format("<option value=\"%s\">%s</option>", pageTitle, pageTitle);
			}*/
			html+= "</select><br/>\n";
			
		  //buttonTxt= String.format("<button data-inline=\"true\" onclick=\"addLink(self, $('input:text[name=linkName]').val(), $('input[name=linkUrl]').val(), '%s', ($('#pagename').val()), %d, %d, '%s', ($('#paragraphname').val()))\">%s</button>\n", username, paragraphPosition, linkPosition, action, add ? Strings.Add : delete ? Strings.Delete : Strings.Edit);
			buttonTxt= String.format("<button data-inline=\"true\" onclick=\"addLink(self, $('input:text[name=linkName]').val(), $('input[name=linkUrl]').val(), '%s', ($('#pagename').val()), %d, ($('#paralinkname').val()), '%s', ($('#paragraphname').val()));\">%s</button>\n", username, paragraphPosition, action, add ? Strings.Add : delete ? Strings.Delete : Strings.Edit);
		} else {
			buttonTxt= String.format("<button data-inline=\"true\" onclick=\"addLink(self, $('input:text[name=linkName]').val(), $('input[name=linkUrl]').val(), '%s', '%s', %d, %d, '%s', '%s')\">%s</button>\n", username, pageName, paragraphPosition, linkPosition, action, paragraphName, add ? Strings.Add : delete ? Strings.Delete : Strings.Edit);
		}
		
		
		html+= String.format("<div data-role=\"fieldcontain\"><label for=\"linkName\">%s "+Strings.Name+":</label><input type=\"text\" name=\"linkName\" id=\"linkName\" class=\"linkName\" value=\"%s\" /></div>\n", paragraph ? "Paragraph" : page ? "Page" : "Link", paragraph ? paragraphName : page ? pageName : linkName);
		if(!(paragraph || page)) html+= String.format("<div data-role=\"fieldcontain\"><label for=\"linkUrl\">"+Strings.LinkURL+":</label><input type=\"url\" name=\"linkUrl\" id=\"linkUrl\" class=\"linkUrl\" value=\"%s\" /></div>\n", linkUrl);
		
		System.out.println(buttonTxt);
		html+= buttonTxt;
		
		html+= "</body>\n";
		html+= "</html>\n";
		
		return html;
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String user= (String)request.getParameter("user"); //session.getAttribute("username");
		final String password= (String)request.getParameter("password");
		final String page= (String)request.getParameter("page");
		final String linkUrl= (String)request.getParameter("link_url");
		final String linkName= (String)request.getParameter("link_name");
		final String paragraphName= (String)request.getParameter("paragraph_name");
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
		final String action= (String)request.getParameter("action");
		/*if(null != action && action.endsWith("ADDFROMTOOLBARLINK")) {
			request.getSession(true).setAttribute("role", "user");
		}*/
		
		response.getWriter().println(createPage(user, password, linkName, linkUrl, page, paragraphPos, linkPos, action, paragraphName));
	}
}
