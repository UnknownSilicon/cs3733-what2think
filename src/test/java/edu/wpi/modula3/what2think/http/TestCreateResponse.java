package edu.wpi.modula3.what2think.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCreateResponse {

	@Test
	public void testConstruct() {
		CreateResponse response = new CreateResponse(123, "abc");
		assertEquals(123, response.statusCode);
		assertEquals("abc", response.response);
	}
}
