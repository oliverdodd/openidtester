package net._01001111.openidtester.webapp.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import net._01001111.openidtester.openid.OpenIdConsumer;

import org.junit.After;
import org.junit.Test;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

public class OpenIdControllerTest {

	OpenIdConsumer openIdConsumer = mock(OpenIdConsumer.class);
	OpenIdController controller = new OpenIdController(openIdConsumer);
	
	HttpServletRequest request = new MockHttpServletRequest();
	final String endpoint = "http://example.com";
	final String identifier = endpoint + "/my_open_id";
	final String redirectUrl = endpoint + "?key=value";
	final String error = "test error message";
	
	@After
	public void no_more() {
		verifyNoMoreInteractions(openIdConsumer);
	}
	
	@Test
	public void form() {
		ModelAndView mav = controller.form();
		assertEquals("o/form", mav.getViewName());
	}

	@Test
	public void form_with_error() {
		ModelAndView mav = controller.form(error);
		assertEquals("o/form", mav.getViewName());
		assertEquals(error, mav.getModel().get("error"));
	}
	
	@Test
	public void authenticate() throws Exception {
		when(openIdConsumer.discover(identifier)).thenReturn(
				new DiscoveryInformation(new URL(endpoint)));
		when(openIdConsumer.performAuthRequest(any(DiscoveryInformation.class),
				any(URL.class))).thenReturn(redirectUrl);
		
		ModelAndView mav = controller.authenticate(request, identifier);
		RedirectView view = (RedirectView) mav.getView();
		
		assertEquals(redirectUrl, view.getUrl());
		
		verify(openIdConsumer, times(1)).discover(identifier);
		verify(openIdConsumer, times(1)).performAuthRequest(
				any(DiscoveryInformation.class), any(URL.class));
	}
	
	@Test
	public void authenticate_no_identifier() {
		ModelAndView mav = controller.authenticate(request, "");
		assertEquals(controller.NO_IDENTIFIER, mav.getModel().get("error"));
		assertEquals("o/form", mav.getViewName());
	}
	
	@Test
	public void authenticate_openid_error() throws Exception {
		final DiscoveryException e = new DiscoveryException(error);
		
		when(openIdConsumer.discover(identifier)).thenThrow(e);
		
		ModelAndView mav = controller.authenticate(request, identifier);
		assertEquals(e.getLocalizedMessage(), mav.getModel().get("error"));
		assertEquals("o/form", mav.getViewName());
		
		verify(openIdConsumer, times(1)).discover(identifier);
	}
	
	@Test
	public void authenticate_openid_error2() throws Exception {
		DiscoveryInformation d = new DiscoveryInformation(new URL(endpoint));
		ConsumerException e = new ConsumerException(error);
		
		when(openIdConsumer.discover(identifier)).thenReturn(d);
		when(openIdConsumer.performAuthRequest(any(DiscoveryInformation.class),
				any(URL.class))).thenThrow(e);
		
		ModelAndView mav = controller.authenticate(request, identifier);
		assertEquals(e.getLocalizedMessage(), mav.getModel().get("error"));
		assertEquals("o/form", mav.getViewName());
		
		verify(openIdConsumer, times(1)).discover(identifier);
		verify(openIdConsumer, times(1)).performAuthRequest(
				any(DiscoveryInformation.class), any(URL.class));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void validate() throws Exception {
		VerificationResult verifier = mock(VerificationResult.class);
		Identifier id = mock(Identifier.class);
		
		when(openIdConsumer.verifyResponse(any(HttpServletRequest.class),
				any(String.class))).thenReturn(verifier);
		when(verifier.getVerifiedId()).thenReturn(id);
		when(id.getIdentifier()).thenReturn(identifier);
		when(openIdConsumer.getFetchedAttributes(verifier)).thenReturn(
				Collections.EMPTY_MAP);
		
		ModelAndView mav = controller.validate(request);
		
		assertEquals("o/form", mav.getViewName());
		assertEquals(identifier, mav.getModel().get("id"));
		assertNotNull(mav.getModel().get("attributes"));
		
		verify(openIdConsumer, times(1)).verifyResponse(
				any(HttpServletRequest.class), any(String.class));
		verify(verifier, times(1)).getVerifiedId();
		verify(id, times(1)).getIdentifier();
		verify(openIdConsumer, times(1)).getFetchedAttributes(verifier);
	}
	
	@Test
	public void validate_null_id() throws Exception {
		VerificationResult verifier = mock(VerificationResult.class);
		
		when(openIdConsumer.verifyResponse(any(HttpServletRequest.class),
				any(String.class))).thenReturn(verifier);
		when(verifier.getVerifiedId()).thenReturn(null);
		
		ModelAndView mav = controller.validate(request);
		
		assertEquals("o/form", mav.getViewName());
		assertEquals(controller.UNVERIFIED, mav.getModel().get("error"));
		assertNull(mav.getModel().get("id"));
		assertNull(mav.getModel().get("attributes"));
		
		verify(openIdConsumer, times(1)).verifyResponse(
				any(HttpServletRequest.class), any(String.class));
		verify(verifier, times(1)).getVerifiedId();
	}
	
	@Test
	public void validate_openid_error() throws Exception {
		ConsumerException e = new ConsumerException(error);
		
		when(openIdConsumer.verifyResponse(any(HttpServletRequest.class),
				any(String.class))).thenThrow(e);
		
		ModelAndView mav = controller.validate(request);
		assertEquals(e.getLocalizedMessage(), mav.getModel().get("error"));
		assertEquals("o/form", mav.getViewName());
		
		verify(openIdConsumer, times(1)).verifyResponse(
				any(HttpServletRequest.class), any(String.class));
	}
}
