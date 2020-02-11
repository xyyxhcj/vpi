package press.whcj.ams.web;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import press.whcj.ams.entity.ApiGroup;
import press.whcj.ams.entity.vo.ApiGroupVo;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.service.ApiGroupService;
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
@RequestMapping("apiGroup")
public class ApiGroupController extends BaseController {
    @Resource
    private ApiGroupService apiGroupService;

    @RequestMapping("add")
    public Result<String> add(@RequestBody ApiGroup apiGroupDto) {
        UserVo operator = UserUtils.getOperator();
        apiGroupDto.initCreate(operator);
        String id = apiGroupService.save(apiGroupDto, operator);
        return ok(id);
    }

    @RequestMapping("edit")
    public Result<String> edit(@RequestBody ApiGroup apiGroupDto) {
        FastUtils.checkParams(apiGroupDto.getId());
        UserVo operator = UserUtils.getOperator();
        apiGroupDto.initUpdate(operator);
        apiGroupService.save(apiGroupDto, operator);
        return ok(apiGroupDto.getId());
    }

    @RequestMapping("findList")
    public Result<List<ApiGroupVo>> findList(@RequestBody ApiGroup apiGroupDto) {
        return ok(apiGroupService.findList(apiGroupDto));
    }

    @RequestMapping("delete")
    public Result<Object> delete(@RequestBody ApiGroup apiGroupDto) {
		apiGroupService.delete(apiGroupDto);
        return ok();
    }
}
