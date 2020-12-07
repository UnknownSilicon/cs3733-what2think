package edu.wpi.modula3.what2think;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import edu.wpi.modula3.what2think.http.AdminGetRequest;
import edu.wpi.modula3.what2think.http.AdminGetResponse;
import edu.wpi.modula3.what2think.http.GetRequest;
import edu.wpi.modula3.what2think.http.GetResponse;
import edu.wpi.modula3.what2think.model.Choice;
import edu.wpi.modula3.what2think.model.SimpleChoice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TestAdminGet extends LambdaTest{
    @Test
    public void testGetUnfinishedChoice() {
        GetSimpleChoices gc = new GetSimpleChoices();

        AdminGetRequest request = new AdminGetRequest();

        AdminGetResponse response = gc.handleRequest(request, createContext("getChoices"));

        assertEquals(200, response.getStatusCode());

        assertEquals("", response.getError());
        assertNotNull(response.getChoices());

        SimpleChoice[] choices = response.getChoices();
        boolean choiceFound = false;
        for (SimpleChoice s : choices) {
            System.out.println("Choice " + s.getId() + "...");
            if(s.getId().equals("Test1")){
                choiceFound = true;
                assertEquals("Test 1", s.getDescription());
                assertEquals("2020-11-23 20:03:37.947000", s.getDateCreated()); //5 hours before table, copied from TestGetChoice
                assertNull(s.getDateCompleted());
            }
        }
        assertTrue(choiceFound);
    }

    @Test
    public void testGetFinishedChoice() {
        GetSimpleChoices gc = new GetSimpleChoices();

        AdminGetRequest request = new AdminGetRequest();

        AdminGetResponse response = gc.handleRequest(request, createContext("getChoices"));

        assertEquals(200, response.getStatusCode());

        assertEquals("", response.getError());
        assertNotNull(response.getChoices());

        SimpleChoice[] choices = response.getChoices();
        boolean choiceFound = false;
        for(SimpleChoice s : choices){
            if(s.getId().equals("Test2")){
                choiceFound = true;
                assertEquals("Test 2", s.getDescription());
                assertEquals("2020-11-23 17:03:39.947000", s.getDateCreated()); //5 hours before table
                assertEquals("2020-11-23 08:04:39.947000", s.getDateCompleted()); //5 hours before table
            }
        }
        assertTrue(choiceFound);
    }
}
