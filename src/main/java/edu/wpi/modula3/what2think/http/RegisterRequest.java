package edu.wpi.modula3.what2think.http;

import edu.wpi.modula3.what2think.model.User;

public class RegisterRequest {
    String id;
    User user;

    public RegisterRequest(String id, User user) {
        this.id = id;
        this.user = user;
    }

    public RegisterRequest() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" + "\n" +
                "id='" + id + '\'' + "\n" +
                ", user=" + user + "\n" +
                '}';
    }
}
