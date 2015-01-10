package com.fmt.common;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientResponse;

public class URL {
	/**
	 * Returns HTML text from URL.
	 * @param siteurl URL of site
	 * @return complete HTML string
	 **/
	public static String getUrl(String siteurl) {
		String output = "";

		Client client = ClientBuilder.newClient();

		WebTarget target= client.target(siteurl);
		
		Response response= target.request(MediaType.TEXT_PLAIN_TYPE).get(Response.class);

		//ClientResponse response = webResource.accept(MediaType.TEXT_HTML_TYPE).get(ClientResponse.class);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}

		output = (String)response.getEntity();

		System.out.println("Output from Server:\n"+ output);

		return output;
	}
}
