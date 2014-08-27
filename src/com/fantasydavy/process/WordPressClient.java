package com.fantasydavy.process;

import java.net.MalformedURLException;
import java.util.Date;

import net.bican.wordpress.Page;
import net.bican.wordpress.Wordpress;
import redstone.xmlrpc.XmlRpcFault;

public class WordPressClient {

	private static final String URL = "http://petehuey.com/xmlrpc.php";

	void publishStandingsPage(String standings) {
		try {
			Wordpress wp = new Wordpress("stupiddave", "h3dg3br3wl3agu3", URL);
			Page page = wp.getPage(612);
			page.setDateCreated(new Date());
			page.setDescription(standings);
			wp.editPage(612, page, "publish");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (XmlRpcFault e) {
			e.printStackTrace();
		}
	}

	public void publishTeamPage(Team team) {
		try {
			Wordpress wp = new Wordpress("stupiddave", "h3dg3br3wl3agu3", URL);
			Page page = wp.getPage(677);
			page.setDateCreated(new Date());
			StringBuffer sb = new StringBuffer(page.getDescription());
			sb.replace(
					sb.indexOf("<table id=\"playerscoring\">"),
					sb.lastIndexOf("<!-- end of player score table playerscoring -->"),
					team.createStartingLineupTable());
			page.setDescription(sb.toString());
			wp.editPage(677, page, "publish");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (XmlRpcFault e) {
			e.printStackTrace();
		}

	}
}
