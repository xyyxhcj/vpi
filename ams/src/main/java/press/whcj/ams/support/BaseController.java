package press.whcj.ams.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import press.whcj.ams.exception.ResultCode;
import press.whcj.ams.exception.ServiceException;
import press.whcj.ams.util.FastUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * BaseController
 *
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@RestControllerAdvice
public class BaseController {

	public final Logger logger;

	{
		this.logger = LoggerFactory.getLogger(this.getClass());
	}

	protected <T> Result<T> ok(T data) {
		return new Result<T>().ok(data);
	}

	protected <T> Result<T> ok() {
		return new Result<T>().ok();
	}

	protected <T> Result<T> ok(ResultCode resultCode, T data) {
		return new Result<>(resultCode, data);
	}

	public static HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
	}

	protected <T> Result<T> error(String msg) {
		return error(msg, ResultCode.BAD_REQUEST);
	}

	protected <T> Result<T> error(ResultCode resultCode) {
		throw new ServiceException(resultCode);
	}

	protected <T> Result<T> error(String msg, ResultCode resultCode) {
		throw new ServiceException(msg, resultCode);
	}

	protected <T> Result<T> result(ResultCode resultCode) {
		return new Result<>(resultCode);
	}

	protected <T> Result<T> confirm(int result) {
		return result > 0 ? ok() : error(ResultCode.OPERATION_FAILURE);
	}

	public <T> Result<T> error(ResultCode resultCode, T data) {
		throw new ServiceException(resultCode, data);
	}

	@ExceptionHandler(Exception.class)
	//@SuppressWarnings("uncheck")
	public Result<Object> handleException(Exception e, HttpServletRequest request) {
		Result<Object> result;
		if (e instanceof HttpMessageNotReadableException || e instanceof HttpMediaTypeNotSupportedException) {
			result = Result.fail(ResultCode.PARAMS_NOT);
		} else if (e instanceof ServiceException) {
			ServiceException se = (ServiceException) e;
			result = Result.fail(se.code, se.message, se.data);
		} else {
			result = Result.error(ResultCode.INTERNAL_SERVER_ERROR);
		}
		logger.error("req: {} error，code: {}，errorMsg: {}", request.getRequestURI(), result.getCode(), result.getMessage(), e);
		FastUtils.loggerRequestBody(logger, request);
		return result;
	}
}
