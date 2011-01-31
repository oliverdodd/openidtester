package net._01001111.openidtester.openid;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.MessageException;

public interface OpenIdConsumer {

	/**
	 * Discover an OpenId endpoint using the provided OpenId identifier.
	 * 
	 * @param userId
	 * @return
	 * @throws DiscoveryException
	 */
	public abstract DiscoveryInformation discover(String userId)
			throws DiscoveryException;

	/**
	 * 
	 * Get the Discovery Information for the provided OpenId endpoint
	 * 
	 * @param openIdEndpoint
	 * @return
	 * @throws DiscoveryException
	 */
	public abstract DiscoveryInformation getDiscoveryInformation(
			URL openIdEndpoint) throws DiscoveryException;

	/**
	 * 
	 * Perform an OpenId Authentication Request
	 * 
	 * @param discovered
	 * @param returnToUrl
	 * @return
	 * @throws ConsumerException
	 * @throws DiscoveryException
	 * @throws MessageException
	 * @throws IOException
	 */
	public abstract String performAuthRequest(DiscoveryInformation discovered,
			URL returnToUrl) throws ConsumerException, DiscoveryException,
			MessageException, IOException;

	/**
	 * 
	 * Perform an OpenId Authentication Request.
	 * @param request TODO
	 * @param openIdEndpoint
	 * @param returnToUrl
	 * 
	 * @return
	 * @throws ConsumerException
	 * @throws DiscoveryException
	 * @throws MessageException
	 * @throws IOException
	 */
	public abstract String performAuthRequest(HttpServletRequest request,
			URL openIdEndpoint, URL returnToUrl) throws ConsumerException,
			DiscoveryException, MessageException, IOException;

	/**
	 * 
	 * Verify an OpenId Authentication Response
	 * 
	 * @param request
	 * @param receivingURL
	 * @param discovered
	 * @return
	 * @throws OpenIDException
	 */
	public abstract VerificationResult verifyResponse(
			HttpServletRequest request, String receivingURL,
			DiscoveryInformation discovered) throws OpenIDException;

	/**
	 * 
	 * Verify an OpenId Authentication Response
	 * 
	 * @param request
	 * @param receivingURL
	 * @return
	 * @throws OpenIDException
	 */
	public abstract VerificationResult verifyResponse(
			HttpServletRequest request, String receivingURL)
			throws OpenIDException;

	/**
	 * 
	 * Get the attributes fetched via the OpenId attribute exchange.
	 * 
	 * @param verification
	 * @return
	 * @throws MessageException
	 */
	public abstract Map<String, String> getFetchedAttributes(
			VerificationResult verification) throws MessageException;

}