package com.fmt.bookmark.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * File Upload Servlet.
 **/
@SuppressWarnings("serial")
public class PostFileForm extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		final String user= (String)request.getParameter("user");
		String html= "";

		html+= "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n";
		html+= "\"http://www.w3.org/TR/html4/loose.dtd\">\n";
		html+= "<html>\n";
		html+= "<head>\n";
		html+= "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n";
		html+= "<meta name=\"viewport\" content=\"width=device-width, initial-scale=0.75\">\n";
		html+= "<link rel=\"stylesheet\" href=\"./jquery/jquery.mobile.css\" />\n";
		html+= "<title>File Upload</title>\n";
		html+= "</head>\n";
		html+= "<body>\n";

		html+= "<form method=\"POST\" action=\"upload\" enctype=\"multipart/form-data\">\n";
		html+= "<input type=\"hidden\" name=\"username\" value=\""+user+"\" />\n";
		html+= "Select file to upload links from: <input style=\"width: 500px;\" type=\"file\" name=\"uploadFile\" />\n";
		//html+= "<br/>\n";
		html+= "<input style=\"width: 500px;\" type=\"submit\" value=\"Upload\" />\n";
		html+= "</form>\n";
		html+= "</body>\n";
		html+= "</html>\n";
		response.getWriter().println(html);
	}
}
