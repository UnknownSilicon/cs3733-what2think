package edu.wpi.modula3.what2think.http;

public class GetRequest {
    String ID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public GetRequest(String ID) {
        this.ID = ID;
    }

    public GetRequest() {

    }

    @Override
    public String toString() {
        String str = "ID: ";

        str += ID;
        str += "\n";

        return str;
    }
}
