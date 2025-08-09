package com.db;
import java.sql.*;

public class DBConnection {

	public static Connection getConnection() throws SQLException {
	    Connection conn = DriverManager.getConnection(
	        "jdbc:mysql://localhost:3306/mini_crm", 
	        "root", 
	        "Dinesh@3778"
	    );
	    conn.setAutoCommit(true);
	    return conn;
	}

}
