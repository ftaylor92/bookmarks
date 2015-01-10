package com.fmt.rest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientResponse;

/** Jersey REST_WS wrapper. **/
public class JerseyDeleteClient {

	/**
	 * main. for testing.
	 * 
	 * @param args
	 **/
	public static void main(String[] args) {

		try {
			Client client = ClientBuilder.newClient();

			WebTarget target= client.target("http://localhost:8080/bookmarks/rest/secure/post");
			// .resource("http://localhost:8080/bookmarks/rest/secure/contacts");

			String input = "{\"username\":\"ftaylor92\"}";

			//ClientResponse response = webResource.type("application/json").delete(ClientResponse.class, input);
			Response response= target.request(MediaType.APPLICATION_JSON).post(Entity.entity(ClientResponse.class, input));
			
			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			System.out.println("Output from Server .... \n");
			String output = (String)response.getEntity();
			System.out.println(output);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
}