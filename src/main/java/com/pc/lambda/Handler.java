package com.pc.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.pc.lambda.service.InvokeAPI;

public class Handler implements RequestHandler<String, String> {

	private static LambdaLogger logger = null;

	public String handleRequest(String input, Context context) {
		
		System.out.println("Inside Lambda Handler....");

		logger = context.getLogger();

		// Read all of the environment variables
		String clientId = System.getenv("appId");
		String clientSecret = System.getenv("appSecret");
		String scope = System.getenv("scope");
		String grantType = System.getenv("grantType");
		String orchestratorBaseURL = System.getenv("orchestratorBaseURL");
		
		System.setProperty("https.proxyHost", System.getenv("https_proxyHost"));
		System.setProperty("https.proxyPort", System.getenv("https_proxyPort"));
		
		logger.log("\nscope => " + scope);
		logger.log("\ngrantType => " + grantType);
		logger.log("\norchestratorBaseURL => " + orchestratorBaseURL);
		if (clientId != null && clientId.length() > 5) {
			logger.log("\nclientId => " + clientId.substring(0, 4));
		} else {
			logger.log("\nclientId not available");
		}
		if (clientSecret != null && clientSecret.length() > 5) {
			logger.log("\nclientSecret => " + clientSecret.substring(0, 4));
		} else {
			logger.log("\nclientSecret not available");
		}

		try {
			InvokeAPI api = new InvokeAPI();
			String releaseKey = api.callOrchestratorAPI(clientId, clientSecret, scope, grantType, orchestratorBaseURL);
			return "Release Key: " + releaseKey;
		} catch (Exception e) {
			logger.log("\nException while performing file operations: " + e.getMessage() + " \nStack Trace: "
					+ e.getStackTrace());
		}

		logger.log("\nCompleted Lambda Handler !");
		return "";
	}
	
}
