package br.com.caelum.livraria.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.caelum.livraria.rest.oauth2.AccessToken;

@Controller
@Scope("request")
public class OAuthCodeController {

	private static final String REDIRECT_URL = "http://localhost:8088/fj36-livraria/oauth/code/returnURL";
	
	private static final String AUTH_CODE_FORM_URL = "http://localhost:8080/fj36-webservice/oauth/code/form";
	
	private static final String AUTH_CODE_TOKEN_URL = "http://localhost:8080/fj36-webservice/oauth/code/token";
	
	@Autowired
	private AccessToken accessToken;

	@RequestMapping("/oauth/code") //callback
	public String redirectToPayfast() throws Exception {
		
		OAuthClientRequest message = OAuthClientRequest
				.authorizationLocation(AUTH_CODE_FORM_URL)
				.setClientId("livraria_id")
				.setResponseType(OAuth.OAUTH_CODE)
				.setRedirectURI(REDIRECT_URL)
				.buildQueryMessage();
		
		String oauthURI = message.getLocationUri();
		return "redirect:" + oauthURI;
	}
	
	@RequestMapping("/oauth/code/returnURL")
	public String oauthReturn(HttpServletRequest request) throws Exception{
		
		OAuthAuthzResponse oauthResponse = OAuthAuthzResponse
				.oauthCodeAuthzResponse(request);
		
		String code = oauthResponse.getCode();
		
		OAuthClientRequest oauthRequest = OAuthClientRequest
				.tokenLocation(AUTH_CODE_TOKEN_URL)
				.setGrantType(GrantType.AUTHORIZATION_CODE)
				.setClientId("livraria_id")
				.setClientSecret("livraria_secret")
				.setCode(code)
				.setRedirectURI(REDIRECT_URL)
				.buildQueryMessage();
		
		OAuthClient client = new OAuthClient(new URLConnectionClient());
		OAuthJSONAccessTokenResponse tokenResponse = client.accessToken(oauthRequest, OAuth.HttpMethod.POST);
		
		String token = tokenResponse.getAccessToken();
		
		accessToken.setToken(token);
		
		return "redirect:/carrinho/criarPagamento";
	}
}
