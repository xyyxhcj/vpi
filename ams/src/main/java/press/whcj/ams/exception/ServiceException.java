package press.whcj.ams.exception;

/**
 * service exception
 *
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = -1L;
	public final int code;
	public final String message;
	public Object data;

	public ServiceException(String message, ResultCode resultCode) {
		super(message);
		this.code = resultCode.code;
		this.message = message;
	}

	public ServiceException(String message, ResultCode resultCode, Object data) {
		this(message, resultCode);
		this.data = data;
	}

	public ServiceException(ResultCode resultCode) {
		this(resultCode.message, resultCode);
	}

	public ServiceException(ResultCode resultCode, Object data) {
		this(resultCode.message, resultCode, data);
	}
}
