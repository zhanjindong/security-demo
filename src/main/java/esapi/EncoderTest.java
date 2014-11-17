package esapi;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;

public class EncoderTest {

	public static void main(String[] args) throws EncodingException {
		String input = "http://www.baidu.com?t=123";
		String result = ESAPI.encoder().encodeForURL(input);
		System.out.println(input);
	}

}
