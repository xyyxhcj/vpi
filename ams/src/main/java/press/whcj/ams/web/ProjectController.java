package press.whcj.ams.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import press.whcj.ams.common.Constant;
import press.whcj.ams.entity.*;
import press.whcj.ams.entity.dto.ApiDto;
import press.whcj.ams.entity.dto.ProjectDto;
import press.whcj.ams.entity.dto.ProjectUserDto;
import press.whcj.ams.entity.vo.ApiGroupVo;
import press.whcj.ams.entity.vo.ApiVo;
import press.whcj.ams.entity.vo.ProjectGroupVo;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.service.*;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.PermUtils;
import press.whcj.ams.util.UserUtils;

import javax.annotation.Resource;
import java.io.IOException;
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
	private ApiGroupService apiGroupService;
	@Resource
	private ApiService apiService;
	@Resource
	private MongoTemplate mongoTemplate;

	@PostMapping("add")
	public Result<String> add(@RequestBody ProjectDto projectDto) {
		UserVo operator = UserUtils.getOperator();
		projectDto.initCreate(operator);
		String id = projectService.save(projectDto, operator);
		return ok(id);
	}

	@PostMapping("edit")
	public Result<String> edit(@RequestBody ProjectDto projectDto) {
		FastUtils.checkParams(projectDto.getId());
		UserVo operator = UserUtils.getOperator();
		projectDto.initUpdate(operator);
		projectService.save(projectDto, operator);
		return ok(projectDto.getId());
	}

	@PostMapping("assign")
	public Result<Object> assign(@RequestBody ProjectDto projectDto) {
		FastUtils.checkParams(projectDto.getId());
		UserVo operator = UserUtils.getOperator();
		PermUtils.checkProjectAdmin(mongoTemplate, projectDto.getId(), operator);
		projectService.assign(projectDto, operator);
		return ok(true);
	}

	@PostMapping("findProjectUser")
	public Result<List<User>> findProjectUser(@RequestBody ProjectUser projectUser) {
		return ok(projectUserService.findProjectUser(projectUser));
	}

	@PostMapping("findList")
	public Result<List<Project>> findList(@RequestBody ProjectDto projectDto) {
		return ok(projectService.findList(projectDto, UserUtils.getOperator()));
	}

	@PostMapping("findListByGroupForOwner")
	public Result<List<Object>> findListByGroupForOwner(@RequestBody ProjectDto projectDto) {
		ProjectGroup projectGroupDto = new ProjectGroup();
		if (StringUtils.isEmpty(projectDto.getGroupId())) {
			projectDto.setGroupId(null);
		}
		projectGroupDto.setParentId(projectDto.getGroupId());
		UserVo operator = UserUtils.getOperator();
		List<ProjectGroupVo> listByParent = projectGroupService.findListByParentForOwner(projectGroupDto, operator);
		List<Project> listByGroup = projectService.findListByGroupForOwner(projectDto, operator);
		List<Object> result = new LinkedList<>(listByParent);
		result.addAll(listByGroup);
		return ok(result);
	}

	@PostMapping("findListByGroupForOther")
	public Result<List<Object>> findListByGroupForOther(@RequestBody ProjectDto projectDto) {
		ProjectGroup projectGroupDto = new ProjectGroup();
		if (StringUtils.isEmpty(projectDto.getGroupId())) {
			projectDto.setGroupId(null);
		}
		projectGroupDto.setParentId(projectDto.getGroupId());
		UserVo operator = UserUtils.getOperator();
		List<ProjectGroupVo> listByParent = projectGroupService.findListByParentForOther(projectGroupDto, operator);
		List<Project> listByGroup = projectService.findListByGroupForOther(projectDto, operator);
		List<Object> result = new LinkedList<>(listByParent);
		result.addAll(listByGroup);
		return ok(result);
	}

	@PostMapping("editProjectUser")
	public Result<Object> editProjectUser(@RequestBody ProjectUserDto projectUserDto) {
		FastUtils.checkParams(projectUserDto.getProjectId());
		UserVo operator = UserUtils.getOperator();
		PermUtils.checkProjectAdmin(mongoTemplate, projectUserDto.getProjectId(), operator);
		projectUserService.edit(projectUserDto);
		return ok();
	}

	@PostMapping("remove")
	public Result<Object> remove(@RequestBody ProjectDto projectDto) {
		String projectId = projectDto.getId();
		FastUtils.checkParams(projectId);
		UserVo operator = UserUtils.getOperator();
		PermUtils.checkProjectOwner(mongoTemplate, projectId, operator);
		projectService.remove(projectId, operator);
		return ok();
	}

	@PostMapping("exportHtml")
	public void exportHtml(@RequestBody ProjectDto projectDto) throws IOException {
		String projectId = projectDto.getId();
		String envId = projectDto.getEnvId();
		String projectName = projectDto.getName();
		FastUtils.checkParams(projectId);
		ApiGroup apiGroup = new ApiGroup();
		apiGroup.setProjectId(projectId);
		ApiDto apiDto = new ApiDto();
		apiDto.setProjectId(projectId);
		apiDto.setEnvId(envId);
		List<ApiGroupVo> apiGroupList = apiGroupService.findList(apiGroup);
		List<ApiVo> apiList = apiService.findAllDetail(apiDto);
		Document doc = Jsoup.connect(String.format(Constant.SysConfig.EXPORT_URL, projectId, projectName, envId)).get();
		System.out.println(doc);
	}
}
