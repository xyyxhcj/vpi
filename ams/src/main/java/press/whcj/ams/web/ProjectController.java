package press.whcj.ams.web;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import press.whcj.ams.entity.Project;
import press.whcj.ams.entity.ProjectGroup;
import press.whcj.ams.entity.ProjectUser;
import press.whcj.ams.entity.User;
import press.whcj.ams.entity.dto.ProjectDTO;
import press.whcj.ams.entity.dto.ProjectUserDTO;
import press.whcj.ams.entity.vo.ProjectGroupVO;
import press.whcj.ams.entity.vo.UserVO;
import press.whcj.ams.service.ProjectGroupService;
import press.whcj.ams.service.ProjectService;
import press.whcj.ams.service.ProjectUserService;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.PermUtils;
import press.whcj.ams.util.UserUtils;

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

    @PostMapping("add")
    public Result<String> add(@RequestBody ProjectDTO projectDTO) {
        UserVO operator = UserUtils.getOperator();
        projectDTO.initCreate(operator);
        String id = projectService.save(projectDTO, operator);
        return ok(id);
    }

    @PostMapping("edit")
    public Result<String> edit(@RequestBody ProjectDTO projectDTO) {
        FastUtils.checkParams(projectDTO.getId());
        UserVO operator = UserUtils.getOperator();
        projectDTO.initUpdate(operator);
        projectService.save(projectDTO, operator);
        return ok(projectDTO.getId());
    }

    @PostMapping("assign")
    public Result<Object> assign(@RequestBody ProjectDTO projectDTO) {
        FastUtils.checkParams(projectDTO.getId());
        UserVO operator = UserUtils.getOperator();
        PermUtils.checkProjectAdmin(mongoTemplate, projectDTO.getId(), operator);
        projectService.assign(projectDTO, operator);
        return ok(true);
    }

    @PostMapping("findProjectUser")
    public Result<List<User>> findProjectUser(@RequestBody ProjectUser projectUser) {
        return ok(projectUserService.findProjectUser(projectUser));
    }

    @PostMapping("findList")
    public Result<List<Project>> findList(@RequestBody ProjectDTO projectDTO) {
        return ok(projectService.findList(projectDTO, UserUtils.getOperator()));
    }

    @PostMapping("findListByGroupForOwner")
    public Result<List<Object>> findListByGroupForOwner(@RequestBody ProjectDTO projectDTO) {
        ProjectGroup projectGroupDTO = new ProjectGroup();
        if (StringUtils.isEmpty(projectDTO.getGroupId())) {
            projectDTO.setGroupId(null);
        }
        projectGroupDTO.setParentId(projectDTO.getGroupId());
        UserVO operator = UserUtils.getOperator();
        List<ProjectGroupVO> listByParent = projectGroupService.findListByParentForOwner(projectGroupDTO, operator);
        List<Project> listByGroup = projectService.findListByGroupForOwner(projectDTO, operator);
        List<Object> result = new LinkedList<>(listByParent);
        result.addAll(listByGroup);
        return ok(result);
    }

    @PostMapping("findListByGroupForOther")
    public Result<List<Object>> findListByGroupForOther(@RequestBody ProjectDTO projectDTO) {
        ProjectGroup projectGroupDTO = new ProjectGroup();
        if (StringUtils.isEmpty(projectDTO.getGroupId())) {
            projectDTO.setGroupId(null);
        }
        projectGroupDTO.setParentId(projectDTO.getGroupId());
        UserVO operator = UserUtils.getOperator();
        List<ProjectGroupVO> listByParent = projectGroupService.findListByParentForOther(projectGroupDTO, operator);
        List<Project> listByGroup = projectService.findListByGroupForOther(projectDTO, operator);
        List<Object> result = new LinkedList<>(listByParent);
        result.addAll(listByGroup);
        return ok(result);
    }

    @PostMapping("editProjectUser")
    public Result<Object> editProjectUser(@RequestBody ProjectUserDTO projectUserDTO) {
        FastUtils.checkParams(projectUserDTO.getProjectId());
        UserVO operator = UserUtils.getOperator();
        PermUtils.checkProjectAdmin(mongoTemplate, projectUserDTO.getProjectId(), operator);
        projectUserService.edit(projectUserDTO);
        return ok();
    }

    @PostMapping("remove")
    public Result<Object> remove(@RequestBody ProjectDTO projectDTO) {
        String projectId = projectDTO.getId();
        FastUtils.checkParams(projectId);
        UserVO operator = UserUtils.getOperator();
        PermUtils.checkProjectOwner(mongoTemplate, projectId, operator);
        projectService.remove(projectId, operator);
        return ok();
    }
}
