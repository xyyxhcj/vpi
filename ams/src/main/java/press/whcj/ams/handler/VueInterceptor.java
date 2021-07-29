package press.whcj.ams.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * vue config
 * @author xyyxhcj@qq.com
 * @since 2021/07/28
 */

@Component
public class VueInterceptor  extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.getRequestDispatcher(request.getRequestURI().replaceFirst("/vpi", "")).forward(request, response);
        return false;
    }
}
