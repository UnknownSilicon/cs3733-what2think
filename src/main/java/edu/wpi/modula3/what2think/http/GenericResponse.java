package edu.wpi.modula3.what2think.http;

public class GenericResponse {
    //a response to use when you only need a status code

    int statusCode;
    String error;

    public GenericResponse(int statusCode, String error) {
        this.statusCode = statusCode;
        this.error = error;
    }

    public GenericResponse(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "GenericResponse{" + "\n" +
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
