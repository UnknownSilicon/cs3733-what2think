package edu.wpi.modula3.what2think.http;

public class GetRequest {
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GetRequest(String id) {
        this.id = id;
    }

    public GetRequest() {

    }

    @Override
    public String toString() {
        String str = "ID: ";

        str += id;
        str += "\n";

        return str;
    }
}
