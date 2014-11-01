package com.fantasydavy.process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.fantasydavy.utilities.GmailHandler;
import com.fantasydavy.utilities.interfaces.EmailHandler;

public class Controller {

	public static void main(String[] args) {
		if ("dumpPlayerNamesToIds".equals(args[0])) {
			dumpPlayerNamesToIds();
		} else if ("dumpCleanPlayerNamesToIds".equals(args[0])) {
			dumpCleanPlayerNamesToIds();
		} else if ("setupDropbox".equals(args[0])) {

		} else if ("doWeeklyScoring".equals(args[0])) {
			boolean isFinal = false;
			Processor processor = new Processor();
			processor.doWeeklyScoring();
			
			if (args.length > 1 && "final".equals(args[1])) {
				isFinal = true;
				System.out.println("This is the finalised scoring.");
				processor.finaliseWeeklyScores();
			}
			processor.createStandingsTable();
			processor.sendWeeklyTeamEmail(isFinal);
//			processor.updateTeamWebPage();
			processor.publishTableToWeb();
			processor.uploadInfoToDropbox();
		} else if ("testEmail".equals(args[0])) {
			EmailHandler emailHandler = new GmailHandler();
			emailHandler.sendEmail("Fantasy test yo", null);
		} else if ("resetTeamScores".equals(args[0])) {
			DAO dao = new DAO();
			dao.resetTeamScores();
		}
	}

	private static void dumpCleanPlayerNamesToIds() {
		for (int i = 1; i < 9; i++) {
			Scanner scanner = null;
			try {
				scanner = new Scanner(new File("Resources/Squads/Ids/" + i))
						.useDelimiter("\n");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			System.out.println("Squad for owner id " + i);
			while (scanner.hasNext()) {
				int playerId = scanner.nextInt();
				JSONObject playerJson = Processor.createJsonObject(playerId);
				try {
					System.out.println(playerJson.getString("first_name") + ","
							+ playerJson.getString("second_name") + ","
							+ playerJson.getString("web_name") + ","
							+ playerJson.getString("team_name") + ","
							+ playerJson.getInt("id"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void dumpPlayerNamesToIds() {
		ArrayList<String> listOfCleanNames = new ArrayList<String>();
		HashMap<String, Integer> nameIdMap = new HashMap<String, Integer>();
		for (int i = 1; i < 579; i++) {
			JSONObject playerJSON = Processor.createJsonObject(i);
			System.out.println("fetched player " + i);
			try {
				nameIdMap.put(playerJSON.getString("web_name"),
						playerJSON.getInt("id"));
				listOfCleanNames.add(playerJSON.getString("web_name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		PrintWriter badNameWriter;
		try {
			badNameWriter = new PrintWriter("Resources/Squads/Names/bad_names",
					"UTF-8");
			for (int i = 1; i < 9; i++) {
				try {
					Scanner scanner = new Scanner(new File(
							"Resources/Squads/Names/" + i)).useDelimiter("\n");
					PrintWriter idWriter = new PrintWriter(
							"Resources/Squads/Ids/" + i, "UTF-8");
					while (scanner.hasNext()) {
						String thisPlayerName = scanner.next();
						System.out.println(thisPlayerName + ","
								+ nameIdMap.get(thisPlayerName));
						if (null == nameIdMap.get(thisPlayerName)) {
							boolean isMatched = false;
							for (String cleanName : listOfCleanNames) {
								if (StringUtils.getLevenshteinDistance(
										thisPlayerName, cleanName) < 3) {
									idWriter.println(nameIdMap.get(cleanName));
									isMatched = true;
								}
							}
							if (isMatched == false) {
								badNameWriter.println(thisPlayerName);
							}
						} else {
							idWriter.println(nameIdMap.get(thisPlayerName));
						}
					}
					idWriter.close();
					scanner.close();
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			badNameWriter.close();

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}
}
