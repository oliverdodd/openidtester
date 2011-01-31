package net._01001111.openidtester.webapp.mapper;

import javax.servlet.http.HttpServletRequest;

import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper;

public class NoDecoratorMapper extends AbstractDecoratorMapper {

	public Decorator getDecorator(HttpServletRequest request, Page page) {
		if (request.getHeader("X-Requested-With") != null) {
			return null;
		} else {
			return super.getDecorator(request, page);
		}
	}

}