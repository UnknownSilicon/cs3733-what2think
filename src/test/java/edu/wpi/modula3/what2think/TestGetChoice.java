package edu.wpi.modula3.what2think;

import edu.wpi.modula3.what2think.http.*;
import edu.wpi.modula3.what2think.model.Alternative;
import edu.wpi.modula3.what2think.model.AlternativeAction;
import edu.wpi.modula3.what2think.model.Choice;
import edu.wpi.modula3.what2think.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestGetChoice extends LambdaTest {

    @Test
    public void testGetUnfinishedChoice() {
        CreateChoice cc = new CreateChoice();
        String[] alts = new String[]{"alt1-1", "alt1-2", "alt1-3"};
        CreateRequest createRequest = new CreateRequest("Test1", 5, alts);
        CreateResponse createResponse = cc.handleRequest(createRequest, createContext("createChoice"));
        Choice oldchoice = createResponse.getChoice();
        String choiceID = oldchoice.getId();
        assertEquals(200, createResponse.getStatusCode());

        RegisterUser ru = new RegisterUser();
        User user = new User("LUCAS", "");
        RegisterRequest rrequest = new RegisterRequest(choiceID, user);
        GenericResponse rresponse = ru.handleRequest(rrequest, createContext("registerUser"));
        assertEquals(200, rresponse.getStatusCode());

        AddApproval aa = new AddApproval();
        AlternativeAction act = new AlternativeAction(user, oldchoice.getAlternatives()[0]);
        VoteRequest vrequest = new VoteRequest(oldchoice.getId(), act);
        GenericResponse vresponse = aa.handleRequest(vrequest, createContext("approve"));
        assertEquals(200, vresponse.getStatusCode());

        AddFeedback af = new AddFeedback();
        String content = "new random content";
        AddFeedbackRequest addFeedbackRequest = new AddFeedbackRequest(user, content, oldchoice.getAlternatives()[0].getId());
        GenericResponse addFeedbackResponse = af.handleRequest(addFeedbackRequest, createContext("addFeedback"));
        assertEquals(200, addFeedbackResponse.getStatusCode());

        GetChoice gc = new GetChoice();
        GetRequest request = new GetRequest(choiceID);
        GetResponse response = gc.handleRequest(request, createContext("getChoice"));

        assertEquals(200, response.getStatusCode());
        assertEquals("", response.getError());
        assertNotNull(response.getChoice());

        Choice choice = response.getChoice();

        assertEquals(choiceID, choice.getId());
        assertEquals(oldchoice.getDescription(), choice.getDescription());
        assertEquals(oldchoice.getMaxUsers(), choice.getMaxUsers());
        assertFalse(choice.isCompleted());
        assertNull(choice.getChosenAlternative());
        assertNull(choice.getCompletionTime());

        Alternative[] alternatives = choice.getAlternatives();
        assertEquals(oldchoice.getAlternatives()[0].getId(), alternatives[0].getId());
        assertEquals(oldchoice.getAlternatives()[1].getId(), alternatives[1].getId());
        assertEquals(oldchoice.getAlternatives()[2].getId(), alternatives[2].getId());

        assertEquals(oldchoice.getAlternatives()[0].getContent(), alternatives[0].getContent());
        assertEquals(oldchoice.getAlternatives()[1].getContent(), alternatives[1].getContent());
        assertEquals(oldchoice.getAlternatives()[2].getContent(), alternatives[2].getContent());

        assertEquals(user.getName(), alternatives[0].getApprovers()[0].getName());

        assertEquals(content, alternatives[0].getFeedback()[0].getContent());
        assertEquals(user.getName(), alternatives[0].getFeedback()[0].getUser().getName());

        User[] users = choice.getUsers();
        assertEquals(user.getName(), users[0].getName());
    }
}
