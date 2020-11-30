package edu.wpi.modula3.what2think;

import edu.wpi.modula3.what2think.http.*;
import edu.wpi.modula3.what2think.model.Alternative;
import edu.wpi.modula3.what2think.model.Choice;
import edu.wpi.modula3.what2think.model.Feedback;
import edu.wpi.modula3.what2think.model.User;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAddFeedback extends LambdaTest {

    @Test
    public void testAddFeedback() {
        CreateChoice cc = new CreateChoice();
        String[] alts = new String[]{"abc", "123", "def"};
        CreateRequest createRequest = new CreateRequest("Test1", 5, alts);
        CreateResponse createResponse = cc.handleRequest(createRequest, createContext("createChoice"));
        String choiceID = createResponse.getChoice().getId();

        assertEquals(200, createResponse.getStatusCode());
        assertEquals("", createResponse.getError());
        assertNotNull(createResponse.getChoice());

        RegisterUser ru = new RegisterUser();
        User user = new User("test", "ew");
        RegisterRequest registerRequest = new RegisterRequest(choiceID, user);
        RegisterResponse registerResponse = ru.handleRequest(registerRequest, createContext("registerUser"));

        assertEquals(200, registerResponse.getStatusCode());

        AddFeedback af = new AddFeedback();
        String content = "random content";
        AddFeedbackRequest addFeedbackRequest = new AddFeedbackRequest(user, content, createResponse.getChoice().getAlternatives()[0].getId());
        AddFeedbackResponse addFeedbackResponse = af.handleRequest(addFeedbackRequest, createContext("addFeedback"));

        assertEquals(200, addFeedbackResponse.getStatusCode());
        assertEquals("", addFeedbackResponse.getError());

        GetChoice gc = new GetChoice();
        GetRequest getRequest = new GetRequest(choiceID);
        GetResponse getResponse = gc.handleRequest(getRequest, createContext("getChoice"));
        Choice choice = getResponse.getChoice();

        assertEquals(200, getResponse.getStatusCode());
        assertEquals("", getResponse.getError());
        assertNotNull(choice);

        Feedback feedback1 = choice.getAlternatives()[0].getFeedback()[0];

        System.out.println(feedback1.getContent());
        assertEquals(feedback1.getContent(), content);
    }
}
