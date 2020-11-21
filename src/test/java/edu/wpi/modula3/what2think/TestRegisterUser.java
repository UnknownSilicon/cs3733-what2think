package edu.wpi.modula3.what2think;

import edu.wpi.modula3.what2think.http.CreateRequest;
import edu.wpi.modula3.what2think.http.CreateResponse;
import edu.wpi.modula3.what2think.http.RegisterRequest;
import edu.wpi.modula3.what2think.http.RegisterResponse;
import edu.wpi.modula3.what2think.model.User;
import org.junit.jupiter.api.Test;

public class TestRegisterUser extends LambdaTest{

    @Test
    void testRegisterName(){
        RegisterUser ru = new RegisterUser();
        CreateChoice cc = new CreateChoice();

        User user = new User("test", null);

        String[] alts = new String[]{"abc", "123", "def"};
        CreateRequest cRequest = new CreateRequest("Test1", 5, alts);
        CreateResponse cResponse = cc.handleRequest(cRequest, createContext("createChoice"));

        RegisterRequest req = new RegisterRequest(cResponse.getChoice().getId(), user);
        RegisterResponse response = ru.handleRequest(req, createContext("registerUser"));


    }
}
