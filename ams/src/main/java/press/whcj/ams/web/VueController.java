package press.whcj.ams.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 处理vue项目的转发，替代nginx
 * @author xyyxhcj@qq.com
 * @since 2021/07/28
 */
@Controller
public class VueController {
    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }
}
