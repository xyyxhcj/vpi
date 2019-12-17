package press.whcj.ams.support;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import press.whcj.ams.service.UserService;

import javax.annotation.Resource;

/**
 * system init
 *
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Component
public class InitCache implements CommandLineRunner {
	@Resource
	private UserService userService;
	@Override
	public void run(String... args) {
		System.out.println(">>>>>>>>>>>>>>> system init start <<<<<<<<<<<<<");
		userService.init();
		System.out.println(">>>>>>>>>>>>>>> system init end <<<<<<<<<<<<<");
	}
}
