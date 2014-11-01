package com.fmt.google.oauth;

import java.io.IOException;

import javax.jdo.JDOHelper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jdo.auth.oauth2.JdoCredentialStore;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

//import com.google.api.services.

@SuppressWarnings("serial")
public final class CalendarServletCallbackSample extends AbstractAuthorizationCodeCallbackServlet {

	  @Override
	  protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
	      throws ServletException, IOException {
	    resp.sendRedirect("/");
	  }

	  @Override
	  protected void onError(
	      HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
	      throws ServletException, IOException {
	    // handle error
	  }

	  @Override
	  protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
	    GenericUrl url = new GenericUrl(req.getRequestURL().toString());
	    url.setRawPath("/oauth2callback");
	    return url.build();
	  }

	  @Override
	  protected AuthorizationCodeFlow initializeFlow() throws IOException {
//	    return new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(), new JacksonFactory(),
//	        "415956547431-j75j87p5k0ce3sikh3g3mraaruij53ih.apps.googleusercontent.com", "UPTBXbvUhqcsvAVdyweYGigt",
//	        Collections.singleton(CalendarScopes.CALENDAR)).setCredentialStore(
//	        new JdoCredentialStore(JDOHelper.getPersistenceManagerFactory("transactions-optional")))
//	        .build();
		    return new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
		            new NetHttpTransport(),
		            new JacksonFactory(),
		            new GenericUrl("https://server.example.com/token"),
		            new BasicAuthentication("s6BhdRkqt3", "7Fjfp0ZBr1KtDRbnfVdmIw"),
		            "s6BhdRkqt3",
		            "https://server.example.com/authorize").setCredentialStore(
		            new JdoCredentialStore(JDOHelper.getPersistenceManagerFactory("transactions-optional")))
		            .build();

	  }

	  @Override
	  protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
		  return "fmtaylor92@gmail.com";
	  }
}