package edu.wpi.modula3.what2think;

import edu.wpi.modula3.what2think.http.CreateRequest;
import edu.wpi.modula3.what2think.http.CreateResponse;
import edu.wpi.modula3.what2think.model.Alternative;
import edu.wpi.modula3.what2think.model.Choice;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TestCreateChoice extends LambdaTest {

	@Test
	public void testCreateChoice() {
		CreateChoice cc = new CreateChoice();

		String[] alts = new String[]{"abc", "123", "def"};

		CreateRequest request = new CreateRequest("Test1", 5, alts);

		CreateResponse response = cc.handleRequest(request, createContext("createChoice"));

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
