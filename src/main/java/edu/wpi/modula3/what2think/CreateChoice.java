package edu.wpi.modula3.what2think;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import edu.wpi.modula3.what2think.db.DAO;
import edu.wpi.modula3.what2think.http.CreateRequest;
import edu.wpi.modula3.what2think.http.CreateResponse;
import edu.wpi.modula3.what2think.model.Alternative;
import edu.wpi.modula3.what2think.model.Choice;

import java.util.Arrays;
import java.util.UUID;

public class CreateChoice implements RequestHandler<CreateRequest, CreateResponse> {

	LambdaLogger logger;
	DAO dao;

	/** Store Choice into RDS
	 *
	 * @throws Exception
	 */
	boolean storeChoice(Choice choice) throws Exception {
		if (logger != null) { logger.log("in storeChoice"); }
		if (dao == null) {
			dao = new DAO(logger);
			logger.log("Created DAO\n");
		}

		return dao.addChoice(choice);
	}

	@Override
	public CreateResponse handleRequest(CreateRequest req, Context context) {
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler of RequestHandler\n");
		logger.log(req.toString());

		Choice choice = new Choice();

		boolean fail = false;
		String failMessage = "";

		logger.log("Creating Choice\n");

		choice.setId(UUID.randomUUID().toString());
		choice.setDescription(req.getDescription());
		choice.setMaxUsers(req.getMaxUsers());

		Alternative[] alternatives = Arrays.stream(req.getAlternatives())
									.map(s -> new Alternative(UUID.randomUUID().toString(), s))
									.toArray(Alternative[]::new);

		choice.setAlternatives(alternatives);
		choice.setCompleted(false);

		logger.log("Storing Choice in DB\n");

		try {
			fail = !storeChoice(choice);
		} catch (Exception e) {
			logger.log("Exception!\n" + e.getMessage() + "\n");
			fail = true;
		}

		logger.log("Creating Response\n");
		// compute proper response and return. Note that the status code is internal to the HTTP response
		// and has to be processed specifically by the client code.
		CreateResponse response;
		if (fail) {
			response = new CreateResponse(400, failMessage);
		} else {
			response = new CreateResponse(choice, 200);  // success
		}

		logger.log("Returning Response\n");

		return response;
	}
}