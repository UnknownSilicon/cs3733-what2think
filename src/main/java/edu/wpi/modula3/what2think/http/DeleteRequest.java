package edu.wpi.modula3.what2think.http;

public class DeleteRequest {
    float days;

    public DeleteRequest(float days) {
        this.days = days;
    }

    public DeleteRequest(){

    }

    @Override
    public String toString() {
        return "DeleteRequest{" + "\n" +
                "days='" + days + '\'' + "\n" +
                '}';
    }

    public float getDays() {
        return days;
    }

    public void setDays(float days) {
        this.days = days;
    }

}
