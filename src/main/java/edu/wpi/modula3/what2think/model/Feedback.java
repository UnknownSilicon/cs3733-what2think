package edu.wpi.modula3.what2think.model;

public class Feedback {
	User user;
	String content;
	String timestamp;
	String alternativeId;

	public Feedback() {
	}

	public Feedback(User user, String content, String timestamp, String alternativeId) {
		this.user = user;
		this.content = content;
		this.timestamp = timestamp;
		this.alternativeId = alternativeId;
	}

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

	public String getAlternativeId() {
		return alternativeId;
	}

	public void setAlternativeId(String alternativeId) {
		this.alternativeId = alternativeId;
	}
}
