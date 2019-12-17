package press.whcj.ams.support;

import lombok.Getter;
import lombok.Setter;
import press.whcj.ams.common.Constant;
import press.whcj.ams.exception.ResultCode;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
public class Result<T> {
	private static final int SUCCESS = 200;
	/**
	 * success/fail/error
	 */
	private final static String SUCCESS_STR = "success";
	private String status;
	private int code;
	private T data;
	private String message;

	public Result() {
	}

	public Result(ResultCode resultCode) {
		this.status = SUCCESS_STR;
		this.code = resultCode.code;
		this.message = resultCode.message;
	}

	public Result(ResultCode resultCode, T data) {
		this.code = resultCode.code;
		this.message = resultCode.message;
		this.data = data;
	}

	public Result(ResultCode resultCode, String message, T data) {
		this.code = resultCode.code;
		this.message = message;
		this.data = data;
	}

	public Result(String status, int code, String message, T data) {
		this.status = status;
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public Result(String status, ResultCode resultCode) {
		this.status = status;
		this.code = resultCode.code;
		this.message = resultCode.message;
	}

	public Result<T> ok(T data) {
		this.code = SUCCESS;
		this.data = data;
		return this;
	}

	public Result<T> ok() {
		this.code = SUCCESS;
		return this;
	}

	public static <T> Result<T> fail(ResultCode resultCode) {
		return new Result<>(Constant.ReqResult.FAIL, resultCode);
	}

	public static <T> Result<T> fail(int code, String message, T data) {
		return new Result<>(Constant.ReqResult.FAIL, code, message, data);
	}

	public static <T> Result<T> error(ResultCode resultCode) {
		return new Result<>(Constant.ReqResult.ERROR, resultCode);
	}
}
