package edu.wpi.modula3.what2think.model;

public class Alternative {
	String id;
	String content;
	User[] approvers;
	User[] disapprovers;
	User[] voters;
	Feedback[] feedback;

	public Alternative() {
	}

	public Alternative(String id, String content, User[] approvers, User[] disapprovers, User[] voters, Feedback[] feedback) {
		this.id = id;
		this.content = content;
		this.approvers = approvers;
		this.disapprovers = disapprovers;
		this.voters = voters;
		this.feedback = feedback;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User[] getApprovers() {
		return approvers;
	}

	public void setApprovers(User[] approvers) {
		this.approvers = approvers;
	}

	public User[] getDisapprovers() {
		return disapprovers;
	}

	public void setDisapprovers(User[] disapprovers) {
		this.disapprovers = disapprovers;
	}

	public User[] getVoters() {
		return voters;
	}

	public void setVoters(User[] voters) {
		this.voters = voters;
	}

	public Feedback[] getFeedback() {
		return feedback;
	}

	public void setFeedback(Feedback[] feedback) {
		this.feedback = feedback;
	}
}
