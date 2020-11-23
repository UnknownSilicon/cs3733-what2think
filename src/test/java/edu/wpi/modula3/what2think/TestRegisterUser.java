package edu.wpi.modula3.what2think;

import edu.wpi.modula3.what2think.http.CreateRequest;
import edu.wpi.modula3.what2think.http.CreateResponse;
import edu.wpi.modula3.what2think.http.RegisterRequest;
import edu.wpi.modula3.what2think.http.RegisterResponse;
import edu.wpi.modula3.what2think.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRegisterUser extends LambdaTest{

    @Test
    void testRegister(){
        RegisterUser ru = new RegisterUser();
        CreateChoice cc = new CreateChoice();

        User user = new User("test", "ew");

        String[] alts = new String[]{"abc", "123", "def"};
        CreateRequest cRequest = new CreateRequest("Test1", 5, alts);
        CreateResponse cResponse = cc.handleRequest(cRequest, createContext("createChoice"));

        RegisterRequest req = new RegisterRequest(cResponse.getChoice().getId(), user);
        RegisterResponse response = ru.handleRequest(req, createContext("registerUser"));

        //assert success in adding
        assertEquals(200, response.getStatusCode());

        req = new RegisterRequest(cResponse.getChoice().getId(), user);
        response = ru.handleRequest(req, createContext("registerUser"));

        //success in finding
        assertEquals(200, response.getStatusCode());

        user.setPassword("no");
        req = new RegisterRequest(cResponse.getChoice().getId(), user);
        response = ru.handleRequest(req, createContext("registerUser"));

        //failure because wrong password
        assertEquals(400, response.getStatusCode());
        assertEquals("incorrect password", response.getError());

        user.setName("blank");
        user.setPassword("");
        req = new RegisterRequest(cResponse.getChoice().getId(), user);
        response = ru.handleRequest(req, createContext("registerUser"));

        //success in adding
        assertEquals(200, response.getStatusCode());

        req = new RegisterRequest(cResponse.getChoice().getId(), user);
        response = ru.handleRequest(req, createContext("registerUser"));

        //success in finding
        assertEquals(200, response.getStatusCode());
    }
}
