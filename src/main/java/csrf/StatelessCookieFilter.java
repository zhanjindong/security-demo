package csrf;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatelessCookieFilter implements Filter {

	private final static Logger LOG = LoggerFactory.getLogger(StatelessCookieFilter.class);
	private final static Pattern COMMA = Pattern.compile(",");

	private String csrfTokenName;
	private String oncePerRequestAttributeName;
	private int cookieMaxAge;
	private Set<String> excludeURLs;
	private List<String> excludeStartWithURLs;
	private Set<String> excludeFormURLs;
	private Random random;

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) req;
		HttpServletResponse httpResp = (HttpServletResponse) resp;

		if (httpReq.getAttribute(oncePerRequestAttributeName) != null) {
			chain.doFilter(httpReq, httpResp);
		} else {
			httpReq.setAttribute(oncePerRequestAttributeName, Boolean.TRUE);
			try {
				doFilterInternal(httpReq, httpResp, chain);
			} finally {
				httpReq.removeAttribute(oncePerRequestAttributeName);
			}
		}
	}

	private void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		if (!req.getMethod().equals("POST")) {
			if (excludeFormURLs.contains(req.getServletPath())) {
				chain.doFilter(req, resp);
				return;
			}
			for (String curStart : excludeStartWithURLs) {
				if (req.getServletPath().startsWith(curStart)) {
					chain.doFilter(req, resp);
					return;
				}
			}
			String token = Long.toString(random.nextLong(), 36);
			LOG.debug("new csrf token generated: {} path: {}", token, req.getServletPath());
			req.setAttribute(csrfTokenName, token);
			Cookie cookie = new Cookie(csrfTokenName, token);
			cookie.setPath("/");
			cookie.setMaxAge(cookieMaxAge);
			resp.addCookie(cookie);
			chain.doFilter(req, resp);
			return;
		}

		if (excludeURLs.contains(req.getServletPath())) {
			chain.doFilter(req, resp);
			return;
		}

		String csrfToken = req.getParameter(csrfTokenName);
		if (csrfToken == null) {
			LOG.info("csrf token not found in POST request: {}", req.getServletPath());
			if (!resp.isCommitted()) {
				resp.sendError(400);
			}
			return;
		}
		req.setAttribute(csrfTokenName, csrfToken);

		for (Cookie curCookie : req.getCookies()) {
			if (curCookie.getName().equals(csrfTokenName)) {
				if (curCookie.getValue().equals(csrfToken)) {
					chain.doFilter(req, resp);
					return;
				} else {
					LOG.info("mismatched csrf token. expected: {} received: {} path: {}", new Object[] { csrfToken,
							curCookie.getValue(), req.getServletPath() });
					if (!resp.isCommitted()) {
						resp.sendError(400);
					}
					return;
				}
			}
		}

		LOG.info("csrf cookie not found at: {}", req.getServletPath());
		if (!resp.isCommitted()) {
			resp.sendError(400);
		}
	}

	public void destroy() {
		// do nothing
	}

	public void init(FilterConfig config) throws ServletException {
		String value = config.getInitParameter("csrfTokenName");
		if (value == null || value.trim().length() == 0) {
			throw new ServletException("csrfTokenName parameter should be specified");
		}
		csrfTokenName = value;
		String excludedURLsStr = config.getInitParameter("exclude");
		if (excludedURLsStr != null) {
			String[] parts = COMMA.split(excludedURLsStr);
			excludeURLs = new HashSet<String>(parts.length);
			for (String cur : parts) {
				excludeURLs.add(cur);
			}
		} else {
			excludeURLs = new HashSet<String>(0);
		}
		String excludedFormURLsStr = config.getInitParameter("excludeGET");
		if (excludedFormURLsStr != null) {
			String[] parts = COMMA.split(excludedFormURLsStr);
			excludeFormURLs = new HashSet<String>(parts.length);
			for (String cur : parts) {
				excludeFormURLs.add(cur.trim());
			}
		} else {
			excludeFormURLs = new HashSet<String>(0);
		}
		String excludeStartWithURLsStr = config.getInitParameter("excludeGETStartWith");
		if (excludeStartWithURLsStr != null) {
			String[] parts = COMMA.split(excludeStartWithURLsStr);
			excludeStartWithURLs = new ArrayList<String>(parts.length);
			for (String curPart : parts) {
				excludeStartWithURLs.add(curPart.trim());
			}
		} else {
			excludeStartWithURLs = new ArrayList<String>(0);
		}
		String cookieMaxAgeStr = config.getInitParameter("cookieMaxAge");
		if (cookieMaxAgeStr != null) {
			try {
				cookieMaxAge = Integer.parseInt(cookieMaxAgeStr);
			} catch (NumberFormatException nfe) {
				throw new ServletException("cookieMaxAge must be an integer: " + cookieMaxAgeStr, nfe);
			}
		} else {
			cookieMaxAge = 3600; // 60*60 seconds = 1 hour
		}
		oncePerRequestAttributeName = getFirstTimeAttributeName();
		random = new SecureRandom();
	}

	public static String getFirstTimeAttributeName() {
		return StatelessCookieFilter.class.getName() + ".ATTR";
	}

}
