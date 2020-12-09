package edu.wpi.modula3.what2think;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import edu.wpi.modula3.what2think.db.DAO;
import edu.wpi.modula3.what2think.http.*;
import edu.wpi.modula3.what2think.model.Alternative;
import edu.wpi.modula3.what2think.model.Choice;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TestDeleteChoices extends LambdaTest {



    boolean createOldChoice(Choice choice, float daysOld)  {
        Context context = createContext("none?");
        DAO dao = new DAO(context.getLogger());
        try {
            return dao.addOldChoice(choice, daysOld);
        }
        catch (Exception e) {
            return false;
        }
    }

    @Test
    public void testDeleteChoices() {
        CreateChoice cc = new CreateChoice();
        String[] alts = new String[]{"alt1-1", "alt1-2", "alt1-3"};
        CreateRequest createRequest = new CreateRequest("Test1", 5, alts);
        CreateResponse createResponse = cc.handleRequest(createRequest, createContext("createChoice"));
        Choice choice = createResponse.getChoice();
        String choiceID = choice.getId();
        assertEquals(200, createResponse.getStatusCode());

        String newId = UUID.randomUUID().toString();
        choice.setId(newId);

        assertTrue(createOldChoice(choice, (float) 1000.001));

        GetChoice gc = new GetChoice();
        GetRequest request = new GetRequest(newId);
        GetResponse response = gc.handleRequest(request, createContext("getChoice"));
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getChoice());

        DeleteChoices del = new DeleteChoices();
        DeleteRequest deleteRequest = new DeleteRequest((float)1000);
        GenericResponse genericResponse = del.handleRequest(deleteRequest, createContext("deleteChoice"));
        assertEquals(200, genericResponse.getStatusCode());

        GetChoice gc2 = new GetChoice();
        GetRequest request2 = new GetRequest(newId);
        GetResponse response2 = gc.handleRequest(request2, createContext("getChoice"));
        assertEquals(400, response2.getStatusCode());
    }

    @Test
    public void testNotDeleteChoices() {
        CreateChoice cc = new CreateChoice();
        String[] alts = new String[]{"alt1-1", "alt1-2", "alt1-3"};
        CreateRequest createRequest = new CreateRequest("Test1", 5, alts);
        CreateResponse createResponse = cc.handleRequest(createRequest, createContext("createChoice"));
        Choice choice = createResponse.getChoice();
        String choiceID = choice.getId();
        assertEquals(200, createResponse.getStatusCode());

        String newId = UUID.randomUUID().toString();
        choice.setId(newId);

        assertTrue(createOldChoice(choice, 99));

        GetChoice gc = new GetChoice();
        GetRequest request = new GetRequest(newId);
        GetResponse response = gc.handleRequest(request, createContext("getChoice"));
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getChoice());

        DeleteChoices del = new DeleteChoices();
        DeleteRequest deleteRequest = new DeleteRequest((float)100);
        GenericResponse genericResponse = del.handleRequest(deleteRequest, createContext("deleteChoice"));
        assertEquals(200, genericResponse.getStatusCode());

        GetChoice gc2 = new GetChoice();
        GetRequest request2 = new GetRequest(newId);
        GetResponse response2 = gc.handleRequest(request2, createContext("getChoice"));
        assertEquals(200, response2.getStatusCode());
    }
}