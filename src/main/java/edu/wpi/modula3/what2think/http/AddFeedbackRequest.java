package edu.wpi.modula3.what2think.http;

import edu.wpi.modula3.what2think.model.Alternative;
import edu.wpi.modula3.what2think.model.User;

public class AddFeedbackRequest {
	User user;
	String content;
	String alternativeId;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAlternativeId() {
		return alternativeId;
	}

	public void String(String alternativeId) {
		this.alternativeId = alternativeId;
	}

	public AddFeedbackRequest(User user, String content, String alternativeId) {
		this.user = user;
		this.content = content;
		this.alternativeId = alternativeId;
	}

	public AddFeedbackRequest() {

	}

	public String toString() {
		String str = "User: ";
		str += user;
		str += "\nContent: ";
		str += content;
		str += "\nAlternativeId: ";
		str += alternativeId;
		str += "\n";
		return str;
	}
}
