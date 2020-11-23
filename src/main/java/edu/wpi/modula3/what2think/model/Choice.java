package edu.wpi.modula3.what2think.model;

public class Choice {
	String id;
	String description;
	Integer maxUsers;
	Alternative[] alternatives;
	User[] users;
	boolean completed;
	Alternative chosenAlternative;
	String completionTime;
	String creationTime;

	public Choice() {

	}

	public Choice(String id, String description, Integer maxUsers, Alternative[] alternatives, User[] users, boolean completed, Alternative chosenAlternative, String completionTime, String creationTime) {
		this.id = id;
		this.description = description;
		this.maxUsers = maxUsers;
		this.alternatives = alternatives;
		this.users = users;
		this.completed = completed;
		this.chosenAlternative = chosenAlternative;
		this.completionTime = completionTime;
		this.creationTime = creationTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getMaxUsers() {
		return maxUsers;
	}

	public void setMaxUsers(Integer maxUsers) {
		this.maxUsers = maxUsers;
	}

	public Alternative[] getAlternatives() {
		return alternatives;
	}

	public void setAlternatives(Alternative[] alternatives) {
		this.alternatives = alternatives;
	}

	public User[] getUsers() {
		return users;
	}

	public void setUsers(User[] users) {
		this.users = users;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public Alternative getChosenAlternative() {
		return chosenAlternative;
	}

	public void setChosenAlternative(Alternative chosenAlternative) {
		this.chosenAlternative = chosenAlternative;
	}

	public String getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(String completionTime) {
		this.completionTime = completionTime;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}
}
