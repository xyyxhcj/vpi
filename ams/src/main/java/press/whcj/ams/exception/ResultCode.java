package press.whcj.ams.exception;

import lombok.Getter;

/**
 * request result code
 *
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
public enum ResultCode {

    /* ============================ common ========================== **/
    PARAMS_NOT(303, "missing parameter"),
    PARAMS_ERROR(304, "parameter error"),
    BAD_REQUEST(400, "bad request"),
    UNAUTHORIZED(401, "without authorization"),
    FORBIDDEN(403, "access forbidden"),
    TIME_OUT(408, "connection timeout"),
    INTERNAL_SERVER_ERROR(500, "abnormal operation"),
    OPERATION_FAILURE(507, "operation failure"),

    /* ============================ business code ========================== **/
    RECORD_NOT_EXIST(10001, "record not exist"),
    LOGIN_NAME_EXIST(10002, "loginName exist"),
    PHONE_EXIST(10003, "phone exist"),
    LOGIN_NAME_OR_PASSWORD_ERROR(10003, "loginName or password error"),
    USER_TOKEN_EXPIRED(10004, "user login has expired"),
    PERMISSION_DENIED(10005, "permission denied"),
    NAME_EXIST(10006, "name exist"),
    OLD_PWD_ERROR(10007, "old password error"),
    STRUCTURE_USED(10008, "structure has been used"),
    ;
    public final int code;
    public String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
