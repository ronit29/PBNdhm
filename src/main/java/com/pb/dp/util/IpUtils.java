package com.pb.dp.util;

import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.text.StrTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Utility class to extract client IP address of the client
 * 
 * @author Ranjeet Singh Yadav
 */
public class IpUtils {

	private final Logger logger = LoggerFactory.getLogger(IpUtils.class);

	public static final String _255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	public static final Pattern pattern = Pattern.compile("^(?:" + _255 + "\\.){3}" + _255 + "$");

	/**
	 * Gets IP address of the client which is consuming web service
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return IP address as string
	 */
	public static String getIpFromRequest(HttpServletRequest request) {
		boolean found = false;
		String ip = getIpTrace(request);
		if (ip != null) {
			StrTokenizer tokenizer = new StrTokenizer(ip, ",");
			while (tokenizer.hasNext()) {
				ip = tokenizer.nextToken().trim();
				if (isIPv4Valid(ip) && !isIPv4Private(ip)) {
					found = true;
					break;
				}
			}
		}
		if (!found) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static String longToIpV4(long longIp) {
		int octet3 = (int) ((longIp >> 24) % 256);
		int octet2 = (int) ((longIp >> 16) % 256);
		int octet1 = (int) ((longIp >> 8) % 256);
		int octet0 = (int) ((longIp) % 256);
		return octet3 + "." + octet2 + "." + octet1 + "." + octet0;
	}

	public static long ipV4ToLong(String ip) {
		String[] octets = ip.split("\\.");
		return (Long.parseLong(octets[0]) << 24) + (Integer.parseInt(octets[1]) << 16) + (Integer.parseInt(octets[2]) << 8) + Integer.parseInt(octets[3]);
	}

	public static boolean isIPv4Private(String ip) {
		long longIp = ipV4ToLong(ip);
		return (longIp >= ipV4ToLong("10.0.0.0") && longIp <= ipV4ToLong("10.255.255.255"))
				|| (longIp >= ipV4ToLong("172.16.0.0") && longIp <= ipV4ToLong("172.31.255.255")) || longIp >= ipV4ToLong("192.168.0.0")
				&& longIp <= ipV4ToLong("192.168.255.255");
	}

	public static boolean isIPv4Valid(String ip) {
		return pattern.matcher(ip).matches();
	}

	public static String getIpFromRequest(ServletRequest request) {
		boolean found = false;
		String ip = getIpTrace(request);
		if (ip != null) {
			StrTokenizer tokenizer = new StrTokenizer(ip, ",");
			while (tokenizer.hasNext()) {
				ip = tokenizer.nextToken().trim();
				if (isIPv4Valid(ip) && !isIPv4Private(ip)) {
					found = true;
					break;
				}
			}
		}
		if (!found) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static String getRequestUrlWithQueryString(ServletRequest request) {
		HttpServletRequest req = (HttpServletRequest) request;
		String uri = req.getScheme()
				+ "://"
				+ req.getServerName()
				+ ("http".equals(req.getScheme()) && req.getServerPort() == 80 || "https".equals(req.getScheme()) && req.getServerPort() == 443 ? "" : ":"
						+ req.getServerPort()) + req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
		return uri;
	}

	public static String getIpTrace(ServletRequest request) {
		HttpServletRequest req = (HttpServletRequest) request;
		String ipTrace = req.getHeader("x-forwarded-for");
		if (StringUtils.isEmpty(ipTrace)) {
			ipTrace = req.getHeader("X-FORWARDED-FOR");
		}
		if (StringUtils.isEmpty(ipTrace)) {
			ipTrace = req.getHeader("X-Forwarded-For");
		}
		if (StringUtils.isEmpty(ipTrace)) {
			ipTrace = req.getRemoteAddr();
		}
		return (StringUtils.isEmpty(ipTrace) ? "NA" : ipTrace);
	}

	public static String getRequestUrlWithIpTrace(ServletRequest request) {
		return "Request Details.. CS Node-[LocalAddr()=" + request.getLocalAddr() + ",LocalName()=" + request.getLocalName() + "], Client IP Trace- ["
				+ getIpTrace(request) + "], Requested URL- " + getRequestUrlWithQueryString(request);
	}

}