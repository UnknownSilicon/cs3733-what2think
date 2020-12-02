package edu.wpi.modula3.what2think.http;

import edu.wpi.modula3.what2think.model.Alternative;
import edu.wpi.modula3.what2think.model.AlternativeAction;

public class CompleteRequest {
    String choiceId;
    Alternative alternative;

    public CompleteRequest(String choiceId, Alternative alternative) {
        this.choiceId = choiceId;
        this.alternative = alternative;
    }

    public CompleteRequest(){

    }

    @Override
    public String toString() {
        return "VoteRequest{" + "\n" +
                "id='" + choiceId + '\'' + "\n" +
                ", alternative=" + alternative + "\n" +
                '}';
    }

    public String getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(String choiceId) {
        this.choiceId = choiceId;
    }

    public Alternative getAlternative() {
        return alternative;
    }

    public void setAlternative(Alternative alternative) {
        this.alternative = alternative;
    }
}
