package press.whcj.ams.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import press.whcj.ams.entity.ApiEnv;
import press.whcj.ams.entity.vo.UserVO;
import press.whcj.ams.service.ApiEnvService;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.UserUtils;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@RestController
@RequestMapping("apiEnv")
public class ApiEnvController extends BaseController {
    @Resource
    private ApiEnvService apiEnvService;

    @PostMapping("add")
    public Result<String> add(@RequestBody ApiEnv apiEnvDTO) {
        UserVO operator = UserUtils.getOperator();
        apiEnvDTO.initCreate(operator);
        String id = apiEnvService.save(apiEnvDTO, operator);
        return ok(id);
    }

    @PostMapping("edit")
    public Result<String> edit(@RequestBody ApiEnv apiEnvDTO) {
        FastUtils.checkParams(apiEnvDTO.getId());
        UserVO operator = UserUtils.getOperator();
        apiEnvDTO.initUpdate(operator);
        apiEnvService.save(apiEnvDTO, operator);
        return ok(apiEnvDTO.getId());
    }

    @PostMapping("delete")
    public Result<String> delete(@RequestBody ApiEnv apiEnvDTO) {
        apiEnvService.delete(apiEnvDTO, UserUtils.getOperator());
        return ok();
    }

    @PostMapping("findList")
    public Result<List<ApiEnv>> findList(@RequestBody ApiEnv apiEnvDTO) {
        return ok(apiEnvService.findList(apiEnvDTO));
    }
}
