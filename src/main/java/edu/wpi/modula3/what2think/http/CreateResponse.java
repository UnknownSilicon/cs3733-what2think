package edu.wpi.modula3.what2think.http;

public class CreateResponse {
	public int statusCode;  // HTTP status code.
	public String response;

	public CreateResponse (int statusCode, String response) {
		this.statusCode = statusCode;
		this.response = response;
	}

	public String toString() {
		if (statusCode == 200) {
			return "Success(" + response + ")";
		} else {
			return "Error(" + statusCode + ", err=" + response + ")";
		}
	}
}
