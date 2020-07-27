package press.whcj.ams.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import press.whcj.ams.entity.ApiGroup;
import press.whcj.ams.entity.vo.ApiGroupVO;
import press.whcj.ams.entity.vo.UserVO;
import press.whcj.ams.service.ApiGroupService;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.UserUtils;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@RestController
@RequestMapping("apiGroup")
public class ApiGroupController extends BaseController {
    @Resource
    private ApiGroupService apiGroupService;

    @PostMapping("add")
    public Result<String> add(@RequestBody ApiGroup apiGroupDTO) {
        UserVO operator = UserUtils.getOperator();
        apiGroupDTO.initCreate(operator);
        String id = apiGroupService.save(apiGroupDTO, operator);
        return ok(id);
    }

    @PostMapping("edit")
    public Result<String> edit(@RequestBody ApiGroup apiGroupDTO) {
        FastUtils.checkParams(apiGroupDTO.getId());
        UserVO operator = UserUtils.getOperator();
        apiGroupDTO.initUpdate(operator);
        apiGroupService.save(apiGroupDTO, operator);
        return ok(apiGroupDTO.getId());
    }

    @PostMapping("vpi/findList")
    public Result<List<ApiGroupVO>> findList(@RequestBody ApiGroup apiGroupDTO) {
        return ok(apiGroupService.findList(apiGroupDTO));
    }

    @PostMapping("delete")
    public Result<Object> delete(@RequestBody ApiGroup apiGroupDTO) {
		apiGroupService.delete(apiGroupDTO);
        return ok();
    }
}
