package edu.wpi.modula3.what2think;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import edu.wpi.modula3.what2think.db.DAO;
import edu.wpi.modula3.what2think.http.GetRequest;
import edu.wpi.modula3.what2think.http.GetResponse;
import edu.wpi.modula3.what2think.model.Choice;

public class GetChoice implements RequestHandler<GetRequest, GetResponse> {

    LambdaLogger logger;
    DAO dao;

    Choice getChoice(String choiceId) throws Exception {
        if (logger != null) { logger.log("in getChoice"); }
        if (dao == null) {
            dao = new DAO(logger);
            logger.log("Created DAO\n");
        }

        return dao.getChoice(choiceId);
    }

    @Override
    public GetResponse handleRequest(GetRequest req, Context context) {
        logger = context.getLogger();
        logger.log("Loading Java Lambda handler of RequestHandler\n");
        logger.log(req.toString());

        boolean fail = false;
        String failMessage = "";

        Choice choice = null;

        logger.log("Loading choice from Choice DB\n");


        try {
            choice = getChoice(req.getID());
            if (choice == null) fail = true;
        } catch (Exception e) {
            logger.log("Exception!\n" + e.getMessage() + "\n");
            fail = true;
        }

        logger.log("Creating Response\n");
        // compute proper response and return. Note that the status code is internal to the HTTP response
        // and has to be processed specifically by the client code.
        GetResponse response;
        if (fail) {
            response = new GetResponse(400, failMessage);
        } else {
            response = new GetResponse(choice, 200);  // success
        }

        logger.log("Returning Response\n");

        return response;
    }
}