package edu.wpi.modula3.what2think.http;

import edu.wpi.modula3.what2think.model.SimpleChoice;

public class AdminGetResponse {
    SimpleChoice[] choices;
    int statusCode;
    String error;


    public AdminGetResponse(SimpleChoice[] choices, int statusCode) {
        this.choices = choices;
        this.statusCode = statusCode;
        this.error = "";
    }

    public AdminGetResponse(int statusCode, String error) {
        this.statusCode = statusCode;
        this.error = error;
    }

    @Override
    public String toString() {
        return "GetResponse{" + "\n" +
                ", statusCode=" + statusCode + "\n" +
                ", error='" + error + '\'' + "\n" +
                '}';
    }

    public SimpleChoice[] getChoices() {
        return choices;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }
}
