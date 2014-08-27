package com.fantasydavy.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import com.fantasydavy.utilities.interfaces.InputLoader;

public class InputLoaderImpl implements InputLoader {
	String resourcesDirectory = "Resources";
	private String ownerIdsSource = "owners";
	private String ownerInfoSource = "owners_info";
	private String squadsDirectory = "Squads/Ids";

	public ArrayList<Integer> loadSubs(int owner) {
		ArrayList<Integer> playerIds = new ArrayList<Integer>();
		Scanner scanner;
		try {
			scanner = new Scanner(new File(resourcesDirectory + "/Subs/"
					+ owner)).useDelimiter("\n");
			while (scanner.hasNext()) {
				playerIds.add(scanner.nextInt());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Can't find teamsheet file for owner ID: "
					+ owner);
			e.printStackTrace();
		}
		return playerIds;
	}

	public ArrayList<Integer> loadOwnerIds() {
		Scanner scanner;
		ArrayList<Integer> ownerIds = new ArrayList<Integer>();

		try {
			scanner = new Scanner(new File(resourcesDirectory + "/"
					+ ownerIdsSource)).useDelimiter("\n");
			while (scanner.hasNext()) {
				ownerIds.add(scanner.nextInt());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Can't find owner IDs file.");
			e.printStackTrace();
		}
		return ownerIds;
	}

	public ArrayList<String[]> loadOwnerInfo() {
		ArrayList<String[]> ownerInfo = new ArrayList<String[]>();
		try {
			Scanner scanner = new Scanner(new File(resourcesDirectory + "/"
					+ ownerInfoSource)).useDelimiter("\n");
			while (scanner.hasNext()) {
				ownerInfo.add(scanner.next().split(","));
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Can't find owner info file.");
			e.printStackTrace();
		}
		return ownerInfo;
	}

	public ArrayList<Integer> loadSquads(int owner) {
		ArrayList<Integer> squadPlayers = new ArrayList<Integer>();
		try {
			Scanner scanner = new Scanner(new File(resourcesDirectory + "/"
					+ squadsDirectory + "/" + owner)).useDelimiter("\n");
			while (scanner.hasNext()) {
				squadPlayers.add(scanner.nextInt());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return squadPlayers;
	}

	public String[] parseTeamsheet(int ownerId) throws FileNotFoundException {
		String[] teamsheet = new String[18];
		Scanner scanner = new Scanner(new File(resourcesDirectory
				+ "/Teamsheets/" + ownerId)).useDelimiter("\n");
		int i = 0;
		while (scanner.hasNext()) {
			teamsheet[i] = scanner.next();
			i++;
		}
		scanner.close();
		return teamsheet;
	}
}
