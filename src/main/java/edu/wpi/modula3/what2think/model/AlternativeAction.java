package edu.wpi.modula3.what2think.model;

public class AlternativeAction {
    User user;
    Alternative alternative;

    public AlternativeAction(User user, Alternative alternative) {
        this.user = user;
        this.alternative = alternative;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Alternative getAlternative() {
        return alternative;
    }

    public void setAlternative(Alternative alternative) {
        this.alternative = alternative;
    }
}
