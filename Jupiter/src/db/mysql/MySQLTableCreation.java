package db.mysql;

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;

public class MySQLTableCreation {
	// Run this as Java application to reset db schema.
	public static void main(String[] args) {
		try {
			//Connect to MySQL.
			System.out.println("Connecting to " + MySQLDBUtil.URL);
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			Connection conn = DriverManager.getConnection(MySQLDBUtil.URL);
			
			if (conn == null) {
				return;
			}
			

			//Drop tables in case they exist.
			Statement statement = conn.createStatement();
			String sql = "DROP TABLE IF EXISTS categories";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS history";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS items";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS users";
			statement.executeUpdate(sql);

			//create new tables
			sql = "CREATE TABLE items ("
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "name VARCHAR(255),"
					+ "rating FLOAT,"
					+ "address VARCHAR(255),"
					+ "image_url VARCHAR(255),"
					+ "url VARCHAR(255),"
					+ "distance FLOAT,"
					+ "start_date VARCHAR(255),"
					+ "start_time VARCHAR(255),"
					+ "PRIMARY KEY (item_id)"
					+ ")";
			statement.executeUpdate(sql);

			sql = "CREATE TABLE users ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "password VARCHAR(255) NOT NULL,"
					+ "first_name VARCHAR(255),"
					+ "last_name VARCHAR(255),"
					+ "PRIMARY KEY (user_id)"
					+ ")";
			statement.executeUpdate(sql);

			sql = "CREATE TABLE categories ("
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "category VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (item_id, category),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id)"
					+ ")";
			statement.executeUpdate(sql);

			sql = "CREATE TABLE history ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "PRIMARY KEY (user_id, item_id),"
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id)"
					+ ")";
			statement.executeUpdate(sql);
			
			//test if db can insert
			sql = "INSERT INTO users VALUES('1111', '3229c1097c00d497a0fd282d586be050', 'Zhonghao', 'Lu')";
			statement.executeUpdate(sql);

			
			conn.close();
			System.out.println("Import done successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}

//deploy on AWS
//DROP DATABASE IF EXISTS laiproject;
//CREATE DATABASE laiproject;
//USE laiproject;
//CREATE TABLE items (item_id VARCHAR(255) NOT NULL, name VARCHAR(255), rating FLOAT, address VARCHAR(255), image_url VARCHAR(255), url VARCHAR(255), distance FLOAT, start_date VARCHAR(255), start_time VARCHAR(255), PRIMARY KEY ( item_id));
//CREATE TABLE categories (item_id VARCHAR(255) NOT NULL, category VARCHAR(255) NOT NULL, PRIMARY KEY ( item_id, category), FOREIGN KEY (item_id) REFERENCES items(item_id));    
//CREATE TABLE users (user_id VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, first_name VARCHAR(255), last_name VARCHAR(255), PRIMARY KEY ( user_id ));
//CREATE TABLE history (user_id VARCHAR(255) NOT NULL, item_id VARCHAR(255) NOT NULL, last_favor_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (user_id, item_id), FOREIGN KEY (item_id) REFERENCES items(item_id), FOREIGN KEY (user_id) REFERENCES users(user_id));
//INSERT INTO users VALUES ("1111", "3229c1097c00d497a0fd282d586be050", "Zhonghao", "Lu");
