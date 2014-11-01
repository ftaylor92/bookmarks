package com.fmt.common;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class URL {
	/**
	 * Returns HTML text from URL.
	 * @param siteurl URL of site
	 * @return complete HTML string
	 **/
	public static String getUrl(String siteurl) {
		String output = "";

		Client client = Client.create();

		WebResource webResource = client
				.resource(siteurl);

		ClientResponse response = webResource.accept(MediaType.TEXT_HTML_TYPE)
				.get(ClientResponse.class);

		if (response.getStatus() != 200) {
			output= "Failed : HTTP error code : "+ response.getStatus();
			System.err.println(output);
		}

		output = response.getEntity(String.class);

		System.out.println("Output from Server:\n"+ output);

		return output;
	}
}
