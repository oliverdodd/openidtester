package net._01001111.openidtester.webapp.controller;

public class UrlFilenameViewController extends
		org.springframework.web.servlet.mvc.UrlFilenameViewController {
	private String welcomeFile = "index.html";

	public void setWelcomeFile(String welcomeFile) {
		this.welcomeFile = welcomeFile;
	}

	public String getWelcomeFile() {
		return welcomeFile;
	}

	@Override
	protected String getViewNameForUrlPath(String uri) {
		if (uri.isEmpty() || uri.equals("/")) {
			uri += getWelcomeFile();
		}
		return super.getViewNameForUrlPath(uri);
	}
}
