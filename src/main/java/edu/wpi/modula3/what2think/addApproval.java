package edu.wpi.modula3.what2think;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import edu.wpi.modula3.what2think.db.DAO;
import edu.wpi.modula3.what2think.http.GenericResponse;
import edu.wpi.modula3.what2think.http.RegisterResponse;
import edu.wpi.modula3.what2think.http.VoteRequest;

public class addApproval implements RequestHandler<VoteRequest, GenericResponse> {

    LambdaLogger logger;
    DAO dao;



    @Override
    public GenericResponse handleRequest(VoteRequest req, Context context) {
        logger = context.getLogger();
        logger.log("Loading Java Lambda handler of RequestHandler\n");
        logger.log(req.toString());

        boolean fail = false; //CHANGE CHANGE CHANGE CHANGE CHANGE CHANGE CHANGE
        String failMessage = "";

        try {

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
