package net._01001111.openidtester.openid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.MessageException;
import org.openid4java.message.ax.FetchRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

public class SimpleOpenIdConsumerTest {

	private String openIdIdentifier = "https://www.google.com/accounts/o8/id?my_open_id_identifier";
	private String endpointUrl = "https://www.google.com/accounts/o8/ud";
	private String returnToUrl = "http://test.example.com";

	private MockHttpServletRequest request = new MockHttpServletRequest();

	private SimpleOpenIdConsumer consumer;

	@Before
	public void setup() {
		request.setSession(new MockHttpSession());
		consumer = getOpenIdConsumer();
	}

	private SimpleOpenIdConsumer getOpenIdConsumer() {
		try {
			return new SimpleOpenIdConsumer(new MockConsumerManager(endpointUrl));
			// return new SimpleOpenIdConsumer();
		} catch (ConsumerException e) {
			fail();
		}
		return null;
	}

	@Test
	public void discover() throws ConsumerException, DiscoveryException,
			MessageException, IOException {
		DiscoveryInformation discoveryInformation = consumer
				.discover(openIdIdentifier);
		assertNotNull(discoveryInformation);
		assertEquals(endpointUrl, discoveryInformation.getOPEndpoint()
				.toString());
	}

	@Test
	public void getDiscoveryInformation() throws ConsumerException,
			DiscoveryException, MessageException, IOException {
		DiscoveryInformation discoveryInformation = consumer
				.getDiscoveryInformation(new URL(endpointUrl));
		assertNotNull(discoveryInformation);
		assertEquals(endpointUrl, discoveryInformation.getOPEndpoint()
				.toString());
	}

	@Test
	public void getAttributeFetchRequest() throws MessageException {
		FetchRequest atributeFetchRequest = consumer.getAtributeFetchRequest();
		assertNotNull(atributeFetchRequest);
		assertTrue(atributeFetchRequest.getAttributes().containsKey("email"));
	}

	@Test
	public void performAuthRequest() throws ConsumerException,
			DiscoveryException, MessageException, IOException {
		DiscoveryInformation discoveryInformation = consumer
				.getDiscoveryInformation(new URL(endpointUrl));
		String redirectUrl = consumer.performAuthRequest(discoveryInformation,
				new URL(returnToUrl));
		System.out.println(redirectUrl);
		assertNotNull(redirectUrl);
	}

	@Test
	public void performAuthRequestStoreDiscoveryInfo()
			throws ConsumerException, DiscoveryException, MessageException,
			IOException {
		String redirectUrl = consumer.performAuthRequest(request, new URL(
				endpointUrl), new URL(returnToUrl));
		assertNotNull(redirectUrl);
	}

	@Test
	public void verifyResponse() throws MalformedURLException, OpenIDException {
		DiscoveryInformation discovered = new DiscoveryInformation(new URL(
				endpointUrl));
		VerificationResult verificationResult = consumer.verifyResponse(
				request, returnToUrl, discovered);
		assertNotNull(verificationResult);
	}

}
