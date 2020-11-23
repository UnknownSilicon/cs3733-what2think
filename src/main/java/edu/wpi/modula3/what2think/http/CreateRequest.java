package edu.wpi.modula3.what2think.http;

public class CreateRequest {
	String description;
	Integer maxUsers;
	String[] alternatives;

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

	public String[] getAlternatives() {
		return alternatives;
	}

	public void setAlternatives(String[] alternatives) {
		this.alternatives = alternatives;
	}

	public CreateRequest(String description, Integer maxUsers, String[] alternatives) {
		this.description = description;
		this.maxUsers = maxUsers;
		this.alternatives = alternatives;
	}

	public CreateRequest() {

	}

	public String toString() {
		String str = "Description: ";

		str += description;
		str += "\nMaxUsers: ";
		str += maxUsers;
		str += "\nAlternatives: ";

		for (String s : alternatives) {
			str += s;
			str += "\n";
		}

		return str;
	}
}
