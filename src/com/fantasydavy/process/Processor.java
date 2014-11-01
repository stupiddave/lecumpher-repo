package com.fantasydavy.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import net.bican.wordpress.Wordpress;

import org.json.JSONException;
import org.json.JSONObject;

import com.fantasydavy.utilities.GmailHandler;
import com.fantasydavy.utilities.InputLoaderImpl;
import com.fantasydavy.utilities.TeamTotalPointsComparatorImpl;
import com.fantasydavy.utilities.interfaces.EmailHandler;

public class Processor {

	private ArrayList<Team> teams = new ArrayList<Team>();
	private List<Integer> ownerIds;
	private DAO dao;
	private InputLoaderImpl il;
	private String standingsTable;

	public void doWeeklyScoring() {
		il = new InputLoaderImpl();
		this.ownerIds = il.loadOwnerIds();
		try {
			buildTeams();
		} catch (Exception e) {
			e.printStackTrace();
		}

		doTeamScoring();
	}

	void createStandingsTable() {
		Collections.sort(teams, new TeamTotalPointsComparatorImpl());
		StringBuilder sb = new StringBuilder(
				"<table id=\"standings\"><tr><th id=\"pos\">Pos</th><th id=\"team\">Team</th><th id=\"owner\">Owner</th><th id=\"gwpoints\">Gameweek Points</th><th id=\"totalpoints\">Total Points</th></tr>");
		for (Team team : teams) {
			int position = teams.indexOf(team) + 1;
			sb.append("<tr><td>" + position
					+ "</td><td><a href=\"http://petehuey.com/hfl/"
					+ team.getTeamName().replace(" ", "-").replace("'", "")
					+ "/\">" + team.getTeamName() + "</a></td><td>"
					+ team.getOwnerName() + "</td><td>"
					+ team.getGameweekPoints() + "</td><td>"
					+ team.getTotalPoints() + "</td></tr>");
		}
		sb.append("</table>");
		standingsTable = sb.toString();
	}

	private void doTeamScoring() {
		for (Team team : teams) {
			team.evaluateScores();
		}
	}

	private void buildTeams() throws Exception {
		System.out.println("Building teams...");
		this.dao = new DAO();
		for (int owner : ownerIds) {
			Team team = new Team();
			System.out.println("Building team for owner ID: " + owner);
			try {
				ResultSet rs = dao.getTeamInformation(owner);
				team.setInformation(rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			buildTeamsheet(team);
			team.makeSubstitutions();

			if (team.isValidFormation()) {
				teams.add(team);
			} else {
				System.out.println("The formation for " + team.getTeamName()
						+ " is invalid, please review.");
				throw new Exception();
			}
		}
	}

	private void buildTeamsheet(Team team) throws Exception {
		il = getInputLoader();
		ArrayList<Player> players = new ArrayList<Player>();
		try {
			String[] teamsheet = il.parseTeamsheet(team.getOwnerId());
			for (int i = 0; i < teamsheet.length; i++) {
				Player player = new Player();
				if (teamsheet[i].contains("vc")) {
					player.setViceCaptain(true);
					player.setPlayerId(Integer.parseInt(teamsheet[i].replace(
							"vc", "")));
					team.setSelectedViceCaptain(player);
				} else if (teamsheet[i].contains("c")) {
					player.setCaptain(true);
					player.setPlayerId(Integer.parseInt(teamsheet[i].replace(
							"c", "")));
					team.setSelectedCaptain(player);
				} else {
					player.setPlayerId(Integer.parseInt(teamsheet[i]));
				}

				if (!isValidOwnership(team, player.getPlayerId())) {
					System.out
							.println("Player with id: "
									+ player.getPlayerId()
									+ " \n doesn't belong to owner with \n ID: "
									+ team.getOwnerId()
									+ "\n Name: "
									+ team.getOwnerName()
									+ "\n but is listed on their teamsheet, please amend.");
					throw new Exception();
				}
				player.setOwnerId(team.getOwnerId());

				populatePlayerInfo(player);
				players.add(player);
				System.out.println("Just added " + player.getCommonName()
						+ " to " + team.getTeamName());
			}
		} catch (FileNotFoundException e) {
			System.out.println("Can't find teamsheet file for owner ID: "
					+ team);
			e.printStackTrace();
		}
		team.setTeamsheet(players);
	}

	private InputLoaderImpl getInputLoader() {
		if (il != null) {
			return new InputLoaderImpl();
		} else {
			return il;
		}
	}

	private void populatePlayerInfo(Player player) throws JSONException {
		JSONObject playerJSON = createJsonObject(player.getPlayerId());
		player.setFirstName(playerJSON.getString("first_name"));
		player.setSecondName(playerJSON.getString("second_name"));
		player.setCommonName(playerJSON.getString("web_name"));
		player.setPosition(playerJSON.getString("type_name"));
		player.setTeamName(playerJSON.getString("team_name"));
		player.setEventExplain(playerJSON.getJSONArray("event_explain"));
		player.setGameweekTotal(playerJSON.getInt("event_total"));
		// setMinutesPlayed assumes there will always be an element at index 0
		// on event explain array.
		if (player.getEventExplain().length() == 0) {
			player.setMinutesPlayed(0);
		} else {
			player.setMinutesPlayed(player.getEventExplain().getJSONArray(0)
					.getInt(1));
		}
	}

	private boolean isValidOwnership(Team team, int playerId) {
		DAO dao = new DAO();
		ResultSet rs = dao.getPlayerSummary(playerId);
		try {
			while (rs.next()) {
				if (rs.getInt("owner_id") == team.getOwnerId()) {
					return true;
				} else {
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	public void updatePlayerInfo() {
		this.dao = new DAO();
		dao.updatePlayerStats();
	}

	static JSONObject createJsonObject(int playerId) {
		BufferedReader reader = null;
		JSONObject jsonObject = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new URL(
					"http://fantasy.premierleague.com/web/api/elements/"
							+ playerId + "/").openStream()));
		} catch (IOException e) {
			try {
				Thread.sleep(1000);
				reader = new BufferedReader(new InputStreamReader(new URL(
						"http://fantasy.premierleague.com/web/api/elements/"
								+ playerId + "/").openStream()));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				try {
					Thread.sleep(1000);
					reader = new BufferedReader(new InputStreamReader(new URL(
							"http://fantasy.premierleague.com/web/api/elements/"
									+ playerId + "/").openStream()));
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				} catch (MalformedURLException e2) {
					e2.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
		StringBuffer buffer = new StringBuffer();
		int read;
		char[] chars = new char[1024];
		try {
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			jsonObject = new JSONObject(buffer.toString());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		if (reader != null)
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return jsonObject;

	}

	static JSONObject createJsonObject(String url, int playerId) {
		JSONObject jsonObject = new JsonParser().parseFile(url);
		return jsonObject;
	}

	public void sendWeeklyTeamEmail(Boolean isFinal) {
		EmailHandler emailHandler = new GmailHandler();
		for (Team team : teams) {
			StringBuilder emailText = new StringBuilder();
			emailText.append(createInlineStylingForEmail());
			emailText.append(emailHandler.buildEmailPlayerText(team));
			if (isFinal) {
				emailText
						.append("<p>Here are the final standings for this gameweek:</p>");
			} else {
				emailText
						.append("<p>Overall standings so far this gameweek:</p>");
			}

			emailText.append(standingsTable);
			emailText
					.append("<p>Find everything you need at the <a href=\"http://petehuey.com/hfl/\">HFL website</a></p>");
			emailHandler.sendEmail(emailText.toString(), team.getOwnerEmail());
		}
	}

	private String createInlineStylingForEmail() {
		StringBuilder inlineStyle = new StringBuilder();
		inlineStyle
				.append("<head><style>"
						+ "#standings td {"
						+ "padding: 5px;}"
						+ "tr:nth-child(even) {background: #EEEEEE}"
						+ "tr:nth-child(odd) {background: #FFFFFF}"
						+ "@media only screen and (max-width: 768px) {"
						+ "#owner, tr td:nth-child(3){ display:none; visibility:hidden; }"
						+ "} </style></head>");
		return inlineStyle.toString();
	}

	public void publishTableToWeb() {
		WordPressClient wpClient = new WordPressClient();
		wpClient.publishStandingsPage(standingsTable);
	}

	public void uploadInfoToDropbox() {

	}

	public void finaliseWeeklyScores() {
		for (Team team : teams) {
			dao.saveTeamScores(team);
		}
	}

	public void updateTeamWebPage() {
		WordPressClient wpClient = new WordPressClient();
		for (Team team : teams) {
			wpClient.publishTeamPage(team);
		}
	}
}
