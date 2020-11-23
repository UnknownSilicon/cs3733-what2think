package edu.wpi.modula3.what2think;

import edu.wpi.modula3.what2think.http.GetRequest;
import edu.wpi.modula3.what2think.http.GetResponse;
import edu.wpi.modula3.what2think.model.Alternative;
import edu.wpi.modula3.what2think.model.Choice;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TestGetChoice extends LambdaTest {

    @Test
    public void testGetChoice() {
        GetChoice gc = new GetChoice();

        String[] alts = new String[]{"abc", "123", "def"};

        GetRequest request = new GetRequest("012345678901234567890123456789123456");

        GetResponse response = gc.handleRequest(request, createContext("getChoice"));

        assertEquals(200, response.getStatusCode());
        assertEquals("", response.getError());
        assertNotNull(response.getChoice());

        Choice choice = response.getChoice();

        try {
            UUID.fromString(choice.getId());
        } catch (IllegalArgumentException e) {
            fail();
        }

        assertEquals("Test1", choice.getDescription());
        assertEquals(5, choice.getMaxUsers().intValue());

        for (Alternative a : choice.getAlternatives()) {
            try {
                UUID.fromString(a.getId());
            } catch (IllegalArgumentException e) {
                fail();
            }

            if (Arrays.stream(alts).noneMatch(x -> x.equals(a.getContent()))) {
                fail();
            }
        }
    }
}
