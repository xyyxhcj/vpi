package press.whcj.ams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import press.whcj.ams.config.MongoPoolProperties;
import press.whcj.ams.config.YmlProperties;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@SpringBootApplication
@EnableConfigurationProperties({YmlProperties.class, MongoPoolProperties.class})
public class AmsApplication {
	public static void main(String[] args) {
		SpringApplication.run(AmsApplication.class, args);
	}
}
