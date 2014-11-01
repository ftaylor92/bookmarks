package com.fmt.rest.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/** Jersey REST_WS wrapper. **/
public class JerseyPutClient {

	/**
	 * main. for testing.
	 * 
	 * @param args
	 **/
	public static void main(String[] args) {

		try {

			Client client = Client.create();

			WebResource webResource = client
					.resource("http://localhost:8080/bookmarks/rest/secure/users");
			// .resource("http://localhost:8080/bookmarks/rest/secure/contacts");

			String input = "{\"username\":\"ftaylor92\"}";

			ClientResponse response = webResource.type("application/json")
					.delete(ClientResponse.class, input);

			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			System.out.println("Output from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
}