package press.whcj.ams.handler;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 *
 * if use WebMvcConfigurationSupport, this config disable
 *
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@SpringBootConfiguration
public class InterceptorConfig implements WebMvcConfigurer {
    @Resource
    private SecurityInterceptor securityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(securityInterceptor);
        interceptorRegistration.addPathPatterns("/**");
        interceptorRegistration.excludePathPatterns("/**/login*");
        interceptorRegistration.excludePathPatterns("/**/vpi/**");
    }
}
