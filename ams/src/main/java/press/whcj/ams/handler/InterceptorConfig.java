package press.whcj.ams.handler;

import javax.annotation.Resource;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
    @Resource
    private VueInterceptor vueInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration vueRegistration = registry.addInterceptor(vueInterceptor);
        vueRegistration.addPathPatterns("/vpi/**");

        InterceptorRegistration interceptorRegistration = registry.addInterceptor(securityInterceptor);
        interceptorRegistration.addPathPatterns("/**");
        interceptorRegistration.excludePathPatterns("/**/login*");
        interceptorRegistration.excludePathPatterns("/**/vpi/**");
        interceptorRegistration.excludePathPatterns("/");
        interceptorRegistration.excludePathPatterns("/index");
        interceptorRegistration.excludePathPatterns("/*.html");
        interceptorRegistration.excludePathPatterns("/**/*.js");
        interceptorRegistration.excludePathPatterns("/**/*.css");
        interceptorRegistration.excludePathPatterns("/**/*.gif");
        interceptorRegistration.excludePathPatterns("/**/*.png");
        interceptorRegistration.excludePathPatterns("/**/*.ico");
    }
}
