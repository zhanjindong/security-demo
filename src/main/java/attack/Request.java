package attack;

import java.io.IOException;

import org.apache.http.HttpResponse;

import com.iflytek.ossp.framework.common.httpclient.HttpHelper;
import com.iflytek.ossp.framework.common.httpclient.UrlConnectionHelper;

public class Request {

	private static final String URL = "http://localhost:8080/security-demo/dos.html";

	public static void main(String[] args) throws IOException {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < 1; i++) {
			UrlConnectionHelper.get(URL, 3000, "UTF-8", result);
			System.out.println(result);
			result.delete(0, result.capacity());
		}

		HttpHelper helper = new HttpHelper(5000);
		helper.get(URL, result, "utf-8");
		System.out.println(result);

		HttpResponse resp=helper.get(URL, "utf-8", null);
	}

}
