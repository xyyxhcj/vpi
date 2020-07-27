package press.whcj.ams.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import press.whcj.ams.entity.ProjectGroup;
import press.whcj.ams.entity.dto.ProjectDTO;
import press.whcj.ams.entity.vo.ProjectGroupVO;
import press.whcj.ams.entity.vo.UserVO;
import press.whcj.ams.service.ProjectGroupService;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.UserUtils;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@RestController
@RequestMapping("projectGroup")
public class ProjectGroupController extends BaseController {
	@Resource
	private ProjectGroupService projectGroupService;

	@PostMapping("add")
	public Result<String> add(@RequestBody ProjectGroup projectGroup) {
		UserVO operator = UserUtils.getOperator();
		projectGroup.initCreate(operator);
		String id = projectGroupService.save(projectGroup, operator);
		return ok(id);
	}

	@PostMapping("edit")
	public Result<String> edit(@RequestBody ProjectGroup projectGroup) {
		FastUtils.checkParams(projectGroup.getId());
		UserVO operator = UserUtils.getOperator();
		projectGroup.initUpdate(operator);
		projectGroupService.save(projectGroup, operator);
		return ok(projectGroup.getId());
	}

	@PostMapping("findList")
	public Result<List<ProjectGroupVO>> findList(@RequestBody ProjectGroup projectGroup) {
		return ok(projectGroupService.findList(projectGroup, UserUtils.getOperator()));
	}

	@PostMapping("findDetail")
	public Result<ProjectGroupVO> findDetail(@RequestBody ProjectGroup projectGroup) {
		return ok(projectGroupService.findDetail(projectGroup));
	}

	@PostMapping("delete")
	public Result<Object> delete(@RequestBody ProjectGroup projectGroup) {
		projectGroupService.delete(projectGroup);
		return ok();
	}

	@PostMapping("moveGroup")
	public Result<Object> moveGroup(@RequestBody ProjectDTO projectDTO) {
		projectGroupService.moveGroup(projectDTO, UserUtils.getOperator());
		return ok();
	}
}
