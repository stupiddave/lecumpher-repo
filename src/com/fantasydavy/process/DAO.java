package com.fantasydavy.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.json.JSONException;
import org.json.JSONObject;

import com.fantasydavy.utilities.InputLoaderImpl;

public class DAO {

	static void insertOwners(Connection conn) {
		InputLoaderImpl il = new InputLoaderImpl();
		ArrayList<String[]> owners = il.loadOwnerInfo();
		for (String[] owner : owners) {
			try {
				System.out.println("Inserting info for " + owner[1]);
				PreparedStatement statement = conn
						.prepareStatement("INSERT INTO fantasy_team_t (owner_id, owner_name, owner_email, team_name) VALUES (?, ?, ?, ?)");
				statement.setInt(1, owners.indexOf(owner) + 1);
				statement.setString(2, owner[0]);
				statement.setString(3, owner[1]);
				statement.setString(4, owner[2]);
				statement.executeUpdate();

			} catch (SQLException e) {
				System.out
						.println("Issue inserting owner details into fantasy_team_t.");
				e.printStackTrace();
			}

		}
	}

	static void createFantasyTeamTable(Connection conn) {
		Statement statementDropTable;
		Statement statementCreateTable;
		try {
			statementDropTable = conn.createStatement();
			statementDropTable.execute("DROP TABLE fantasy_team_t");
		} catch (SQLException e) {
			if (e.getErrorCode() == 30000) {
				System.out
						.println("fantasy_team_t table didn't exist to drop.  Proceeding...");
			} else {
				e.printStackTrace();
			}
		}
		try {
			statementCreateTable = conn.createStatement();
			statementCreateTable
					.execute("CREATE TABLE fantasy_team_t(owner_id int NOT NULL, owner_name varchar(40), owner_email varchar(100), "
							+ "team_name varchar(40), team_gameweek_points int, team_total_points int)");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	static void insertAllPlayers(Connection conn) {
		InputLoaderImpl il = new InputLoaderImpl();
		for (int owner = 1; owner < 9; owner++) {
			ArrayList<Integer> player_ids = il.loadSquads(owner);
			for (int player : player_ids) {
				StringBuilder sb = new StringBuilder("INSERT INTO "
						+ "player_t (player_id, owner_id) values (");
				sb.append(player + ", " + owner + ")");
				try {
					PreparedStatement statement = conn.prepareStatement(sb
							.toString());
					statement.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	static void createPlayerTable(Connection conn) {
		try {
			Statement statementDropTable = conn.createStatement();
			statementDropTable.execute("DROP TABLE player_t");
		} catch (SQLException e) {
			if (e.getErrorCode() == 30000) {
				System.out
						.println("player_t table didn't exist to drop.  Proceeding...");
			} else {
				e.printStackTrace();
			}
		}
		try {
			Statement statementCreateTable = conn.createStatement();
			statementCreateTable
					.execute("CREATE TABLE player_t(player_id int NOT NULL, owner_id int NOT NULL)");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	static Connection getDatabaseConnection() {
		try {
			EmbeddedDriver driver = new EmbeddedDriver();
			Connection conn = driver.connect("jdbc:derby:FF_DB;create=true;",
					null);
			return conn;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ResultSet getTeamInformation(int ownerId) {
		Connection conn = getDatabaseConnection();
		try {
			PreparedStatement statement = conn
					.prepareStatement("SELECT owner_id, owner_name, team_name, owner_email, team_gameweek_points, team_total_points "
							+ "FROM fantasy_team_t WHERE owner_id = " + ownerId);
			ResultSet rs = statement.executeQuery();
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ResultSet getPlayerSummary(int playerId) {
		Connection conn = getDatabaseConnection();
		try {
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT player_id, owner_id"
					+ " FROM player_t WHERE player_id = " + playerId);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void updatePlayerStats() {
		for (int playerId = 1; playerId < 538; playerId++) {
			JSONObject playerJson = Processor.createJsonObject(playerId);
			if (playerId % 20 == 0) {
				System.out.println("Updated player " + playerId);
			}
			if (playerJson != null) {
				try {
					StringBuilder sb = new StringBuilder(
							"UPDATE player_t SET gameweek_total = ");
					sb.append(playerJson.getInt("event_total"));
					sb.append(", gameweek_detail = ");
					sb.append(" WHERE player_id = " + playerId);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void saveTeamScores(Team team) {
		Connection conn = getDatabaseConnection();
		StringBuilder sb = new StringBuilder(
				"UPDATE fantasy_team_t SET team_gameweek_points = ");
		sb.append(team.getGameweekPoints());
		sb.append(", team_total_points = ");
		sb.append(team.getTotalPoints());
		sb.append(" WHERE owner_id = " + team.getOwnerId());
		PreparedStatement statement;
		try {
			statement = conn.prepareStatement(sb.toString());
			statement.executeUpdate();
			System.out.println("Team total scores updated in the database.");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void resetTeamScores() {
		Connection conn = getDatabaseConnection();
		PreparedStatement statement;
		String sql = "UPDATE fantasy_team_t SET team_total_points = 0";
		try {
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Scores have been reset.");
	}
}
