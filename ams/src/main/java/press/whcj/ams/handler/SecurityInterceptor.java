package press.whcj.ams.handler;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.session.data.mongo.MongoIndexedSessionRepository;
import org.springframework.session.data.mongo.MongoSession;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import press.whcj.ams.common.Constant;
import press.whcj.ams.exception.ResultCode;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.JsonUtils;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Component
public class SecurityInterceptor extends HandlerInterceptorAdapter {
    @Resource
    private MongoIndexedSessionRepository sessionRepository;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)||"GET".equals(method)) {
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
        String result = JsonUtils.object2Str(JsonUtils.NON_NULL_MAPPER, new Result<>(Constant.ReqResult.FAIL, ResultCode.USER_TOKEN_EXPIRED));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().print(result);
    }
}
