package csrf;

import java.io.IOException;
import java.util.Random;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.owasp.esapi.ESAPI;

/**
 * 
 * 通过简单的全局Seesion token进行用户身份验证。
 * 
 * @author jdzhan,2014-11-6
 * 
 */
public class CSRFTokenFilter implements Filter {
	
	private static Random random = new Random();

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession s = req.getSession();

		// 从 session 中得到 csrftoken 属性
		String sToken = (String) s.getAttribute("csrftoken");
		if (sToken == null) {

			// 产生新的 token 放入 session 中
			sToken = generateToken();
			s.setAttribute("csrftoken", sToken);
			System.out.println(sToken);
			chain.doFilter(request, response);
		} else {

			// 从 HTTP 头中取得 csrftoken
			String xhrToken = req.getHeader("csrftoken");

			// 从请求参数中取得 csrftoken
			String pToken = req.getParameter("csrftoken");
			if (sToken != null && xhrToken != null && sToken.equals(xhrToken)) {
				chain.doFilter(request, response);
			} else if (sToken != null && pToken != null && sToken.equals(pToken)) {
				chain.doFilter(request, response);
			} else {
				request.getRequestDispatcher("error.jsp?name=CSRF").forward(request, response);
			}
		}

	}

	private String generateToken() {
		// TODO:this just test
		return String.valueOf(random.nextInt(100));
	}

	public void destroy() {
	}
}
