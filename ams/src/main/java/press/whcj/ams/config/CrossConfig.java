package press.whcj.ams.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * cross domain config
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Configuration
public class CrossConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // allow domain
        corsConfiguration.addAllowedOrigin("*");
        // allow header
        corsConfiguration.addAllowedHeader("*");
        // allow method
        corsConfiguration.addAllowedMethod("*");
        // path config
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}
