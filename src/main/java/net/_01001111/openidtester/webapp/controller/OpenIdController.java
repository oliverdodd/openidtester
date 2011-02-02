package net._01001111.openidtester.webapp.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net._01001111.openidtester.openid.OpenIdConsumer;
import net._01001111.openidtester.util.HttpRequestUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("o")
public class OpenIdController {

	private transient final Log log = LogFactory.getLog(getClass());
	private HttpRequestUtil requestUtil = new HttpRequestUtil();

	private OpenIdConsumer openIdConsumer;
	
	protected final String UNVERIFIED = "could not verify id";
	protected final String NO_IDENTIFIER = "no identifier";
	
	@Autowired
	public OpenIdController(OpenIdConsumer openIdConsumer) {
		this.openIdConsumer = openIdConsumer;
	}

	@RequestMapping(value = "")
	public ModelAndView form() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("o/form");
		return mav;
	}

	public ModelAndView form(String error) {
		ModelAndView mav = form();
		mav.addObject("error", error);
		return mav;
	}

	@RequestMapping(value = "/a")
	public ModelAndView authenticate(HttpServletRequest request,
			@RequestParam(required = false) String identifier) {
		if (identifier == null || identifier.isEmpty()) {
			return form(NO_IDENTIFIER);
		}
		try {
			// TODO: refactor into a single call
			DiscoveryInformation d = openIdConsumer.discover(identifier);
			String redirectUrl = openIdConsumer.performAuthRequest(d, 
					getCallbackURL(request));
			return new ModelAndView(new RedirectView(redirectUrl));
		} catch (Exception e) {
			return form(e.getLocalizedMessage());
		}
	}

	@RequestMapping(value = "/v")
	public ModelAndView validate(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		ModelAndView mav = new ModelAndView();
		try {
			// TODO: refactor into a call that returns a tuple
			VerificationResult verification = openIdConsumer.verifyResponse(
					request, url);
			Identifier identifier = verification.getVerifiedId();

			if (identifier != null) {
				String id = identifier.getIdentifier();
				Map<String, String> attributes = openIdConsumer
						.getFetchedAttributes(verification);
				
				if (log.isDebugEnabled()) {
					log.debug("Validated id: "+ id);
					log.debug("With attributes: ");
					for (Map.Entry<String, String> e : attributes.entrySet()) {
						log.debug(e.getKey() + " : " + e.getValue());
					}
				}
				
				mav.addObject("id", id);
				mav.addObject("attributes", attributes);
			} else {
				mav.addObject("error", UNVERIFIED);
			}
		} catch (OpenIDException e) {
			mav.addObject("error", e.getLocalizedMessage());
		}
		mav.setViewName("o/form");
		return mav;
	}
	
	private URL getCallbackURL(HttpServletRequest request)
			throws MalformedURLException {
		return new URL(requestUtil.getBaseUrl(request) + "/o/v");
	}
}
