package edu.wpi.modula3.what2think;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import edu.wpi.modula3.what2think.db.DAO;
import edu.wpi.modula3.what2think.http.CompleteRequest;
import edu.wpi.modula3.what2think.http.GenericResponse;
import edu.wpi.modula3.what2think.model.Alternative;

public class CompleteChoice implements RequestHandler<CompleteRequest, GenericResponse> {

	LambdaLogger logger;
	DAO dao;

	/** Store Choice into RDS
	 *
	 * @throws Exception
	 */
	boolean completeChoice (String choiceId, Alternative alternative) throws Exception {
		if (logger != null) { logger.log("in completeChoice"); }
		if (dao == null) {
			dao = new DAO(logger);
			logger.log("Created DAO\n");
		}

		return dao.completeChoice(choiceId, alternative);
	}

	@Override
	public GenericResponse handleRequest(CompleteRequest req, Context context) {
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler of RequestHandler\n");
		logger.log(req.toString());

		boolean fail = false;
		String failMessage = "";

		logger.log("Completing Choice\n");

		String choiceId = req.getChoiceId();
		Alternative alternative = req.getAlternative();

		logger.log("Storing Completion in DB\n");

		try {
			fail = !completeChoice(choiceId, alternative);
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