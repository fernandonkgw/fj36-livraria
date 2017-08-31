package br.com.caelum.livraria.controller;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.caelum.livraria.rest.oauth2.AccessToken;

@Controller
@Scope("request")
public class OAuthPasswordController {
	
	private static final String PASSWORD_GRANT_TOKEN_URL = "http://localhost:8080/fj36-webservice/oauth/password/token";
	
	@Autowired
	private AccessToken accessToken;
	
	@RequestMapping("/oauth/password/form")
	public String form(){
		return "oauth/form";
	}
	
	@RequestMapping(value="/oauth/password/token", method=RequestMethod.POST)
	public String token(String username, String password) throws Exception{
		
		OAuthClientRequest request = OAuthClientRequest
				.tokenLocation(PASSWORD_GRANT_TOKEN_URL)
				.setGrantType(GrantType.PASSWORD)
				.setClientId("livraria_id")
				.setClientSecret("livraria_secret")
				.setUsername(username)
				.setPassword(password)
				.buildBodyMessage();

		OAuthClient client = new OAuthClient(new URLConnectionClient());
		OAuthJSONAccessTokenResponse tokenResponse = client.accessToken(request);
		String token = tokenResponse.getAccessToken();
		accessToken.setToken(token);
		System.out.println("Token recebido " + token);
		
		return "redirect:/carrinho/criarPagamento";
	}
}
