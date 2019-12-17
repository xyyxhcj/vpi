package press.whcj.ams.handler;

import org.springframework.http.MediaType;
import org.springframework.session.data.mongo.MongoOperationsSessionRepository;
import org.springframework.session.data.mongo.MongoSession;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import press.whcj.ams.common.Constant;
import press.whcj.ams.exception.ResultCode;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.JsonUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Component
public class SecurityInterceptor extends HandlerInterceptorAdapter {
	@Resource
	private MongoOperationsSessionRepository sessionRepository;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (Constant.PropertyName.OPTIONS.equalsIgnoreCase(request.getMethod())) {
			return true;
		}
		String userToken = request.getHeader(Constant.SysConfig.AUTH_HEADER);
		if (!StringUtils.isEmpty(userToken)) {
			MongoSession session = sessionRepository.findById(userToken);
			if (session != null && !session.isExpired()) {
				return true;
			}
		}
		setResponse(response);
		return false;
	}

	private void setResponse(HttpServletResponse response) throws IOException {
		String result = JsonUtils.object2Str(JsonUtils.NON_NULL_MAPPER, new Result(Constant.ReqResult.FAIL, ResultCode.USER_TOKEN_EXPIRED));
		response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		response.getWriter().print(result);
	}
}
