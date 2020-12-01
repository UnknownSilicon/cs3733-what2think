package edu.wpi.modula3.what2think;

import com.amazonaws.services.lambda.runtime.RequestHandler;
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
    public void validDisapproval(){
        RegisterUser ru = new RegisterUser();
        CreateChoice cc = new CreateChoice();
        AddDisapproval ad = new AddDisapproval();
        RemoveDisapproval rd = new RemoveDisapproval();

        User user = new User("testingVotes", "ew");

        String[] alts = new String[]{"voting", "votes", "voted"};
        CreateRequest cRequest = new CreateRequest("TestVote", 5, alts);
        CreateResponse cResponse = cc.handleRequest(cRequest, createContext("createChoice"));

        RegisterRequest rReq = new RegisterRequest(cResponse.getChoice().getId(), user);
        RegisterResponse rResponse = ru.handleRequest(rReq, createContext("registerUser"));

        AlternativeAction act = new AlternativeAction(user, cResponse.getChoice().getAlternatives()[1]);

        VoteRequest req = new VoteRequest(cResponse.getChoice().getId(), act);
        GenericResponse response = ad.handleRequest(req, createContext("disapprove"));

        assertEquals(200, response.getStatusCode());

        req = new VoteRequest(cResponse.getChoice().getId(), act);
        response = rd.handleRequest(req, createContext("removeDisapproval"));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void swapVotes(){
        RegisterUser ru = new RegisterUser();
        CreateChoice cc = new CreateChoice();
        AddApproval aa = new AddApproval();
        AddDisapproval ad = new AddDisapproval();
        RemoveApproval ra = new RemoveApproval();
        RemoveDisapproval rd = new RemoveDisapproval();

        User user = new User("testingVotes", "ew");

        String[] alts = new String[]{"voting", "votes", "voted"};
        CreateRequest cRequest = new CreateRequest("TestVote", 5, alts);
        CreateResponse cResponse = cc.handleRequest(cRequest, createContext("createChoice"));

        RegisterRequest rReq = new RegisterRequest(cResponse.getChoice().getId(), user);
        RegisterResponse rResponse = ru.handleRequest(rReq, createContext("registerUser"));

        AlternativeAction act = new AlternativeAction(user, cResponse.getChoice().getAlternatives()[1]);

        VoteRequest req = new VoteRequest(cResponse.getChoice().getId(), act);
        GenericResponse response = ad.handleRequest(req, createContext("disapprove"));

        assertEquals(200, response.getStatusCode());

        req = new VoteRequest(cResponse.getChoice().getId(), act);
        response = aa.handleRequest(req, createContext("approve"));

        assertEquals(200, response.getStatusCode());

        req = new VoteRequest(cResponse.getChoice().getId(), act);
        response = rd.handleRequest(req, createContext("removeDisapproval"));

        assertEquals(400, response.getStatusCode());
        assertEquals("Disapproval does not exist", response.getError());

        req = new VoteRequest(cResponse.getChoice().getId(), act);
        response = ad.handleRequest(req, createContext("disapprove"));

        assertEquals(200, response.getStatusCode());

        req = new VoteRequest(cResponse.getChoice().getId(), act);
        response = ra.handleRequest(req, createContext("removeApproval"));

        assertEquals(400, response.getStatusCode());
        assertEquals("Approval does not exist", response.getError());

        req = new VoteRequest(cResponse.getChoice().getId(), act);
        response = rd.handleRequest(req, createContext("removeDisapproval"));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testInvalidAddApproval(){
        testInvalid(new AddApproval(), "approve");
    }

    @Test
    public void testInvalidAddDispproval(){
        testInvalid(new AddDisapproval(), "disapprove");
    }

    @Test
    public void testInvalidRemoveApproval(){
        testInvalid(new RemoveApproval(), "removeApproval");
    }

    @Test
    public void testInvalidRemoveDisapproval(){
        testInvalid(new RemoveDisapproval(), "removeDisapproval");
    }

    public void testInvalid(RequestHandler<VoteRequest, GenericResponse> handler, String apiCall){
        RegisterUser ru = new RegisterUser();
        CreateChoice cc = new CreateChoice();

        User user = new User("testingVotes", "ew");

        String[] alts = new String[]{"voting", "votes", "voted"};
        CreateRequest cRequest = new CreateRequest("TestVote", 5, alts);
        CreateResponse cResponse = cc.handleRequest(cRequest, createContext("createChoice"));

        RegisterRequest rReq = new RegisterRequest(cResponse.getChoice().getId(), user);
        RegisterResponse rResponse = ru.handleRequest(rReq, createContext("registerUser"));

        AlternativeAction act = new AlternativeAction(user, cResponse.getChoice().getAlternatives()[1]);

        VoteRequest req = new VoteRequest(null, act);
        GenericResponse response = handler.handleRequest(req, createContext(apiCall));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        req = new VoteRequest("", act);
        response = handler.handleRequest(req, createContext(apiCall));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        req = new VoteRequest(cResponse.getChoice().getId(), null);
        response = handler.handleRequest(req, createContext(apiCall));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        AlternativeAction badAct = new AlternativeAction(null, cResponse.getChoice().getAlternatives()[1]);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = handler.handleRequest(req, createContext(apiCall));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        badAct.setUser(user);
        badAct.setAlternative(null);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = handler.handleRequest(req, createContext(apiCall));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        User badUser = new User(null, "ew");
        badAct.setAlternative(cResponse.getChoice().getAlternatives()[1]);
        badAct.setUser(badUser);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = handler.handleRequest(req, createContext(apiCall));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        badUser.setName("");
        badAct.setUser(badUser);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = handler.handleRequest(req, createContext(apiCall));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        badUser.setName("valid");
        badUser.setPassword(null);
        badAct.setUser(badUser);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = handler.handleRequest(req, createContext(apiCall));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        badAct.setUser(user);
        Alternative badAlt = new Alternative(null, "validContent");
        badAct.setAlternative(badAlt);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = handler.handleRequest(req, createContext(apiCall));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        badAlt.setId("");
        badAct.setAlternative(badAlt);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = handler.handleRequest(req, createContext(apiCall));

        assertEquals(400, response.getStatusCode());
        assertEquals("invalid input", response.getError());

        req = new VoteRequest("entirelyWrongId-ajksdjhksddhkjak", act);
        response = handler.handleRequest(req, createContext(apiCall));

        assertEquals(400, response.getStatusCode());
        assertEquals("No choice with this ID", response.getError());

        badAct.setAlternative(cResponse.getChoice().getAlternatives()[1]);
        badAct.setUser(new User("validButWrong", "meh"));

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = handler.handleRequest(req, createContext(apiCall));

        assertEquals(400, response.getStatusCode());
        assertEquals("No user with this name in given choice", response.getError());

        badAct.setAlternative(new Alternative("valid but wrong", "no"));
        badAct.setUser(user);

        req = new VoteRequest(cResponse.getChoice().getId(), badAct);
        response = handler.handleRequest(req, createContext(apiCall));

        assertEquals(400, response.getStatusCode());
        assertEquals("No alternative with this ID in given choice", response.getError());
    }

    @Test
    public void testMissing(){
        RegisterUser ru = new RegisterUser();
        CreateChoice cc = new CreateChoice();
        RemoveApproval ra = new RemoveApproval();
        RemoveDisapproval rd = new RemoveDisapproval();

        User user = new User("testingVotes", "ew");

        String[] alts = new String[]{"voting", "votes", "voted"};
        CreateRequest cRequest = new CreateRequest("TestVote", 5, alts);
        CreateResponse cResponse = cc.handleRequest(cRequest, createContext("createChoice"));

        RegisterRequest rReq = new RegisterRequest(cResponse.getChoice().getId(), user);
        RegisterResponse rResponse = ru.handleRequest(rReq, createContext("registerUser"));

        AlternativeAction act = new AlternativeAction(user, cResponse.getChoice().getAlternatives()[1]);

        VoteRequest req = new VoteRequest(cResponse.getChoice().getId(), act);
        GenericResponse response = ra.handleRequest(req, createContext("disapprove"));

        assertEquals(400, response.getStatusCode());
        assertEquals("Approval does not exist", response.getError());

        req = new VoteRequest(cResponse.getChoice().getId(), act);
        response = rd.handleRequest(req, createContext("removeDisapproval"));

        assertEquals(400, response.getStatusCode());
        assertEquals("Disapproval does not exist", response.getError());
    }
}
