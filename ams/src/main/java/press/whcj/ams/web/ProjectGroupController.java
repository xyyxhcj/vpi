package press.whcj.ams.web;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import press.whcj.ams.entity.ProjectGroup;
import press.whcj.ams.entity.vo.ProjectGroupVo;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.service.ProjectGroupService;
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
@RequestMapping("projectGroup")
public class ProjectGroupController extends BaseController {
	@Resource
	private ProjectGroupService projectGroupService;

	@RequestMapping("add")
	public Result<String> add(@RequestBody ProjectGroup projectGroup) {
		UserVo operator = UserUtils.getOperator();
		projectGroup.initCreate(operator);
		String id = projectGroupService.save(projectGroup, operator);
		return ok(id);
	}

	@RequestMapping("edit")
	public Result<String> edit(@RequestBody ProjectGroup projectGroup) {
		FastUtils.checkParams(projectGroup.getId());
		UserVo operator = UserUtils.getOperator();
		projectGroup.initUpdate(operator);
		projectGroupService.save(projectGroup, operator);
		return ok(projectGroup.getId());
	}

	@RequestMapping("findList")
	public Result<List<ProjectGroupVo>> findList(@RequestBody ProjectGroup projectGroup) {
		return ok(projectGroupService.findList(projectGroup, UserUtils.getOperator()));
	}

	@RequestMapping("findDetail")
	public Result<ProjectGroupVo> findDetail(@RequestBody ProjectGroup projectGroup) {
		return ok(projectGroupService.findDetail(projectGroup));
	}

	@RequestMapping("delete")
	public Result<Object> delete(@RequestBody ProjectGroup projectGroup) {
		projectGroupService.delete(projectGroup);
		return ok();
	}
}
