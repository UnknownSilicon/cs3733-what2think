package edu.wpi.modula3.what2think.http;

import edu.wpi.modula3.what2think.model.Feedback;

public class AddFeedbackResponse {
	Feedback feedback;
	int statusCode;
	String error;


	public AddFeedbackResponse(Feedback feedback, int statusCode) {
		this.feedback = feedback;
		this.statusCode = statusCode;
		this.error = "";
	}

	public AddFeedbackResponse(int statusCode, String error) {
		this.statusCode = statusCode;
		this.error = error;
	}

	@Override
	public String toString() {
		if (feedback == null) return "NoFeedback";
		return "Feedback(" + feedback + ")";
	}

	public Feedback getFeedback() {
		return feedback;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getError() {
		return error;
	}
}