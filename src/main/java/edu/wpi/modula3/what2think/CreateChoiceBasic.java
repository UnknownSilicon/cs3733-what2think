package edu.wpi.modula3.what2think;

import java.util.Scanner;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import edu.wpi.modula3.what2think.http.CreateRequest;
import edu.wpi.modula3.what2think.http.CreateResponse;

public class CreateChoiceBasic implements RequestHandler<CreateRequest, CreateResponse> {

	LambdaLogger logger;

	/** Load from RDS, if it exists
	 *
	 * @throws Exception
	 */
	double loadValueFromRDS(String arg) throws Exception {
		if (logger != null) { logger.log("in loadValue"); }
		/*ConstantsDAO dao = new ConstantsDAO(logger);
		logger.log("Created ConstantsDAO\n");
		Constant constant = dao.getConstant(arg);
		logger.log("Retrieved Constant\n");
		return constant.value;*/
		return -1;
	}

	@Override
	public CreateResponse handleRequest(CreateRequest req, Context context) {
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler of RequestHandler\n");
		logger.log(req.toString());

		boolean fail = false;
		String failMessage = "";

		logger.log("Creating Response\n");

		// compute proper response and return. Note that the status code is internal to the HTTP response
		// and has to be processed specifically by the client code.
		CreateResponse response;
		if (fail) {
			response = new CreateResponse(400, failMessage);
		} else {
			response = new CreateResponse(200, "TestSucceeded");  // success
		}

		logger.log("Returning Response\n");

		return response;
	}
}