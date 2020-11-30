package edu.wpi.modula3.what2think;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import edu.wpi.modula3.what2think.db.DAO;
import edu.wpi.modula3.what2think.http.GenericResponse;
import edu.wpi.modula3.what2think.http.RegisterResponse;
import edu.wpi.modula3.what2think.http.VoteRequest;
import edu.wpi.modula3.what2think.model.AlternativeAction;

public class AddApproval implements RequestHandler<VoteRequest, GenericResponse> {

    LambdaLogger logger;
    DAO dao;

    boolean validateInput(String id, AlternativeAction act) throws Exception{
        if (logger != null) { logger.log("in storeChoice"); }
        if (dao == null) {
            dao = new DAO(logger);
            logger.log("Created DAO\n");
        }

        if(id == null || id.equals("")){
            return false;
        }
        else if(act == null){
            return false;
        }
        else if(act.getUser() == null || act.getAlternative() == null){
            return false;
        }
        else if(act.getUser().getName() == null || act.getUser().getName().equals("")){
            return false;
        }
        else if(act.getUser().getPassword() == null){
            return false;
        }
        else if(act.getAlternative().getId() == null || act.getAlternative().getId().equals("")){
            return false;
        }
        return dao.validateAlternativeAction(id,act);
    }

    @Override
    public GenericResponse handleRequest(VoteRequest req, Context context) {
        logger = context.getLogger();
        logger.log("Loading Java Lambda handler of RequestHandler\n");
        logger.log(req.toString());

        boolean fail;
        String failMessage = "";

        try {
            logger.log("validating input");
            fail = !validateInput(req.getId(), req.getAltAction());
            if(!fail){
                logger.log("checking for opposite");
                if(dao.voteExists(req.getId(), req.getAltAction(), false)){
                    logger.log("deleting opposite");
                    dao.deleteVote(req.getId(), req.getAltAction(), false);
                }
                logger.log("adding vote");
                fail = !dao.addVote(req.getId(), req.getAltAction(), true);
            }
            else{
                failMessage = "invalid input";
            }
        } catch (Exception e) {
            logger.log("Exception!\n" + e.getMessage() + "\n");
            e.printStackTrace();
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
