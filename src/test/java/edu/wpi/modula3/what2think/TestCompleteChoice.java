package edu.wpi.modula3.what2think;

import edu.wpi.modula3.what2think.http.*;
import edu.wpi.modula3.what2think.model.Alternative;
import edu.wpi.modula3.what2think.model.Choice;
import edu.wpi.modula3.what2think.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCompleteChoice extends LambdaTest {

    @Test
    public void testCompleteChoice() {
        CreateChoice cc = new CreateChoice();
        String[] alts = new String[]{"abc", "123", "def"};
        CreateRequest createRequest = new CreateRequest("Test1", 5, alts);
        CreateResponse createResponse = cc.handleRequest(createRequest, createContext("createChoice"));
        Choice choice = createResponse.getChoice();
        String choiceID = choice.getId();

        assertEquals(200, createResponse.getStatusCode());
        assertEquals("", createResponse.getError());
        assertNotNull(choice);

        CompleteChoice compc = new CompleteChoice();
        CompleteRequest completeRequest = new CompleteRequest(choiceID, choice.getAlternatives()[0]);
        GenericResponse genericResponse = compc.handleRequest(completeRequest, createContext("completeChoice"));

        assertEquals(200, genericResponse.getStatusCode());

        GetChoice gc = new GetChoice();
        GetRequest getRequest = new GetRequest(choiceID);
        GetResponse getResponse = gc.handleRequest(getRequest, createContext("getChoice"));
        Choice newChoice = getResponse.getChoice();

        assertEquals(200, getResponse.getStatusCode());
        assertEquals("", getResponse.getError());
        assertNotNull(newChoice);

        assertNotNull(newChoice.getCompletionTime());
        assertEquals(choice.getAlternatives()[0].getId(), newChoice.getChosenAlternative().getId());
    }

    @Test
    public void testBadChoice() {
        CreateChoice cc = new CreateChoice();
        String[] alts = new String[]{"abc", "123", "def"};
        CreateRequest createRequest = new CreateRequest("Test1", 5, alts);
        CreateResponse createResponse = cc.handleRequest(createRequest, createContext("createChoice"));
        Choice choice = createResponse.getChoice();
        String choiceID = choice.getId();

        assertEquals(200, createResponse.getStatusCode());
        assertEquals("", createResponse.getError());
        assertNotNull(choice);

        Alternative newAlternative = new Alternative();
        newAlternative.setId("fake");
        CompleteChoice compc = new CompleteChoice();
        CompleteRequest completeRequest = new CompleteRequest(choiceID, newAlternative);
        GenericResponse genericResponse = compc.handleRequest(completeRequest, createContext("completeChoice"));

        assertEquals(400, genericResponse.getStatusCode());
    }
}