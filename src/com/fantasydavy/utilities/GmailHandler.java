package com.fantasydavy.utilities;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.fantasydavy.process.Player;
import com.fantasydavy.process.Team;
import com.fantasydavy.utilities.interfaces.EmailHandler;

public class GmailHandler implements EmailHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fantasydavy.process.EmailHandler#sendEmail(java.lang.String)
	 */
	@Override
	public void sendEmail(String emailText, String ownerEmail) {
		Properties props = System.getProperties();
		props.put("mail.smtp.starttls.enable", true);
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.user", "hedgebrewleague@gmail.com");
		props.put("mail.smtp.password", "16ballynaris");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", true);

		Session session = Session.getDefaultInstance(props, null);

		MimeMessage message = new MimeMessage(session);

		try {
			InternetAddress from = new InternetAddress(
					"hedgebrewleague@gmail.com", "League Admin");
			message.setSubject("Fantasy Football Scoring Update");
			message.setFrom(from);
			message.addRecipients(Message.RecipientType.TO,
					InternetAddress.parse(ownerEmail));

			// Create a multi-part to combine the parts
			Multipart multipart = new MimeMultipart("alternative");

			// Create your text message part
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(emailText);

			// Add the text part to the multipart
			multipart.addBodyPart(messageBodyPart);

			// Create the html part
			messageBodyPart = new MimeBodyPart();
			String htmlMessage = emailText;
			messageBodyPart.setContent(htmlMessage, "text/html");

			// Add html part to multi part
			multipart.addBodyPart(messageBodyPart);

			// Associate multi-part with message
			message.setContent(multipart);

			// Send message
			Transport transport = session.getTransport("smtp");
			transport.connect("smtp.gmail.com", "hedgebrewleague@gmail.com",
					"16ballynaris");
			System.out.println("Sending email to " + ownerEmail);
			transport.sendMessage(message, message.getAllRecipients());

		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fantasydavy.process.EmailHandler#buildEmailPlayerText(com.fantasydavy
	 * .process.Team)
	 */
	@Override
	public String buildEmailPlayerText(Team team) {
		StringBuilder sb = new StringBuilder("<p>Hi " + team.getOwnerName()
				+ ","
				+ "<br>Here is the lineup for " + team.getTeamName()
				+ " this week:</p>");
		sb.append(team.createStartingLineupTableForEmail());

		if (team.getUnplayedStarters().size() > 0) {
			sb.append("<p>Here are the players who have been automatically substituted this week:</p>");
			sb.append("<table id=\"unusedstarters\">");
			for (Player unplayedStarter : team.getUnplayedStarters()) {
				sb.append("<tr><td>" + unplayedStarter.getCommonName()
						+ "</td></tr>");
			}
			sb.append("</table>");
		}
		sb.append("<p>Here are your unused subs:</p>");
		sb.append("<table id=\"unusedsubs\">");
		for (Player unplayedSub : team.getUnplayedSubs()) {
			sb.append("<tr><td>" + unplayedSub.getCommonName() + "</td><td>"
					+ unplayedSub.getGameweekTotal() + "</td></tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}
}