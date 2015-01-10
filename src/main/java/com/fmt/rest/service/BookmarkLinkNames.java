package com.fmt.rest.service;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
@Path("/linknames")
public class BookmarkLinkNames {
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
		System.out.printf("GET(getLinx-LN): user: %s, pass: %s, site: %s, pageName: %s, action: %s, linkPosition: %s, linkName: %s\n", user, pass, site, pageName, action, linkPosition, linkName);
		
		CacheControl cc = new CacheControl();
		cc.setMaxAge(60);
		cc.setNoCache(true);
		
		List<String> linx= null;

		if(!(null == user || user.isEmpty())) {
			linx= getParagraphLinkNames(user, pageName, paragraphName);
		}
		
		if(null == linx) {
			linx= new ArrayList<>();
			linx.add(String.format("failed: username=%s, page=%s", user, pageName));
		}

		ResponseBuilder rb = Response.status(stat).entity(linx);
		System.out.println("Response: "+ linx.toString());
		return rb.cacheControl(cc).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods","GET").build();
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
		System.out.printf("SELECT link_name FROM bookmark_linx WHERE user='%s' AND page_name='%s' AND paragraph_name='%s';\n", user, pageName, paragraphName);
		String sql= "SELECT link_name FROM bookmark_linx WHERE user=? AND page_name=? AND paragraph_name=?;";
		
		//linkNames= HerokuConnection.getSpringConnection().query(sql, new Object[]{user, pageName, paragraphName}, (row, nRow) -> row.getString("link_name"));
		
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
}
