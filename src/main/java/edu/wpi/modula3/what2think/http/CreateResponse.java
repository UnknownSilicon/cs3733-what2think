package edu.wpi.modula3.what2think.http;

import edu.wpi.modula3.what2think.model.Choice;

public class CreateResponse {
	Choice choice;
	int statusCode;
	String error;


	public CreateResponse(Choice choice, int statusCode) {
		this.choice = choice;
		this.statusCode = statusCode;
		this.error = "";
	}

	public CreateResponse(int statusCode, String error) {
		this.statusCode = statusCode;
		this.error = error;
	}

	@Override
	public String toString() {
		if (choice == null) return "NoChoice";
		return "Choice(" + choice + ")";
	}

	public Choice getChoice() {
		return choice;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getError() {
		return error;
	}
}
