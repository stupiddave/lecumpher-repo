package com.fantasydavy.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class RosterLister {

public static void main(String[] args) {
	for(int i = 1; i < 623; i++) {
		BufferedReader reader = null;
		JSONObject jsonObject = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new URL(
					"http://fantasy.premierleague.com/web/api/elements/"
							+ i + "/").openStream()));
		} catch (IOException e) {
			e.printStackTrace();
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
			System.out.println(i + "," + jsonObject.getString("first_name") + "," + jsonObject.getString("second_name") + "," + "'" + jsonObject.getString("web_name") + "'" + "," + jsonObject.getString("team_name"));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		
	}
}
}
