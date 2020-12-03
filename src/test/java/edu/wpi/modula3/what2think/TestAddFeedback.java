package edu.wpi.modula3.what2think;

import edu.wpi.modula3.what2think.http.*;
import edu.wpi.modula3.what2think.model.Alternative;
import edu.wpi.modula3.what2think.model.Choice;
import edu.wpi.modula3.what2think.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAddFeedback extends LambdaTest {

    @Test
    public void testAddFeedback() {
        CreateChoice cc = new CreateChoice();
        String[] alts = new String[]{"abc", "123", "def"};
        CreateRequest createRequest = new CreateRequest("Test1", 5, alts);
        CreateResponse createResponse = cc.handleRequest(createRequest, createContext("createChoice"));
        Choice startingChoice = createResponse.getChoice();
        String choiceID = startingChoice.getId();

        assertEquals(200, createResponse.getStatusCode());
        assertNotNull(startingChoice);

        RegisterUser ru = new RegisterUser();
        User user = new User("test", "ew");
        RegisterRequest registerRequest = new RegisterRequest(choiceID, user);
        GenericResponse registerResponse = ru.handleRequest(registerRequest, createContext("registerUser"));

        assertEquals(200, registerResponse.getStatusCode());

        AddFeedback af = new AddFeedback();
        String content = "new random content";
        AddFeedbackRequest addFeedbackRequest = new AddFeedbackRequest(user, content, startingChoice.getAlternatives()[0].getId());
        GenericResponse addFeedbackResponse = af.handleRequest(addFeedbackRequest, createContext("addFeedback"));

        assertEquals(200, addFeedbackResponse.getStatusCode());

        AddFeedback af2 = new AddFeedback();
        String content2 = "second random feedback";
        AddFeedbackRequest addFeedbackRequest2 = new AddFeedbackRequest(user, content2, startingChoice.getAlternatives()[1].getId());
        GenericResponse addFeedbackResponse2 = af2.handleRequest(addFeedbackRequest2, createContext("addFeedback"));

        assertEquals(200, addFeedbackResponse2.getStatusCode());

        GetChoice gc = new GetChoice();
        GetRequest getRequest = new GetRequest(choiceID);
        GetResponse getResponse = gc.handleRequest(getRequest, createContext("getChoice"));
        Choice choice = getResponse.getChoice();

        assertEquals(200, getResponse.getStatusCode());
        assertNotNull(choice);

        Alternative[] alternatives = choice.getAlternatives();
        for (int i = 0; i < 3; i++) {
            if (alternatives[i].getContent().equals("abc")) {
                assertEquals(alternatives[i].getFeedback()[0].getContent(), content);
                assertEquals(alternatives[i].getFeedback()[0].getUser().getName(), user.getName());
                assertEquals(alternatives[i].getFeedback()[0].getAlternativeId(), startingChoice.getAlternatives()[0].getId());
            }
            if (alternatives[i].getContent().equals("123")) {
                assertEquals(alternatives[i].getFeedback()[0].getContent(), content2);
                assertEquals(alternatives[i].getFeedback()[0].getUser().getName(), user.getName());
                assertEquals(alternatives[i].getFeedback()[0].getAlternativeId(), startingChoice.getAlternatives()[1].getId());
            }
        }
    }
}