package press.whcj.ams.web;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import press.whcj.ams.entity.ApiTestHistory;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.User;
import press.whcj.ams.entity.dto.ApiTestHistoryDto;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.service.ApiTestHistoryService;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.UserUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author xyyxhcj@qq.com
 * @since 2020/02/06
 */
@RestController
@RequestMapping("apiTestHistory")
public class ApiTestHistoryController extends BaseController {
    @Resource
    private ApiTestHistoryService apiTestHistoryService;
    @RequestMapping("add")
    public Result<String> add(@RequestBody ApiTestHistory apiTestHistory) {
        UserVo operator = UserUtils.getOperator();
        LocalDateTime now = LocalDateTime.now();
        apiTestHistory.setCreate(new User(operator.getId()));
        apiTestHistory.setCreateTime(now);
        String id = apiTestHistoryService.save(apiTestHistory, operator);
        return ok(id);
    }

    @RequestMapping("findPage")
    public Result<MongoPage<ApiTestHistory>> findPage(@RequestBody ApiTestHistoryDto apiTestHistoryDto) {
        return ok(apiTestHistoryService.findPage(apiTestHistoryDto));
    }
}
