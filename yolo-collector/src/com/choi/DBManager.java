package com.choi;

import java.sql.Connection;

public class DBManager {
	private static DBManager instance = null;

	private Connection sqlite = null;

	private boolean connect = false;

	private DBManager() {
	}
	
	public static DBManager getInstance() {
		if(instance == null) {
			instance = new DBManager();
			instance.connect();
		}
		
		return instance;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public Connection getConnection() throws Exception {
		if (sqlite == null)
			throw new Exception("Not Connected");

		return sqlite;
	}

	/**
	 * 
	 */
	public void connect() {
		try {
			Class.forName("org.sqlite.JDBC");

			StringBuffer url = new StringBuffer();
			url.append("jdbc:sqlite:./yolo.db");

			sqlite = java.sql.DriverManager.getConnection(url.toString());

			System.out.println("Connected ...");

			connect = true;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void close() {
		if (sqlite != null) {
			try {
				sqlite.close();
			} catch (Exception e) {
			}
		}

		connect = false;
	}

	/**
	 * @throws Exception
	 */
	public void reconnect() throws Exception {
		if (sqlite != null) {
			try {
				sqlite.close();
			} catch (Exception e) {
			}
		}

		try {
			connect();
			connect = true;
		} catch (Exception e) {
			connect = false;
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @return
	 */
	public boolean isConnect() {
		return connect;
	}
}
