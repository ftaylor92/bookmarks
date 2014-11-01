package com.fmt.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * connection to EC2 database.
**/
public class CloudbeesConnection {
	/** connection to database. **/
	private static Connection connection = null;
	
	/** returns singleton connection to database. **/
	public static Connection getConnection() {
		if(null == connection) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				// Setup the connection with the DB
				connection = DriverManager.getConnection("jdbc:postgres://dhjxmgwllqrxiy:jHAlfUz0r_9gGBie7hPksPVpWn@ec2-54-235-245-180.compute-1.amazonaws.com:5432/dbgj0tjogthpr8?");
					//+ "user=dhjxmgwllqrxiy&password=jHAlfUz0r_9gGBie7hPksPVpWn");
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
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
}
