package com.fantasydavy.process;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabasePrimer {

	public static void main(String[] args) {
		Connection conn = DAO.getDatabaseConnection();
		if ("prime".equals(args[0])) {
			setupTheDatabase(conn);
		} else if ("update".equals(args[0])) {
			updatePlayerOwnership(conn);
		}
	}
		static void setupTheDatabase(Connection conn) {
			DAO.createFantasyTeamTable(conn);
			DAO.insertOwners(conn);
			DAO.createPlayerTable(conn);
			DAO.insertAllPlayers(conn);
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		public static void updatePlayerOwnership(Connection conn) {
			DAO.createPlayerTable(conn);
			DAO.insertAllPlayers(conn);
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		}
}
