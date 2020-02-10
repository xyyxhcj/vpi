package press.whcj.ams.web;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import press.whcj.ams.entity.ApiEnv;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.service.ApiEnvService;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.UserUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@RestController
@RequestMapping("apiEnv")
public class ApiEnvController extends BaseController {
    @Resource
    private ApiEnvService apiEnvService;

    @RequestMapping("add")
    public Result<String> add(@RequestBody ApiEnv apiEnvDto) {
        UserVo operator = UserUtils.getOperator();
        apiEnvDto.initCreate(operator);
        String id = apiEnvService.save(apiEnvDto, operator);
        return ok(id);
    }

    @RequestMapping("edit")
    public Result<String> edit(@RequestBody ApiEnv apiEnvDto) {
        FastUtils.checkParams(apiEnvDto.getId());
        UserVo operator = UserUtils.getOperator();
        apiEnvDto.initUpdate(operator);
        apiEnvService.save(apiEnvDto, operator);
        return ok(apiEnvDto.getId());
    }

    @RequestMapping("delete")
    public Result<String> delete(@RequestBody ApiEnv apiEnvDto) {
        apiEnvService.delete(apiEnvDto, UserUtils.getOperator());
        return ok();
    }

    @RequestMapping("findList")
    public Result<List<ApiEnv>> findList(@RequestBody ApiEnv apiEnvDto) {
        return ok(apiEnvService.findList(apiEnvDto));
    }
}
