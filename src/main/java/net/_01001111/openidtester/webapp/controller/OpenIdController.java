package net._01001111.openidtester.webapp.controller;

import java.net.URL;

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
@RequestMapping(value = "o")
public class OpenIdController {

	private transient final Log log = LogFactory.getLog(getClass());
	private HttpRequestUtil requestUtil = new HttpRequestUtil();

	@Autowired
	private OpenIdConsumer openIdConsumer;

	@RequestMapping(value = "/")
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
			@RequestParam String identifier) {
		if (identifier == null) {
			return form("no identifier");
		}
		try {
			DiscoveryInformation d = openIdConsumer.discover(identifier);
			String redirectUrl = openIdConsumer.performAuthRequest(d, new URL(
					requestUtil.getBaseUrl(request)));
			return new ModelAndView(new RedirectView(redirectUrl));
		} catch (Exception e) {
			return form(e.getLocalizedMessage());
		}
	}

	@RequestMapping(value = "/c")
	public ModelAndView complete(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		ModelAndView mav = new ModelAndView();
		try {
			VerificationResult verification = openIdConsumer.verifyResponse(
					request, url);
			Identifier identifier = verification.getVerifiedId();

			if (identifier != null) {
				mav.addObject("id", identifier.getIdentifier());
				mav.addObject("attributes", openIdConsumer
						.getFetchedAttributes(verification));
			}
		} catch (OpenIDException e) {
			mav.addObject("error", e.getLocalizedMessage());
		}
		mav.setViewName("o/form");
		return mav;
	}

}
