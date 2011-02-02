package net._01001111.openidtester.webapp.controller;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class UrlFilenameViewControllerTest {
	
	UrlFilenameViewController controller = new UrlFilenameViewController();
	
	String welcomeView = "welcome";
	String welcomeFile = welcomeView + ".html";
	
	@Before
	public void setup() {
		controller.setWelcomeFile(welcomeFile);
	}
	
	@Test
	public void getViewNameForUrlPath_welcome() {
		Assert.assertEquals(welcomeView, controller
				.getViewNameForUrlPath(""));
	}
	
	@Test
	public void getViewNameForUrlPath_welcome2() {
		Assert.assertEquals(welcomeView, controller
				.getViewNameForUrlPath("/"));
	}
	
	@Test
	public void getViewNameForUrlPath_no_welcome() {
		Assert.assertEquals("some/path/", controller
				.getViewNameForUrlPath("some/path/"));
	}
	
	@Test
	public void getViewNameForUrlPath_no_welcome3() {
		Assert.assertEquals("some/path/view", controller
				.getViewNameForUrlPath("some/path/view.html"));
	}
}
