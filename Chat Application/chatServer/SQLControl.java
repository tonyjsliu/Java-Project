package chatServer;

import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.*;

public class SQLControl {
	
	private Connection connection;
	
	// insert data into sql
	public boolean insert(String[] strs) {
		try {
			if (checkData(strs[0], strs[2]))
				return false;
			String query = "INSERT INTO person values ('" + strs[0] + "','" + strs[1] + "','" + strs[2] + "')";
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
			statement.close();
			return true;
		} catch (SQLException ex) {
			return false;
		}
	}
	
	// check whether data is created with same username or email
	public boolean checkData(String username, String email) {
		try {
			String query = "SELECT * FROM person where username = '" + username + "' or email = '" + email + "'";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet resultSet = preparedStatement.executeQuery();
			boolean check = resultSet.next();
			preparedStatement.close();
			resultSet.close();
			return check;
		} catch(SQLException ex) {
			return false;
		}
	}
	
	// check whether data is in sql
	public boolean queryData(String[] strs) {
		try {
			String query = "SELECT * FROM person where username = '" + strs[0] + "' and password = '" + strs[1] + "'";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet resultSet = preparedStatement.executeQuery();
			boolean check = resultSet.next();
			preparedStatement.close();
			resultSet.close();
			return check;
		} catch(SQLException ex) {
			return false;
		}
	}
	
	// connect sql server
	public void openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:8889/chat", "root", "root");
			System.out.println("Connection established successfully with the database server.");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void main(String[] args) {
		SQLControl i = new SQLControl();
		i.openConnection();
	}
	
}
