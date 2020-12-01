package edu.wpi.modula3.what2think.http;

import edu.wpi.modula3.what2think.model.AlternativeAction;

public class VoteRequest {
    String id;
    AlternativeAction altAction;

    public VoteRequest(String id, AlternativeAction altAction) {
        this.id = id;
        this.altAction = altAction;
    }

    public VoteRequest(){

    }

    @Override
    public String toString() {
        return "VoteRequest{" + "\n" +
                "id='" + id + '\'' + "\n" +
                ", altAction=" + altAction + "\n" +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AlternativeAction getAltAction() {
        return altAction;
    }

    public void setAltAction(AlternativeAction altAction) {
        this.altAction = altAction;
    }
}
