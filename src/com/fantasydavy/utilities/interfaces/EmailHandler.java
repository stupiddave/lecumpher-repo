package com.fantasydavy.utilities.interfaces;

import com.fantasydavy.process.Team;

public interface EmailHandler {

	public abstract void sendEmail(String emailText, String ownerEmail);

	public abstract String buildEmailPlayerText(Team team);

}