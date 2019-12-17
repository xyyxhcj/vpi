package press.whcj.ams.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * get profile config
 *
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "system.properties")
public class YmlProperties {

	/**
	 * profile  dev/test/prod
	 */
	private String profileActive;
}
