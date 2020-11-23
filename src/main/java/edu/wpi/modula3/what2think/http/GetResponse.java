package edu.wpi.modula3.what2think.http;

import edu.wpi.modula3.what2think.model.Choice;

public class GetResponse {
    Choice choice;
    int statusCode;
    String error;


    public GetResponse(Choice choice, int statusCode) {
        this.choice = choice;
        this.statusCode = statusCode;
        this.error = "";
    }

    public GetResponse(int statusCode, String error) {
        this.statusCode = statusCode;
        this.error = error;
    }

    @Override
    public String toString() {
        return "GetResponse{" + "\n" +
                "Choice ID=" + choice.getId() + "\n" +
                ", statusCode=" + statusCode + "\n" +
                ", error='" + error + '\'' + "\n" +
                '}';
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
