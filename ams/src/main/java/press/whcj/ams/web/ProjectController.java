package press.whcj.ams.web;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import press.whcj.ams.entity.Project;
import press.whcj.ams.entity.ProjectGroup;
import press.whcj.ams.entity.ProjectUser;
import press.whcj.ams.entity.User;
import press.whcj.ams.entity.dto.ProjectDto;
import press.whcj.ams.entity.dto.ProjectUserDto;
import press.whcj.ams.entity.vo.ProjectGroupVo;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.service.ProjectGroupService;
import press.whcj.ams.service.ProjectService;
import press.whcj.ams.service.ProjectUserService;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.PermUtils;
import press.whcj.ams.util.UserUtils;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@RestController
@RequestMapping("project")
public class ProjectController extends BaseController {
	@Resource
	private ProjectService projectService;
	@Resource
	private ProjectUserService projectUserService;
	@Resource
	private ProjectGroupService projectGroupService;
	@Resource
	private MongoTemplate mongoTemplate;

	@RequestMapping("add")
	public Result<String> add(@RequestBody ProjectDto projectDto) {
		UserVo operator = UserUtils.getOperator();
		projectDto.initCreate(operator);
		String id = projectService.save(projectDto, operator);
		return ok(id);
	}

	@RequestMapping("edit")
	public Result<String> edit(@RequestBody ProjectDto projectDto) {
		FastUtils.checkParams(projectDto.getId());
		UserVo operator = UserUtils.getOperator();
		projectDto.initUpdate(operator);
		projectService.save(projectDto, operator);
		return ok(projectDto.getId());
	}

	@RequestMapping("assign")
	public Result<Object> assign(@RequestBody ProjectDto projectDto) {
		FastUtils.checkParams(projectDto.getId());
		UserVo operator = UserUtils.getOperator();
		PermUtils.checkProjectAdmin(mongoTemplate, projectDto.getId(), operator);
		projectService.assign(projectDto, operator);
		return ok(true);
	}

	@RequestMapping("findProjectUser")
	public Result<List<User>> findProjectUser(@RequestBody ProjectUser projectUser) {
		return ok(projectUserService.findProjectUser(projectUser));
	}

	@RequestMapping("findList")
	public Result<List<Project>> findList(@RequestBody ProjectDto projectDto) {
		return ok(projectService.findList(projectDto, UserUtils.getOperator()));
	}

	@RequestMapping("findListByGroup")
	public Result<List<Object>> findListByGroup(@RequestBody ProjectDto projectDto) {
		ProjectGroup projectGroupDto = new ProjectGroup();
		if (StringUtils.isEmpty(projectDto.getGroupId())) {
			projectDto.setGroupId(null);
		}
		projectGroupDto.setParentId(projectDto.getGroupId());
		UserVo operator = UserUtils.getOperator();
		List<ProjectGroupVo> listByParent = projectGroupService.findListByParent(projectGroupDto, operator);
		List<Project> listByGroup = projectService.findListByGroup(projectDto, operator);
		List<Object> result = new LinkedList<>(listByParent);
		result.addAll(listByGroup);
		return ok(result);
	}

	@RequestMapping("editProjectUser")
	public Result<Object> editProjectUser(@RequestBody ProjectUserDto projectUserDto) {
		FastUtils.checkParams(projectUserDto.getProjectId());
		UserVo operator = UserUtils.getOperator();
		PermUtils.checkProjectAdmin(mongoTemplate, projectUserDto.getProjectId(), operator);
		projectUserService.edit(projectUserDto);
		return ok();
	}

	@RequestMapping("remove")
	public Result<Object> remove(@RequestBody ProjectDto projectDto) {
		String projectId = projectDto.getId();
		FastUtils.checkParams(projectId);
		UserVo operator = UserUtils.getOperator();
		PermUtils.checkProjectOwner(mongoTemplate, projectId, operator);
		projectService.remove(projectId, operator);
		return ok();
	}
}
