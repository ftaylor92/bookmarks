package com.fmt.rest.service;

import java.net.URLDecoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

/** counter for how many hits this page gets. **/
@Path("/counter")
public class Counter {
	
	/**
	 * GET role, given site, user, pass
	 * @param user username
	 * @param pass password
	 * @param site site associated with username and password
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
			@QueryParam("action") String action) {
		Status stat= Response.Status.OK;
		boolean success= false;
		System.out.printf("GET(getLinx-C): user: %s, pass: %s, site: %s, pageName: %s, action: %s\n", user, pass, site, pageName, action);
		
		CacheControl cc = new CacheControl();
		cc.setMaxAge(60);
		cc.setNoCache(true);
		
		int count= -1;
		
		if(!(null == site || site.isEmpty())) {
			site= URLDecoder.decode(site);

			System.out.printf("SELECT site, count FROM counter WHERE site='%s';\n", site);
			String sql= "SELECT site, count FROM counter WHERE site=?;";
	
			HerokuConnection.getConnection();
			try {
				PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
				preparedStatement.setString(1, site);
				ResultSet resultSet= preparedStatement.executeQuery();
				
				while(resultSet.next()) {
					count= resultSet.getInt("count");
					break;
				}
				
				resultSet.close();
				preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			if(-1 == count) {
				System.out.printf("INSERT INTO counter (site, count) VALUES ('%s', %d);\n", site, 1);
				sql= "INSERT INTO counter (site, count) VALUES (?, ?);";
				
				try {
					PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
					preparedStatement.setString(1, site);
					preparedStatement.setInt(2, 1);
					success= !preparedStatement.execute();
					preparedStatement.close();
					count= 1;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				count++;
				System.out.printf("UPDATE counter SET count=%d WHERE site='%s';\n", count, site);
				sql= "UPDATE counter SET count=? WHERE site=?;";
				
				try {
					PreparedStatement preparedStatement= HerokuConnection.getConnection().prepareStatement(sql);
					preparedStatement.setInt(1, count);
					preparedStatement.setString(2, site);
					success= !preparedStatement.execute();
					preparedStatement.close();
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			HerokuConnection.close();
		} else {
			count= -2;
		}
		
		if(!success) {
			stat= Response.Status.NOT_FOUND;
		}

		ResponseBuilder rb = Response.status(stat).entity(count);
		System.out.println("Response: "+ count);
		return rb.cacheControl(cc).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods","GET").build();
	}

}
