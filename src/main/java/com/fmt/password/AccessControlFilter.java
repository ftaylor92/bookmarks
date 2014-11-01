package com.fmt.password;
 
import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.fmt.bookmark.intl.Strings;

/** Access Control Filter. **/
public class AccessControlFilter implements Filter {
 
	@Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
 
    	HttpServletRequest request = (HttpServletRequest) req;
    	//HttpServletResponse response = (HttpServletResponse) req;
         
        //Get the IP address of client machine.
        String ipAddress = request.getRemoteAddr();
         
        //Log the IP address and current timestamp.
        System.out.println("IP "+ipAddress + ", Time "+ new Date().toString());
        
        String message= "";
        String role= null;
        HttpSession session = request.getSession();
		final String user= (String)request.getParameter("user");
		final String password= (String)request.getParameter("password");
		final String site= "com.fmt.bookmarks";
        try {
			if(null != session)	{
				role= (String)session.getAttribute("role");
				if(null == role) {
					if(!(null == user || user.isEmpty() || null == password || password.isEmpty())) {
			    		Secure.queryForUsers(user, password, site, session);
			    		session = request.getSession();
			    		role= (String)session.getAttribute("role");
					}
				}
			}
			else {
				message+= Strings.nullSession;
			}
			
			if(null == role || !role.contains("user")) {
				if(!(null == user || user.isEmpty() || null == password || password.isEmpty() || null == site || site.isEmpty())) {
		    		Secure.queryForUsers(user, password, site, session);
		    		session = request.getSession();
		    		role= (String)session.getAttribute("role");
				}
				if(null == role || !role.contains("user")) {
					if(null == role) {
						message+= ", role == null";
					} else if(!role.contains("user")) {
						message+= ", role == "+ role;
					}
					res.getWriter().write(String.format(Strings.invaliduser+ ", %s\n\n"+Strings.pleaselogin+" http://fmtmac-bookmarks.herokuapp.com", message).replaceAll(", ,", ","));
				} else {
					chain.doFilter(req, res);
				}
			} else {
				chain.doFilter(req, res);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			res.getWriter().write(ex.getMessage().replaceAll(", ,", ","));
		}
    }
	
	@Override
    public void init(FilterConfig config) throws ServletException {
         
        //Get init parameter
        String testParam = config.getInitParameter("test-param");
         
        //Print the init parameter
        System.out.println("Test Param: " + testParam);
    }
	
	@Override
    public void destroy() {
        //add code to release any resource
    }
}