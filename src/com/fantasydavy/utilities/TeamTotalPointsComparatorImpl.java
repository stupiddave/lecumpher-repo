package com.fantasydavy.utilities;

import java.util.Comparator;

import com.fantasydavy.process.Team;
import com.fantasydavy.utilities.interfaces.TeamTotalPointsComparator;

public class TeamTotalPointsComparatorImpl implements Comparator<Team>, TeamTotalPointsComparator {

	/* (non-Javadoc)
	 * @see com.fantasydavy.utilities.TeamTotalPointsComparator#compare(com.fantasydavy.process.Team, com.fantasydavy.process.Team)
	 */
	@Override
	public int compare(Team team1, Team team2) {
		return team2.getTotalPoints() - team1.getTotalPoints();
	}

}
