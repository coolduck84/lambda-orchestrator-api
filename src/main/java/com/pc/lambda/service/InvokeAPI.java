package com.pc.lambda.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class InvokeAPI {

	public String callOrchestratorAPI(String clientId, String clientSecret, String scope, String grantType,
			String orchestratorBaseURL) {
		
		System.out.println("Inside callOrchestratorAPI().....");
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		headers.set("X-UIPATH-TenantName", "Default");
		headers.set("X-UIPATH-OrganizationUnitId", "1");
		headers.set("Content-Type", "application/x-www-form-urlencoded");

		String releaseKey = "";
		String accessToken = getAccessToken(clientId, clientSecret, scope, grantType, orchestratorBaseURL, headers);
		if (accessToken == null) {
			return "Access token could not be retrieved !";
		} else {
			releaseKey = getReleaseKey(accessToken, orchestratorBaseURL, headers);
			if (releaseKey == null) {
				return "Release Key could not be retrieved !";
			}
		}

		System.out.println("Completed callOrchestratorAPI() !");
		return releaseKey;
	}

	private String getAccessToken(String clientId, String clientSecret, String scope, String grantType,
			String orchestratorBaseURL, HttpHeaders headers) {
		System.out.println("Inside getAccessToken().....");
		
		String apiUrl = orchestratorBaseURL + "identity/connect/token";
		String accessToken = null;
		try {
			// Create XML string/data for the API request
			String payload = "grant_type=" + grantType + "&client_id=" + clientId + "&client_secret=" + clientSecret
					+ "&scope=" + scope + "";

			URI uri = new URI(apiUrl);
			HttpEntity<String> request = new HttpEntity<>(payload, headers);
			
			System.out.println("API Invocation started !");
			ResponseEntity<String> response = new RestTemplate().postForEntity(uri, request, String.class);
			System.out.println("API Invocation completed !");
			
			if (response != null) {
				JSONObject jsonObject = new JSONObject(response.getBody());
				accessToken = jsonObject.get("access_token").toString();
			}

			System.out.println("Response status getAccessToken(): " + response.getStatusCodeValue());
		} catch (URISyntaxException uRISyntaxException) {
			System.out.println("Exception occured while setting uri:" + uRISyntaxException);
			System.out.println("Uri cannot be built" + uRISyntaxException.getReason());
		} catch (Exception exception) {
			System.out.println("Exception occured while getting access token:" + exception);
		}

		System.out.println("Completed getAccessToken() !");
		return accessToken;
	}

	private String getReleaseKey(String accessToken, String orchestratorBaseURL, HttpHeaders headers) {
		System.out.println("Inside getReleaseKey().....");
		
		String releaseKey = null;
		try {
			String processName = "FIPC_Maestro_POC";
			String apiUrl = orchestratorBaseURL + "odata/Releases?$Filter=Name";
			String param = " eq '" + processName + "'";
			apiUrl = apiUrl + URLEncoder.encode(param, "UTF-8");

			headers.add("Authorization", "Bearer " + accessToken);

			URI uri = new URI(apiUrl);
			HttpEntity<Void> request = new HttpEntity<>(headers);
			
			System.out.println("API Invocation started !");
			ResponseEntity<String> response = new RestTemplate().exchange(uri, HttpMethod.GET, request, String.class);
			System.out.println("API Invocation completed !");
			
			if (response != null) {
				JSONObject jsonObject = new JSONObject(response.getBody());
				int processCount = jsonObject.getInt("@odata.count");
				if (processCount != 1) {
					throw new Exception("Process count for process: " + processName + " is " + processCount
							+ " which is unexpected");
				} else {
					JSONObject valueJsonObject = jsonObject.getJSONArray("value").getJSONObject(0);
					releaseKey = valueJsonObject.get("Key").toString();
				}
			}

			System.out.println("Response status getReleaseKey(): " + response.getStatusCodeValue());
		} catch (URISyntaxException uRISyntaxException) {
			System.out.println("Exception occured while setting uri:" + uRISyntaxException);
			System.out.println("Uri cannot be built" + uRISyntaxException.getReason());
		} catch (Exception exception) {
			System.out.println("Exception occured while getting release key:" + exception);
		}

		System.out.println("Completed getReleaseKey() !");
		return releaseKey;
	}
}
