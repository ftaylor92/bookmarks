package com.fmt.password;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fmt.bookmark.intl.Strings;
import com.fmt.database.HerokuConnection;

/**
 * Security servlet.
 **/
@SuppressWarnings("serial")
public class Secure extends HttpServlet {

	/**
	 * queries for user and populates session with proper values.
	 * @param username user name
	 * @param password password for username
	 * @param site which site is associated with username
	 * @param session current web session
	 **/
	public static void queryForUsers(String username, String password, String site, HttpSession session) throws IOException{
		boolean badSite= false;
		boolean badPassword= false;
		boolean badUsername= true;
		boolean allMatch= false;
		
		System.out.printf("SELECT role_name, password, site_name FROM rest_users WHERE user_name=%s\n", username);
		final String sql= "SELECT role_name, password, site_name FROM rest_users WHERE user_name=?";

		try {
			PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
			preparedStatement.setString(1, username);
			ResultSet resultSet= preparedStatement.executeQuery();
			
			String retRole= null;
			String retPass= null;
			String retSite= null;
			while(resultSet.next()) {
				badUsername= false;
				retPass= resultSet.getString("password");
				retSite= resultSet.getString("site_name");
				retRole= resultSet.getString("role_name");
				if(retSite.equals(site))  {
					badSite= false;
					if(retPass.equals(password)) {
						allMatch= true;
						badPassword= false;
						break;
					} else {
						badPassword= true;
					}
				} else {
					badSite= true;
				}
			}
			resultSet.close();
			preparedStatement.close();
			
			if(allMatch) {
				session.setAttribute("role", retRole);
				//contact.setStatus(Contact.returnCodes.success);
			} else if(badSite) {
				session.setAttribute("role", Strings.badSite);
				throw new IOException(String.format(Strings.username+"(%s) "+Strings.accountnot+"(%s)", username, site));
			} else if(badPassword) {
				session.setAttribute("role", Strings.badPassword);
				throw new IOException(String.format(Strings.incorrectPass+"(%s), "+Strings.site+"(%s)", username, site));
			} else if(badUsername) {
				session.setAttribute("role", Strings.badUsername);
				throw new IOException(String.format(Strings.nousername+"(%s) "+Strings.definedfor+"(%s)", username, site));
			} else {
				throw new IOException(Strings.unkErr);
				//contact.setStatus(Contact.returnCodes.fail);
			}
		} catch (SQLException e) {
			throw new IOException(Strings.noDB);
		} catch(NullPointerException e) {
			throw new IOException(Strings.noDB);
		}
		
		HerokuConnection.close();
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final HttpSession session = request.getSession(true);
		final String user= request.getParameter("j_username");
		final String pass= request.getParameter("j_password");
		//final String role= request.getParameter("role_name");
		final String site= request.getParameter("site_name");
		
		//gets role
		queryForUsers(user, pass, site, session);
		final String role= (String)session.getAttribute("role");
		
		if(null != role && role.contains("user")) {
			response.sendRedirect("page?user="+ user);
		} else {
			System.out.printf("user=%s pass=%s role=%s\n", user, pass, role);
			response.sendRedirect("fail-login.jsp");
		}
	}
}