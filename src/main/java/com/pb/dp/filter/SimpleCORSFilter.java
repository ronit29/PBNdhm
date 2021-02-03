package com.pb.dp.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.pb.dp.util.IpUtils;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCORSFilter implements Filter {

	private final Logger logger = LoggerFactory.getLogger(SimpleCORSFilter.class);

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		  HttpServletRequest request = (HttpServletRequest) req;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET,DELETE,PUT");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with, content-type, authorization, content-length,clientKey,x-documentid,x-leadid,x-docname,x-filetype,x-source,x-doctype,x-claimapi,x-claimid,x-productid,x-uploadedbycustomer,x-memberid,x-statusid,x-doccategoryid,x-bookingid");
		if (!(request.getMethod().equalsIgnoreCase("OPTIONS"))) {
	          try {
	              chain.doFilter(req, res);
	          } catch(Exception e) {
	              e.printStackTrace();
	          }
	      } else {
	          response.setHeader("Access-Control-Allow-Origin", "*");
	          response.setHeader("Access-Control-Allow-Methods", "POST,GET,DELETE,PUT");
	          response.setHeader("Access-Control-Max-Age", "3600");
	          response.setHeader("Access-Control-Allow-Headers", "x-requested-with, content-type, authorization, content-length,clientKey,x-documentid,x-leadid,x-docname,x-filetype,x-source,x-doctype,x-claimapi,x-claimid,x-productid,x-uploadedbycustomer,x-memberid,x-statusid,x-doccategoryid,x-bookingid");
	          response.setStatus(HttpServletResponse.SC_OK);
	      }

		String requestDetails = IpUtils.getRequestUrlWithIpTrace(req);
		MDC.put("clientIP", requestDetails);
		logger.debug(requestDetails);
		//chain.doFilter(req, res);
	}

	public void init(FilterConfig filterConfig) {
	}

	public void destroy() {
	}

}
