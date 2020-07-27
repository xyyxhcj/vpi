package press.whcj.ams.web;

import java.time.LocalDateTime;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import press.whcj.ams.entity.ApiTestHistory;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.User;
import press.whcj.ams.entity.dto.ApiTestHistoryDTO;
import press.whcj.ams.entity.vo.UserVO;
import press.whcj.ams.service.ApiTestHistoryService;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.UserUtils;

/**
 * @author xyyxhcj@qq.com
 * @since 2020/02/06
 */
@RestController
@RequestMapping("apiTestHistory")
public class ApiTestHistoryController extends BaseController {
    @Resource
    private ApiTestHistoryService apiTestHistoryService;
    @PostMapping("add")
    public Result<String> add(@RequestBody ApiTestHistory apiTestHistory) {
        UserVO operator = UserUtils.getOperator();
        LocalDateTime now = LocalDateTime.now();
        apiTestHistory.setCreate(new User(operator.getId()));
        apiTestHistory.setCreateTime(now);
        String id = apiTestHistoryService.save(apiTestHistory, operator);
        return ok(id);
    }

    @PostMapping("delete")
    public Result<Object> delete(@RequestBody ApiTestHistoryDTO apiTestHistoryDTO) {
        apiTestHistoryService.delete(apiTestHistoryDTO);
        return ok();
    }

    @PostMapping("findPage")
    public Result<MongoPage<ApiTestHistory>> findPage(@RequestBody ApiTestHistoryDTO apiTestHistoryDTO) {
        return ok(apiTestHistoryService.findPage(apiTestHistoryDTO));
    }
}
