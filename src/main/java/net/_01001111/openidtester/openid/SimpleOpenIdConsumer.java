package net._01001111.openidtester.openid;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.springframework.stereotype.Service;

@Service("openIdConsumer")
public class SimpleOpenIdConsumer implements OpenIdConsumer {

	private ConsumerManager manager;
	private Map<String, String> attributeMap;
	
	private final String DISCOVERY_INFORMATION_SESSION_KEY = "openid_discovery_information";

	public SimpleOpenIdConsumer() throws ConsumerException {
		this(new ConsumerManager());
	}

	protected SimpleOpenIdConsumer(ConsumerManager manager) throws ConsumerException {
		this.manager = manager;
		this.buildAttributeMap();
	}
	
	private void buildAttributeMap() {
		this.attributeMap = new LinkedHashMap<String, String>(3);
		this.attributeMap.put("email", "http://axschema.org/contact/email");
		this.attributeMap.put("firstname", "http://axschema.org/namePerson/first");
		this.attributeMap.put("lastname", "http://axschema.org/namePerson/last");
	}

	/* (non-Javadoc)
	 * @see net._01001111.openidtester.openid.OpenIdConsumerI#discover(java.lang.String)
	 */
	public DiscoveryInformation discover(String userId)
			throws DiscoveryException {
		// perform discovery on the user-supplied identifier
		List<?> discoveries = manager.discover(userId);

		// attempt to associate with the OpenID provider
		// and retrieve one service endpoint for authentication
		return manager.associate(discoveries);
	}

	/* (non-Javadoc)
	 * @see net._01001111.openidtester.openid.OpenIdConsumerI#getDiscoveryInformation(java.net.URL)
	 */
	public DiscoveryInformation getDiscoveryInformation(URL openIdEndpoint)
			throws DiscoveryException {
		// create the discovery information from the supplied endpoint URL
		return new DiscoveryInformation(openIdEndpoint);
	}
	
	private void storeDiscoveryInformation(DiscoveryInformation discovery, HttpSession session) {
		session.setAttribute(DISCOVERY_INFORMATION_SESSION_KEY, discovery);
	}
	
	private DiscoveryInformation retrieveDiscoveryInformation(HttpSession session) {
		return (DiscoveryInformation) session.getAttribute(DISCOVERY_INFORMATION_SESSION_KEY);
	}

	/* (non-Javadoc)
	 * @see net._01001111.openidtester.openid.OpenIdConsumerI#performAuthRequest(org.openid4java.discovery.DiscoveryInformation, java.net.URL)
	 */
	public String performAuthRequest(DiscoveryInformation discovered,
			URL returnToUrl) throws ConsumerException, DiscoveryException,
			MessageException, IOException {
		// obtain a AuthRequest message to be sent to the OpenID provider
		AuthRequest authReq = manager.authenticate(discovered, returnToUrl
				.toString());

		// Attribute Exchange
		authReq.addExtension(getAtributeFetchRequest());

		// GET HTTP-redirect to the OpenID Provider endpoint
		// The only method supported in OpenID 1.x
		// redirect-URL usually limited ~2048 bytes
		return authReq.getDestinationUrl(true);
	}

	/* (non-Javadoc)
	 * @see net._01001111.openidtester.openid.OpenIdConsumerI#performAuthRequest(javax.servlet.http.HttpServletRequest, java.net.URL, java.net.URL)
	 */
	public String performAuthRequest(HttpServletRequest request, URL openIdEndpoint, URL returnToUrl)
			throws ConsumerException, DiscoveryException, MessageException,
			IOException {
		// get discovery information
		DiscoveryInformation discovered = getDiscoveryInformation(openIdEndpoint);
		// store discovery information
		storeDiscoveryInformation(discovered, request.getSession());
		// return redirect URL
		return performAuthRequest(discovered, returnToUrl);
	}

	/* (non-Javadoc)
	 * @see net._01001111.openidtester.openid.OpenIdConsumerI#verifyResponse(javax.servlet.http.HttpServletRequest, java.lang.String, org.openid4java.discovery.DiscoveryInformation)
	 */
	public VerificationResult verifyResponse(
			HttpServletRequest request, String receivingURL,
			DiscoveryInformation discovered) throws OpenIDException {
		ParameterList response = new ParameterList(request.getParameterMap());
		
		// verify the response; ConsumerManager needs to be the same
		// (static) instance used to place the authentication request
		return manager.verify(receivingURL, response, discovered);
	}

	/* (non-Javadoc)
	 * @see net._01001111.openidtester.openid.OpenIdConsumerI#verifyResponse(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	public VerificationResult verifyResponse(
			HttpServletRequest request, String receivingURL)
			throws OpenIDException {
		// retrieve discovery information
		DiscoveryInformation discovered = retrieveDiscoveryInformation(request.getSession());
		// verify result
		return verifyResponse(request, receivingURL, discovered);
	}

	protected FetchRequest getAtributeFetchRequest() throws MessageException {
		FetchRequest fetch = FetchRequest.createFetchRequest();
		for (Map.Entry<String, String> mapEntry : attributeMap.entrySet()) {
			fetch.addAttribute(mapEntry.getKey(), // attribute alias
					mapEntry.getValue(), // type URI
					true); // required
		}
		return fetch;
	}

	/* (non-Javadoc)
	 * @see net._01001111.openidtester.openid.OpenIdConsumerI#getFetchedAttributes(org.openid4java.consumer.VerificationResult)
	 */
	public Map<String, String> getFetchedAttributes(
			VerificationResult verification) throws MessageException {
		Map<String, String> fetchedAttributeMap = new LinkedHashMap<String, String>(
				attributeMap.size());
		if (verification.getVerifiedId() != null) {
			AuthSuccess authSuccess = (AuthSuccess) verification
					.getAuthResponse();

			if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
				FetchResponse fetchResp = (FetchResponse) authSuccess
						.getExtension(AxMessage.OPENID_NS_AX);
				for (Map.Entry<String, String> mapEntry : attributeMap
						.entrySet()) {
					String key = mapEntry.getKey();
					List<?> values = fetchResp.getAttributeValues(key
							.toString());
					if (values.size() > 0) {
						Object v = values.get(0);
						if (v != null)
							fetchedAttributeMap.put(key, v.toString());
					}
				}
			}
		}
		return fetchedAttributeMap;
	}
}
