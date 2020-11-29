package edu.wpi.modula3.what2think.http;

import edu.wpi.modula3.what2think.model.Alternative;
import edu.wpi.modula3.what2think.model.User;

public class CreateFeedbackRequest {
	User user;
	String content;
	String timestamp;
	Alternative alternative;

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

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public Alternative getAlternative() {
		return alternative;
	}

	public void setAlternative(Alternative alternative) {
		this.alternative = alternative;
	}

	public CreateFeedbackRequest(User user, String content, String timestamp, Alternative alternative) {
		this.user = user;
		this.content = content;
		this.timestamp = timestamp;
	}

	public CreateFeedbackRequest() {

	}

	public String toString() {
		String str = "User: ";
		str += user;
		str += "\nContent: ";
		str += content;
		str += "\nTimestamp: ";
		str += timestamp;
		str += "\n";
		return str;
	}
}
