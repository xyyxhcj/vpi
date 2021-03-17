package press.whcj.ams.common;

/**
 * Constant
 *
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
public interface Constant {

	/**
	 * profile
	 */
	interface ProfileActive {
		String DEV = "dev";
		String TEST = "test";
		String PROD = "prod";
	}

	/**
	 * character
	 */
	interface Character {
		String QUESTION = "?";
		String EQUALS = "=";
		String AND = "&";
		String COLON = ":";
		String ASTERISK = "*";
		String POINT = ".";
		String COMMA = ",";
		String BRACKET_LEFT_B = "{";
		String NULL_VALUE = "";
		String UNDER_LINE = "_";
		String VIRGULE = "/";
	}

	/**
	 * result for request
	 */
	interface ReqResult {

		String SUCCESS = "success";
		String FAIL = "fail";
		String ERROR = "error";
	}

	/**
	 * system config
	 */
	interface SysConfig {
		int PAGE_BEGIN = 1;
		int PAGE_SIZE = 20;
		String ADMIN_LOGIN_NAME = "admin";
		String ADMIN_PWD = "xyyxhcj@qq.com";
		String SALT = "v2Ll6EmXPQIf";
		String AUTH_HEADER = "auth";
		int SESSION_TIME_OUT = 60 * 60 * 2;
		String FRONT_HOST = "http://42.192.227.159";
	}

	interface Url {
		String EXPORT_URL = "/exportDoc.html?projectId=%s&projectName=%s&envId=%s";
	}

	/**
	 * session key
	 */
	interface SessionKey {
		String USER_INFO = "user_info";
	}

	/**
	 * property name
	 */
	interface PropertyName {
		String OPTIONS = "options";
	}

	/**
	 * yes or no
	 */
	interface Is {
		Byte YES = 1;
		Byte NO = 0;
	}

	interface CollectionName {
		String USER = "user";
		String PROJECT_USER = "projectUser";
		String PROJECT_GROUP = "projectGroup";
		String API_GROUP = "apiGroup";
		String STRUCTURE = "structure";
		String STRUCTURE_DATA = "structureData";
		String API = "api";
		String API_TEST_CASE = "apiTestCase";
	}

	/**
	 * 0-creator 1-admin 2-readAndWrite 3-read
	 */
	interface UserType {
		Byte CREATOR = 0;
		Byte ADMIN = 1;
		Byte WRITE = 2;
		Byte READ = 3;
	}

	/**
	 * 0-apiCreate 1-userCreate
	 **/
	interface StructureType {
		Byte API_CREATE = 0;
		Byte USER_CREATE = 1;
	}
}
