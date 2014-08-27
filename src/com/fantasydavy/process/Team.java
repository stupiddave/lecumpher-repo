package com.fantasydavy.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;

public class Team {
	private String ownerEmail;
	private int gameweekPoints;
	private int ownerId;
	private String ownerName;
	// private ArrayList<Player> subs;
	private String teamName;
	private ArrayList<Player> teamsheet;
	private int totalPoints;
	private ArrayList<Player> starters;
	private ArrayList<Player> subs;
	private ArrayList<Player> unplayedStarters;
	private ArrayList<Player> unplayedSubs;
	private Player selectedCaptain;
	private Player selectedViceCaptain;
	private Player actualCaptain;

	public Player getActualCaptain() {
		return actualCaptain;
	}

	public void setActualCaptain(Player actualCaptain) {
		this.actualCaptain = actualCaptain;
	}

	public Player getSelectedCaptain() {
		return selectedCaptain;
	}

	public void setSelectedCaptain(Player selectedCaptain) {
		this.selectedCaptain = selectedCaptain;
	}

	public int getGameweekPoints() {
		return gameweekPoints;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public String getTeamName() {
		return teamName;
	}

	public ArrayList<Player> getTeamsheet() {
		return teamsheet;
	}

	public int getTotalPoints() {
		return totalPoints;
	}

	public boolean isValidFormation(ArrayList<Player> team) {
		int goalkeeperCount = 0;
		int defenderCount = 0;
		int midfielderCount = 0;
		int forwardCount = 0;

		for (Player player : team) {
			if ("Goalkeeper".equals(player.getPosition())) {
				goalkeeperCount++;
			} else if ("Defender".equals(player.getPosition())) {
				defenderCount++;
			} else if ("Midfielder".equals(player.getPosition())) {
				midfielderCount++;
			} else if ("Forward".equals(player.getPosition())) {
				forwardCount++;
			}
		}
		if (goalkeeperCount == 1 && defenderCount > 2 && defenderCount < 6
				&& midfielderCount > 2 && midfielderCount < 6
				&& forwardCount > 0 && forwardCount < 4
				&& defenderCount + midfielderCount + forwardCount == 10) {
			return true;
		} else {
			return false;
		}
	}

	public void makeSubstitutions() throws Exception {
		starters = new ArrayList<Player>();
		subs = new ArrayList<Player>();
		unplayedStarters = new ArrayList<Player>();
		unplayedSubs = new ArrayList<Player>();
		ArrayList<Player> actualStarters = new ArrayList<Player>();
		for (Player player : teamsheet) {
			if (teamsheet.indexOf(player) < 11) {
				starters.add(player);
			} else {
				subs.add(player);
			}
		}
		if (isValidFormation(starters) == false) {
			System.out.println("The starting lineup for team " + teamName
					+ " was not in a valid formation.");
				throw new Exception();
		}
		for (Player player : starters) {
			Player playerToUse = player;
			if (player.getMinutesPlayed() == 0) {
				if ("Goalkeeper".equals(player.getPosition())) {
					Iterator<Player> subsIterator = subs.iterator();
					while (subsIterator.hasNext()) {
					Player subPlayer = subsIterator.next();
						if ("Goalkeeper".equals(subPlayer.getPosition())
								&& subPlayer.getMinutesPlayed() > 0) {
							unplayedStarters.add(player);
							playerToUse = subPlayer;
							subsIterator.remove();
							break;
						}
					}
				} else {
					Iterator<Player> subsIterator = subs.iterator();
					while (subsIterator.hasNext()) {
						Player subPlayer = subsIterator.next();
						int currentPosition = starters.indexOf(player);
						if (subPlayer.getMinutesPlayed() > 0) {
							starters.set(currentPosition, subPlayer);
							if (isValidFormation(starters)) {
								unplayedStarters.add(player);
								playerToUse = subPlayer;
								subsIterator.remove();
								break;
							} else {
								starters.set(currentPosition, player);
							}
						}
					}
				}
			}
			playerToUse.setSelected(true);
			actualStarters.add(playerToUse);
		}
		if (isValidFormation(actualStarters)) {
			starters = actualStarters;
			allocateCaptaincy();
		} else {
			try {
				throw new Exception();
			} catch (Exception e) {
				System.out.println("Issue making substitutions for " + teamName
						+ ".");
				e.printStackTrace();
			}
		}
		setUnplayedSubs(subs);
	}

	private void allocateCaptaincy() {
			if (selectedCaptain.getMinutesPlayed() == 0 && selectedViceCaptain.getMinutesPlayed() > 0) {
					setActualCaptain(selectedViceCaptain);
				} else {
					setActualCaptain(selectedCaptain);
				}
	}

	public void setGameweekPoints(int gameweekPoints) {
		this.gameweekPoints = gameweekPoints;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	// public void setSubs(ArrayList<Player> subs) {
	// this.subs = subs;
	// }

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public void setTeamsheet(ArrayList<Player> teamsheet) {
		this.teamsheet = teamsheet;
	}

	public void setTotalPoints(int totalPoints) {
		this.totalPoints = totalPoints;
	}

	public boolean isValidFormation() {
		return isValidFormation(starters);
	}

	public ArrayList<Player> getStarters() {
		return starters;
	}

	public void setStarters(ArrayList<Player> starters) {
		this.starters = starters;
	}

	public ArrayList<Player> getSubs() {
		return subs;
	}

	public void setSubs(ArrayList<Player> subs) {
		this.subs = subs;
	}

	public ArrayList<Player> getUnplayedStarters() {
		return unplayedStarters;
	}

	public void setUnplayedStarters(ArrayList<Player> unplayedStarters) {
		this.unplayedStarters = unplayedStarters;
	}

	public ArrayList<Player> getUnplayedSubs() {
		return unplayedSubs;
	}

	public void setUnplayedSubs(ArrayList<Player> unplayedSubs) {
		this.unplayedSubs = unplayedSubs;
	}

	public void evaluateScores() {
		gameweekPoints = 0;
		for (Player player : starters) {
			if (actualCaptain.equals(player)) {
				gameweekPoints = gameweekPoints + player.getGameweekTotal() + player.getGameweekTotal();
			} else {
				gameweekPoints = gameweekPoints + player.getGameweekTotal();
			}
		}
		totalPoints = totalPoints + gameweekPoints;
	}

	public Player getSelectedViceCaptain() {
		return selectedViceCaptain;
	}

	public void setSelectedViceCaptain(Player selectedViceCaptain) {
		this.selectedViceCaptain = selectedViceCaptain;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}

	public String createStartingLineupTableForEmail() {
		StringBuilder sb = new StringBuilder(
				"<table id=\"playerscoring\">"
						+ "<tr> <th>Name</th> <th>Team</th> <th>Points</th> <th>Detail</th> </tr>");
		for (Player player : getStarters()) {
			if (getStarters().indexOf(player) % 2 == 0) {
				sb.append("<tr bgcolor=\"#EEEEEE\">");
			} else {
				sb.append("<tr bgcolor:\"#FFFFFF\">");
			}
			sb.append("<td>" + player.getCommonName());
	
			if (player.equals(getActualCaptain())) {
				sb.append(" (c)");
			}
			sb.append("</td> <td>" + player.getTeamName() + "</td> <td>");
			if (player.equals(getActualCaptain())) {
					sb.append(player.getGameweekTotal() * 2);
			} else {
				sb.append(player.getGameweekTotal());
			}
					sb.append("</td> ");
			if (player.getEventExplain().length() > 0) {
				sb.append("<td> <table id=\"eventdetail\">"
						+ "<tr><th></th><th></th><th>Points</th></tr>");
				for (int i = 0; i < player.getEventExplain().length(); i++) {
					if (i % 2 == 0) {
						sb.append("<tr bgcolor=\"#EEEEEE\">");
					} else {
						sb.append("<tr bgcolor:\"#FFFFFF\">");
					}
					try {
						String eventName = player.getEventExplain()
								.getJSONArray(i).getString(0);
						int eventStat = player.getEventExplain()
								.getJSONArray(i).getInt(1);
						int eventPoints = player.getEventExplain()
								.getJSONArray(i).getInt(2);
						sb.append("<td>" + eventName + "</td><td>" + eventStat
								+ "</td><td>" + eventPoints + "</td></tr>");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				sb.append("</table></td>");
			} else {
				sb.append("<td></td>");
			}
		}
		sb.append("</table>");
		return sb.toString();
	}

	public void setInformation(ResultSet rs) throws SQLException {
		while (rs.next()) {
			ownerId = rs.getInt("owner_id");
			ownerName = rs.getString("owner_name");
			teamName = rs.getString("team_name");
			totalPoints = rs.getInt("team_total_points");
			ownerEmail = rs.getString("owner_email");
		}

	}

	public String createStartingLineupTable() {
		StringBuilder sb = new StringBuilder(
				"<table id=\"playerscoring\">"
						+ "<tr> <th>Name</th> <th>Team</th> <th>Points</th> <th>Detail</th> </tr>");
		for (Player player : getStarters()) {
			sb.append("<tr><td>" + player.getCommonName());
	
			if (player.equals(getActualCaptain())) {
				sb.append(" (c)");
			}
			sb.append("</td> <td>" + player.getTeamName() + "</td> <td>");
			if (player.equals(getActualCaptain())) {
					sb.append(player.getGameweekTotal() * 2);
			} else {
				sb.append(player.getGameweekTotal());
			}
					sb.append("</td> ");
			if (player.getEventExplain().length() > 0) {
				sb.append("<td> <table id=\"eventdetail\">"
						+ "<tr><th></th><th></th><th>Points</th></tr>");
				for (int i = 0; i < player.getEventExplain().length(); i++) {
					try {
						String eventName = player.getEventExplain()
								.getJSONArray(i).getString(0);
						int eventStat = player.getEventExplain()
								.getJSONArray(i).getInt(1);
						int eventPoints = player.getEventExplain()
								.getJSONArray(i).getInt(2);
						sb.append("<tr><td>" + eventName + "</td><td>" + eventStat
								+ "</td><td>" + eventPoints + "</td></tr>");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				sb.append("</table></td>");
			} else {
				sb.append("<td></td>");
			}
		}
		sb.append("</table><!-- end of player score table playerscoring -->");
		return sb.toString();
	}
}
