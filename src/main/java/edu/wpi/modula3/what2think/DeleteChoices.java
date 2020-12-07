package edu.wpi.modula3.what2think;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import edu.wpi.modula3.what2think.db.DAO;
import edu.wpi.modula3.what2think.http.CompleteRequest;
import edu.wpi.modula3.what2think.http.DeleteRequest;
import edu.wpi.modula3.what2think.http.GenericResponse;
import edu.wpi.modula3.what2think.model.Alternative;
import org.joda.time.DateTime;

public class DeleteChoices implements RequestHandler<DeleteRequest, GenericResponse> {

	LambdaLogger logger;
	DAO dao;

	/** Store Choice into RDS
	 *
	 * @throws Exception
	 */
	boolean deleteChoices (float days) throws Exception {
		if (logger != null) { logger.log("in deleteChoices"); }
		if (dao == null) {
			dao = new DAO(logger);
			logger.log("Created DAO\n");
		}

		return dao.deleteChoices(days);
	}

	@Override
	public GenericResponse handleRequest(DeleteRequest req, Context context) {
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler of RequestHandler\n");
		logger.log(req.toString());

		boolean fail = false;
		String failMessage = "";

		logger.log("Deleting Choice\n");

		float days = req.getDays();

		logger.log("Removing Choice from DB\n");

		try {
			fail = !deleteChoices(days);
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