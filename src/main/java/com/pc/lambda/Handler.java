package com.pc.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.pc.lambda.service.InvokeAPI;

public class Handler implements RequestHandler<String, String> {

	public String handleRequest(String input, Context context) {

		System.out.println("==== Environment Variables ====");

		// Read all of the environment variables
		String clientId = System.getenv("appId");
		String clientSecret = System.getenv("appSecret");
		String scope = System.getenv("scope");
		String grantType = System.getenv("grantType");
		String orchestratorBaseURL = System.getenv("orchestratorBaseURL");
		String certFilePath = System.getenv("certFilePath");

		if (clientId != null && clientId.length() > 5) {
			System.out.println("clientId => " + clientId.substring(0, 4));
		} else {
			System.out.println("clientId not available");
		}
		if (clientSecret != null && clientSecret.length() > 5) {
			System.out.println("clientSecret => " + clientSecret.substring(0, 4));
		} else {
			System.out.println("clientSecret not available");
		}
		System.out.println("scope => " + scope);
		System.out.println("grantType => " + grantType);
		System.out.println("orchestratorBaseURL => " + orchestratorBaseURL);
		System.out.println("certFilePath => " + certFilePath);
		
		System.setProperty("https.proxyHost", System.getenv("https_proxyHost"));
		System.setProperty("https.proxyPort", System.getenv("https_proxyPort"));
		System.out.println("proxyHost => " + System.getProperty("https.proxyHost"));
		System.out.println("proxyPort => " + System.getProperty("https.proxyPort"));

		try {
			InvokeAPI api = new InvokeAPI();
			String releaseKey = api.callOrchestratorAPI(clientId, clientSecret, scope, grantType, orchestratorBaseURL,
					certFilePath);
			return releaseKey;
		} catch (Exception e) {
			System.out.println("Exception while calling Orchestrator APIs: " + e.getMessage() + " \nStack Trace: "
					+ e.getStackTrace());
		}

		System.out.println("Completed Lambda Handler !");
		return "";
	}
}
