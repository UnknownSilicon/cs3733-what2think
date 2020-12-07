package edu.wpi.modula3.what2think;

import edu.wpi.modula3.what2think.http.*;
import edu.wpi.modula3.what2think.model.Alternative;
import edu.wpi.modula3.what2think.model.Choice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestDeleteChoices extends LambdaTest {

    @Test
    public void testDeleteChoices() {
        CreateChoice cc = new CreateChoice();
        String[] alts = new String[]{"abc", "123", "def"};
        CreateRequest createRequest = new CreateRequest("Test1", 5, alts);
        CreateResponse createResponse = cc.handleRequest(createRequest, createContext("createChoice"));
        Choice choice = createResponse.getChoice();
        String choiceID = choice.getId();

        assertEquals(200, createResponse.getStatusCode());
        assertEquals("", createResponse.getError());
        assertNotNull(choice);

        DeleteChoices del = new DeleteChoices();
        DeleteRequest deleteRequest = new DeleteRequest((float)0.001);
        GenericResponse genericResponse = del.handleRequest(deleteRequest, createContext("deleteChoice"));

        assertEquals(200, genericResponse.getStatusCode());
    }
}