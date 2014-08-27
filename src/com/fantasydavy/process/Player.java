package com.fantasydavy.process;

import org.json.JSONArray;



public class Player {
	private String firstName;
	private String secondName;
	private String commonName;
	private int playerId;
	private int ownerId;
	private String teamName;
	private String position;
	private int gameweekTotal;
	private JSONArray eventExplain;
	private boolean selected;
	private boolean captain;
	private boolean viceCaptain;
	private int minutesPlayed;
	
	public int getMinutesPlayed() {
		return minutesPlayed;
	}
	public void setMinutesPlayed(int minutesPlayed) {
		this.minutesPlayed = minutesPlayed;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getSecondName() {
		return secondName;
	}
	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}
	public String getCommonName() {
		return commonName;
	}
	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public int getGameweekTotal() {
		return gameweekTotal;
	}
	public void setGameweekTotal(int gameweekTotal) {
		this.gameweekTotal = gameweekTotal;
	}
	public JSONArray getEventExplain() {
		return eventExplain;
	}
	public void setEventExplain(JSONArray eventExplain) {
		this.eventExplain = eventExplain;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public boolean isCaptain() {
		return captain;
	}
	public void setCaptain(boolean captain) {
		this.captain = captain;
	}
	public boolean isViceCaptain() {
		return viceCaptain;
	}
	public void setViceCaptain(boolean viceCaptain) {
		this.viceCaptain = viceCaptain;
	} 

}
