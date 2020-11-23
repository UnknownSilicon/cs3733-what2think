package edu.wpi.modula3.what2think;

import edu.wpi.modula3.what2think.http.GetRequest;
import edu.wpi.modula3.what2think.http.GetResponse;
import edu.wpi.modula3.what2think.model.Alternative;
import edu.wpi.modula3.what2think.model.Choice;
import edu.wpi.modula3.what2think.model.User;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TestGetChoice extends LambdaTest {

    @Test
    public void testGetUnfinishedChoice() {
        GetChoice gc = new GetChoice();

        GetRequest request = new GetRequest("Test1");

        GetResponse response = gc.handleRequest(request, createContext("getChoice"));

        assertEquals(200, response.getStatusCode());

        assertEquals("", response.getError());
        assertNotNull(response.getChoice());

        Choice choice = response.getChoice();

        assertEquals("Test1", choice.getId());
        assertEquals("Test 1", choice.getDescription());
        assertEquals(1, choice.getMaxUsers().intValue());
        assertFalse(choice.isCompleted());
        assertNull(choice.getChosenAlternative());
        assertNull(choice.getCompletionTime());
        assertEquals("2020-11-23 15:03:37.947", choice.getCreationTime()); //5 hours before table

        User[] users = choice.getUsers();
        assertEquals("LUCAS", users[0].getName());

        Alternative[] alternatives = choice.getAlternatives();
        assertEquals("alt1-1", alternatives[0].getId());
        assertEquals("alt1-2", alternatives[1].getId());
        assertEquals("alt1-3", alternatives[2].getId());

        assertEquals("alt 1-1", alternatives[0].getContent());
        assertEquals("alt 1-2", alternatives[1].getContent());
        assertEquals("alt 1-3", alternatives[2].getContent());

        assertEquals("LUCAS", alternatives[0].getDisapprovers()[0].getName());
        assertEquals("LUCAS", alternatives[1].getApprovers()[0].getName());
        assertNull(alternatives[2].getApprovers());
        assertNull(alternatives[2].getDisapprovers());

        assertEquals("none", alternatives[0].getFeedback()[0].getContent());
        assertEquals("LUCAS", alternatives[0].getFeedback()[0].getUser().getName());
        assertEquals("2020-11-23 05:03:37.947", alternatives[0].getFeedback()[0].getTimestamp());
        assertNull(alternatives[1].getFeedback());
    }

    @Test
    public void testGetFinishedChoice() {
        GetChoice gc = new GetChoice();

        GetRequest request = new GetRequest("Test2");

        GetResponse response = gc.handleRequest(request, createContext("getChoice"));

        assertEquals(200, response.getStatusCode());

        assertEquals("", response.getError());
        assertNotNull(response.getChoice());

        Choice choice = response.getChoice();

        assertEquals("Test2", choice.getId());
        assertEquals("Test 2", choice.getDescription());
        assertEquals(2, choice.getMaxUsers().intValue());
        assertTrue(choice.isCompleted());
        assertEquals("alt 2-1", choice.getChosenAlternative().getContent());
        assertEquals("2020-11-23 12:03:39.947", choice.getCreationTime()); //5 hours before table
        assertEquals("2020-11-23 03:04:39.947", choice.getCompletionTime()); //5 hours before table
    }
}
