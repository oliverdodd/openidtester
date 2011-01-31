package net._01001111.openidtester.util;

import javax.servlet.http.HttpServletRequest;

public class HttpRequestUtil {

	public String getBaseUrl(HttpServletRequest r) {
		StringBuilder s = new StringBuilder(r.getScheme()).append("://")
				.append(r.getServerName());
		if ((r.getServerPort() != 80) && (r.getServerPort() != 443))
			s.append(":").append(r.getServerPort());
		s.append(r.getContextPath());
		return s.toString();

	}

}
