package edu.wpi.modula3.what2think.http;

public class RegisterResponse {
    int statusCode;
    String error;

    public RegisterResponse(int statusCode, String error) {
        this.statusCode = statusCode;
        this.error = error;
    }

    public RegisterResponse(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "RegisterResponse{" + "\n" +
                "statusCode=" + statusCode + "\n" +
                ", error='" + error + '\'' + "\n" +
                '}';
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }
}
