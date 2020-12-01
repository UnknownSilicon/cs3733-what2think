package edu.wpi.modula3.what2think;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import edu.wpi.modula3.what2think.db.DAO;
import edu.wpi.modula3.what2think.http.GenericResponse;
import edu.wpi.modula3.what2think.http.RegisterRequest;
import edu.wpi.modula3.what2think.model.User;

public class RegisterUser implements RequestHandler<RegisterRequest, GenericResponse> {

    LambdaLogger logger;
    DAO dao;

    boolean validateInput(String id, User user){
        if(id == null || id.equals("")){
            return false;
        }
        else if(user == null){
            return false;
        }
        else if(user.getName() == null || user.getName().equals("")){
            return false;
        }
        else {
            return user.getPassword() != null;
        }
    }

    boolean getUser(String id, User user){
        if (logger != null) { logger.log("in getUser"); }
        if (dao == null) {
            dao = new DAO(logger);
            logger.log("Created DAO\n");
        }

        return dao.getUser(id, user);
    }

    boolean addUser(String id, User user) throws Exception{
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
    public GenericResponse handleRequest(RegisterRequest req, Context context) {
        logger = context.getLogger();
        logger.log("Loading Java Lambda handler of RequestHandler\n");
        logger.log(req.toString());

        boolean fail;
        String failMessage = "";

        try {
            fail = !validateInput(req.getId(), req.getUser());
            if(!fail) {
                fail = !getUser(req.getId(), req.getUser());
                if (fail) {
                    fail = !addUser(req.getId(), req.getUser());
                } else {
                    fail = !validateUser(req.getId(), req.getUser());
                    failMessage = "incorrect password";
                }
            }
            else{
                failMessage = "invalid input";
            }
        } catch (Exception e) {
            logger.log("Exception!\n" + e.getMessage() + "\n");
            fail = true;
            failMessage = e.getMessage();
        }

        logger.log("Creating Response\n");
        // compute proper response and return. Note that the status code is internal to the HTTP response
        // and has to be processed specifically by the client code.
        GenericResponse response;
        if (fail) {
            response = new GenericResponse(400, failMessage);
        } else {
            response = new GenericResponse(200);  // success
        }

        logger.log("Returning Response\n");

        return response;
    }
}
