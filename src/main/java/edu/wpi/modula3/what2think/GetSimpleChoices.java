package edu.wpi.modula3.what2think;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import edu.wpi.modula3.what2think.db.DAO;
import edu.wpi.modula3.what2think.http.AdminGetRequest;
import edu.wpi.modula3.what2think.http.AdminGetResponse;
import edu.wpi.modula3.what2think.model.SimpleChoice;

public class GetSimpleChoices implements RequestHandler<AdminGetRequest, AdminGetResponse> {

    LambdaLogger logger;
    DAO dao;

    SimpleChoice[] getSimpleChoices() throws Exception {
        if (logger != null) { logger.log("in getSimpleChoices"); }
        if (dao == null) {
            dao = new DAO(logger);
            logger.log("Created DAO\n");
        }

        return dao.getSimplifiedChoices();
    }

    @Override
    public AdminGetResponse handleRequest(AdminGetRequest req, Context context) {
        logger = context.getLogger();
        logger.log("Loading Java Lambda handler of RequestHandler\n");
        logger.log(req.toString());

        boolean fail = false;
        String failMessage = "";

        SimpleChoice[] simpleChoices = null;

        logger.log("Loading choices from Choice DB\n");


        try {
            simpleChoices = getSimpleChoices();
            if (simpleChoices == null) fail = true;//?????
        } catch (Exception e) {
            logger.log("Exception!\n" + e.getMessage() + "\n");
            fail = true;
        }

        logger.log("Creating Response\n");
        // compute proper response and return. Note that the status code is internal to the HTTP response
        // and has to be processed specifically by the client code.
        AdminGetResponse response;
        if (fail) {
            response = new AdminGetResponse(400, failMessage);
        } else {
            response = new AdminGetResponse(simpleChoices, 200);  // success
        }

        logger.log("Returning Response\n");

        return response;
    }
}