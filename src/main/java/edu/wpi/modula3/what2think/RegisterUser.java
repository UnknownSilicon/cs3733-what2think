package edu.wpi.modula3.what2think;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import edu.wpi.modula3.what2think.db.DAO;
import edu.wpi.modula3.what2think.http.RegisterRequest;
import edu.wpi.modula3.what2think.http.RegisterResponse;
import edu.wpi.modula3.what2think.model.User;

public class RegisterUser implements RequestHandler<RegisterRequest, RegisterResponse> {

    LambdaLogger logger;
    DAO dao;

    boolean getUser(String id, User user){
        if (logger != null) { logger.log("in getUser"); }
        if (dao == null) {
            dao = new DAO(logger);
            logger.log("Created DAO\n");
        }

        return dao.getUser(id, user);
    }

    boolean addUser(String id, User user){
        if (logger != null) { logger.log("in addUser"); }
        if (dao == null) {
            dao = new DAO(logger);
            logger.log("Created DAO\n");
        }

        return dao.addUser(id, user);
    }

    boolean validateUser(String id, User user){
        if (logger != null) { logger.log("in validateUser"); }
        if (dao == null) {
            dao = new DAO(logger);
            logger.log("Created DAO\n");
        }

        return dao.validateUser(id, user);
    }

    @Override
    public RegisterResponse handleRequest(RegisterRequest req, Context context) {
        logger = context.getLogger();
        logger.log("Loading Java Lambda handler of RequestHandler\n");
        logger.log(req.toString());

        boolean fail;
        String failMessage = "";

        try {
            fail = !getUser(req.getId(), req.getUser());
            if(fail){
                fail = !addUser(req.getId(), req.getUser());
                failMessage = "failed to add user";
            }
            else{
                fail = !validateUser(req.getId(), req.getUser());
                failMessage = "incorrect password";
            }
        } catch (Exception e) {
            logger.log("Exception!\n" + e.getMessage() + "\n");
            fail = true;
        }

        logger.log("Creating Response\n");
        // compute proper response and return. Note that the status code is internal to the HTTP response
        // and has to be processed specifically by the client code.
        RegisterResponse response;
        if (fail) {
            response = new RegisterResponse(400, failMessage);
        } else {
            response = new RegisterResponse(200);  // success
        }

        logger.log("Returning Response\n");

        return response;
    }
}
