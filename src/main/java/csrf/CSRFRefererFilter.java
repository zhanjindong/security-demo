package csrf;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.owasp.esapi.ESAPI;

/**
 * 
 * 简单的通过Referer进行CSRF过滤。
 * 
 * @author jdzhan,2014-11-6
 * 
 */
public class CSRFRefererFilter implements Filter {

	private static String[] refererPrefixs;

	public void init(FilterConfig filterConfig) throws ServletException {
		String value = filterConfig.getInitParameter("refererPrefix");
		refererPrefixs = value.split(",");
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		// 从 HTTP 头中取得 Referer 值
		String referer = ((HttpServletRequest) request).getHeader("Referer");
		// 判断 Referer 是否是信任的地址
		if ((referer != null) && startWithPrefix(referer.trim())) {
			chain.doFilter(request, response);
		} else {
			request.getRequestDispatcher("error.jsp?name=CSRF").forward(request, response);
		}
	}

	private boolean startWithPrefix(String referer) {
		for (int i = 0; i < refererPrefixs.length; i++) {
			if (referer.startsWith(refererPrefixs[i])) {
				return true;
			}
		}
		return false;
	}

	public void destroy() {
	}
}
