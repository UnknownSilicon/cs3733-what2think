package edu.wpi.modula3.what2think;

import edu.wpi.modula3.what2think.http.*;
import edu.wpi.modula3.what2think.model.Alternative;
import edu.wpi.modula3.what2think.model.AlternativeAction;
import edu.wpi.modula3.what2think.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestVoting extends LambdaTest{

    @Test
    public void validApproval(){
        RegisterUser ru = new RegisterUser();
        CreateChoice cc = new CreateChoice();
        AddApproval aa = new AddApproval();
        RemoveApproval ra = new RemoveApproval();

        User user = new User("testingVotes", "ew");

        String[] alts = new String[]{"voting", "votes", "voted"};
        CreateRequest cRequest = new CreateRequest("TestVote", 5, alts);
        CreateResponse cResponse = cc.handleRequest(cRequest, createContext("createChoice"));

        RegisterRequest rReq = new RegisterRequest(cResponse.getChoice().getId(), user);
        RegisterResponse rResponse = ru.handleRequest(rReq, createContext("registerUser"));

        AlternativeAction act = new AlternativeAction(user, cResponse.getChoice().getAlternatives()[1]);

        VoteRequest req = new VoteRequest(cResponse.getChoice().getId(), act);
        GenericResponse response = aa.handleRequest(req, createContext("approve"));

        assertEquals(200, response.getStatusCode());

        req = new VoteRequest(cResponse.getChoice().getId(), act);
        response = ra.handleRequest(req, createContext("removeApproval"));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testInvalidAddApproval(){
        RegisterUser ru = new RegisterUser();
        CreateChoice cc = new CreateChoice();
        AddApproval aa = new AddApproval();

        User user = new User("testingVotes", "ew");

        String[] alts = new String[]{"voting", "votes", "voted"};
        CreateRequest cRequest = new CreateRequest("TestVote", 5, alts);
        CreateResponse cResponse = cc.handleRequest(cRequest, createContext("createChoice"));

        RegisterRequest rReq = new RegisterRequest(cResponse.getChoice().getId(), user);
        RegisterResponse rResponse = ru.handleRequest(rReq, createContext("registerUser"));

        AlternativeAction act = new AlternativeAction(user, cResponse.getChoice().getAlternatives()[1]);

        VoteRequest req = new VoteRequest(null, act);
        GenericResponse response = aa.handleRequest(req, createContext("approve"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        req = new VoteRequest("", act);
        response = aa.handleRequest(req, createContext("approve"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        req = new VoteRequest(cResponse.getChoice().getId(), null);
        response = aa.handleRequest(req, createContext("approve"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        AlternativeAction badAct = new AlternativeAction(null, cResponse.getChoice().getAlternatives()[1]);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = aa.handleRequest(req, createContext("approve"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        badAct.setUser(user);
        badAct.setAlternative(null);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = aa.handleRequest(req, createContext("approve"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        User badUser = new User(null, "ew");
        badAct.setAlternative(cResponse.getChoice().getAlternatives()[1]);
        badAct.setUser(badUser);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = aa.handleRequest(req, createContext("approve"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        badUser.setName("");
        badAct.setUser(badUser);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = aa.handleRequest(req, createContext("approve"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        badUser.setName("valid");
        badUser.setPassword(null);
        badAct.setUser(badUser);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = aa.handleRequest(req, createContext("approve"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        badAct.setUser(user);
        Alternative badAlt = new Alternative(null, "validContent");
        badAct.setAlternative(badAlt);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = aa.handleRequest(req, createContext("approve"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        badAlt.setId("");
        badAct.setAlternative(badAlt);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = aa.handleRequest(req, createContext("approve"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        req = new VoteRequest("entirelyWrongId-ajksdjhksddhkjak", act);
        response = aa.handleRequest(req, createContext("approve"));

        assertEquals(400, response.getStatusCode());
        assertEquals("No choice with this ID", response.getError());

        badAct.setAlternative(cResponse.getChoice().getAlternatives()[1]);
        badAct.setUser(new User("validButWrong", "meh"));

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = aa.handleRequest(req, createContext("approve"));

        assertEquals(400, response.getStatusCode());
        assertEquals("No user with this name in given choice", response.getError());

        badAct.setAlternative(new Alternative("valid but wrong", "no"));
        badAct.setUser(user);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = aa.handleRequest(req, createContext("approve"));

        assertEquals(400, response.getStatusCode());
        assertEquals("No alternative with this ID in given choice", response.getError());
    }

    @Test
    public void testInvalidRemoveApproval(){
        RegisterUser ru = new RegisterUser();
        CreateChoice cc = new CreateChoice();
        RemoveApproval ra = new RemoveApproval();

        User user = new User("testingVotes", "ew");

        String[] alts = new String[]{"voting", "votes", "voted"};
        CreateRequest cRequest = new CreateRequest("TestVote", 5, alts);
        CreateResponse cResponse = cc.handleRequest(cRequest, createContext("createChoice"));

        RegisterRequest rReq = new RegisterRequest(cResponse.getChoice().getId(), user);
        RegisterResponse rResponse = ru.handleRequest(rReq, createContext("registerUser"));

        AlternativeAction act = new AlternativeAction(user, cResponse.getChoice().getAlternatives()[1]);

        VoteRequest req = new VoteRequest(null, act);
        GenericResponse response = ra.handleRequest(req, createContext("removeApproval"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        req = new VoteRequest("", act);
        response = ra.handleRequest(req, createContext("removeApproval"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        req = new VoteRequest(cResponse.getChoice().getId(), null);
        response = ra.handleRequest(req, createContext("removeApproval"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        AlternativeAction badAct = new AlternativeAction(null, cResponse.getChoice().getAlternatives()[1]);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = ra.handleRequest(req, createContext("removeApproval"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        badAct.setUser(user);
        badAct.setAlternative(null);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = ra.handleRequest(req, createContext("removeApproval"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        User badUser = new User(null, "ew");
        badAct.setAlternative(cResponse.getChoice().getAlternatives()[1]);
        badAct.setUser(badUser);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = ra.handleRequest(req, createContext("removeApproval"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        badUser.setName("");
        badAct.setUser(badUser);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = ra.handleRequest(req, createContext("removeApproval"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        badUser.setName("valid");
        badUser.setPassword(null);
        badAct.setUser(badUser);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = ra.handleRequest(req, createContext("removeApproval"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        badAct.setUser(user);
        Alternative badAlt = new Alternative(null, "validContent");
        badAct.setAlternative(badAlt);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = ra.handleRequest(req, createContext("removeApproval"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        badAlt.setId("");
        badAct.setAlternative(badAlt);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = ra.handleRequest(req, createContext("removeApproval"));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        req = new VoteRequest("entirelyWrongId-ajksdjhksddhkjak", act);
        response = ra.handleRequest(req, createContext("removeApproval"));

        assertEquals(400, response.getStatusCode());
        assertEquals("No choice with this ID", response.getError());

        badAct.setAlternative(cResponse.getChoice().getAlternatives()[1]);
        badAct.setUser(new User("validButWrong", "meh"));

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = ra.handleRequest(req, createContext("removeApproval"));

        assertEquals(400, response.getStatusCode());
        assertEquals("No user with this name in given choice", response.getError());

        badAct.setAlternative(new Alternative("valid but wrong", "no"));
        badAct.setUser(user);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = ra.handleRequest(req, createContext("removeApproval"));

        assertEquals(400, response.getStatusCode());
        assertEquals("No alternative with this ID in given choice", response.getError());

        req = new VoteRequest(cResponse.getChoice().getId(), act);
        response = ra.handleRequest(req, createContext("removeApproval"));

        assertEquals(400, response.getStatusCode());
        assertEquals("Approval does not exist", response.getError());
    }
}
