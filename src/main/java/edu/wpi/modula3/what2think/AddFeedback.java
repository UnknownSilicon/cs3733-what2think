package edu.wpi.modula3.what2think;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import edu.wpi.modula3.what2think.db.DAO;
import edu.wpi.modula3.what2think.http.AddFeedbackRequest;
import edu.wpi.modula3.what2think.http.GenericResponse;
import edu.wpi.modula3.what2think.model.Feedback;

public class AddFeedback implements RequestHandler<AddFeedbackRequest, GenericResponse> {

	LambdaLogger logger;
	DAO dao;

	/** Store Feedback into RDS
	 *
	 * @throws Exception
	 */
	boolean storeFeedback(Feedback feedback) throws Exception {
		if (logger != null) { logger.log("in storeFeedback"); }
		if (dao == null) {
			dao = new DAO(logger);
			logger.log("Created DAO\n");
		}
		return dao.addFeedback(feedback);
	}

	@Override
	public GenericResponse handleRequest(AddFeedbackRequest req, Context context) {
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler of RequestHandler\n");
		logger.log(req.toString());

		Feedback feedback = new Feedback();

		boolean fail;
		String failMessage = "";

		logger.log("Creating Feedback\n");

		feedback.setUser(req.getUser());
		feedback.setContent(req.getContent());
		feedback.setAlternativeId(req.getAlternativeId());

		logger.log("Storing Feedback in DB\n");

		try {
			fail = !storeFeedback(feedback);
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