package edu.wpi.modula3.what2think;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import edu.wpi.modula3.what2think.db.DAO;
import edu.wpi.modula3.what2think.http.CreateFeedbackRequest;
import edu.wpi.modula3.what2think.http.CreateFeedbackResponse;
import edu.wpi.modula3.what2think.http.CreateRequest;
import edu.wpi.modula3.what2think.http.CreateResponse;
import edu.wpi.modula3.what2think.model.Alternative;
import edu.wpi.modula3.what2think.model.Feedback;

import java.util.Arrays;
import java.util.UUID;

public class CreateFeedback implements RequestHandler<CreateFeedbackRequest, CreateFeedbackResponse> {

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
	public CreateFeedbackResponse handleRequest(CreateFeedbackRequest req, Context context) {
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler of RequestHandler\n");
		logger.log(req.toString());

		Feedback feedback = new Feedback();

		boolean fail = false;
		String failMessage = "";

		logger.log("Creating Feedback\n");

		feedback.setUser(req.getUser());
		feedback.setContent(req.getContent());
		feedback.setTimestamp(req.getTimestamp());

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
		CreateFeedbackResponse response;
		if (fail) {
			response = new CreateFeedbackResponse(400, failMessage);
		} else {
			response = new CreateFeedbackResponse(feedback, 200);  // success
		}

		logger.log("Returning Response\n");

		return response;
	}
}