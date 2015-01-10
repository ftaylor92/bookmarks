package com.fmt.database;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * connection to EC2 database.
**/
public class CloudbeexConnection {
	/** connection to database. **/
	private static Connection connection = null;
	private static JdbcTemplate activeSpringDatabaseConnection= null;
	
	public static JdbcTemplate getSpringConnection() {
		if(null == activeSpringDatabaseConnection) {
			activeSpringDatabaseConnection= new JdbcTemplate(createApacheDataSource());
		}
		
		return activeSpringDatabaseConnection;
	}
	
	public static DataSource createApacheDataSource() {
	    try {
			URI dbUri = new URI("mysql://root:matt88@localhost:3306/cloudbees");
			String username = dbUri.getUserInfo().split(":")[0];
			String password = dbUri.getUserInfo().split(":")[1];
			String dbUrl = "jdbc:mysql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath()+"?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

			BasicDataSource dataSource= new BasicDataSource();
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
			dataSource.setUrl(dbUrl);
			dataSource.setUsername(username);
			dataSource.setPassword(password);
			dataSource.setRemoveAbandoned(true);
			dataSource.setMaxActive(30);
			
			return dataSource;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/** returns singleton connection to database. **/
	public static Connection getConnection() {
		if(null == connection) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				// Setup the connection with the DB
				//connection = DriverManager.getConnection("jdbc:mysql://root:matt88@localhost:3306/cloudbees");
					//+ "user=dhjxmgwllqrxiy&password=jHAlfUz0r_9gGBie7hPksPVpWn");
			    URI dbUri = new URI("mysql://root:matt88@localhost:3306/cloudbees");
			    //ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory
			    
			    String username = dbUri.getUserInfo().split(":")[0];
			    String password = dbUri.getUserInfo().split(":")[1];
			    String dbUrl = "jdbc:mysql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

			    return DriverManager.getConnection(dbUrl, username, password);

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		
		return connection;
	}
	
	/**
	 * closes connection to database.
	**/
	public static void close() {
		if(null != connection) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		connection= null;
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	public static DataSource getTomcatDataSource() {
		try {
			Context initContext= new InitialContext();
			Context envContext= (Context)initContext.lookup("java:/comp/env");
			DataSource ds= (DataSource)envContext.lookup("jdbc/StackDB");
			//Connection c= ds.getConnection();
			return ds;
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
