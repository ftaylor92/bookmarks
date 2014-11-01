package com.fmt.bookmark.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet wrapper which allows access only through Access Control Filter servlet.
**/
@SuppressWarnings("serial")
public class PrivateLinx extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SelectLinx linx= new SelectLinx();
		linx.doGet(request, response);
	}
}
