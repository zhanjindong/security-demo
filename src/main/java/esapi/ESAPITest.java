package esapi;

import org.owasp.esapi.ESAPI;

public class ESAPITest {

	public static void main(String[] args) {
		
		ESAPI.authenticator();// 身份认证安全组件
		ESAPI.accessController();// 资源授权安全组件
		ESAPI.encoder();// 安全编码
		ESAPI.encryptor();// 安全加解密
		ESAPI.validator();// 数据校验
		ESAPI.httpUtilities();// HTTP安全
		ESAPI.randomizer();// 随机数(int,String,GUID...)
		ESAPI.log();// 安全日志组件
		ESAPI.executor();// 安全的执行系统命令
		ESAPI.intrusionDetector();// 攻击检测
		ESAPI.securityConfiguration();// 安全配置
		
	}

}
