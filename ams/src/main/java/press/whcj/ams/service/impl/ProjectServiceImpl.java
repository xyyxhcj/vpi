package press.whcj.ams.service.impl;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import press.whcj.ams.common.ColumnName;
import press.whcj.ams.common.Constant;
import press.whcj.ams.entity.Project;
import press.whcj.ams.entity.ProjectUser;
import press.whcj.ams.entity.User;
import press.whcj.ams.entity.dto.ProjectDto;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.exception.ResultCode;
import press.whcj.ams.exception.ServiceException;
import press.whcj.ams.service.ProjectService;
import press.whcj.ams.util.FastUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Service
public class ProjectServiceImpl implements ProjectService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String save(ProjectDto projectDto, UserVo operator) {
        boolean isUpdate = projectDto.getId() != null;
        String name = projectDto.getName();
        String operatorId = operator.getId();
        FastUtils.checkParams(name);
        Project project;
        if (isUpdate) {
            project = mongoTemplate.findById(projectDto.getId(), Project.class);
            FastUtils.checkNull(project);
            if (!operatorId.equals(Objects.requireNonNull(project).getCreateId())) {
                throw new ServiceException(ResultCode.PERMISSION_DENIED);
            }
            project.setUpdate(null);
        } else {
            project = new Project();
        }
        FastUtils.copyProperties(projectDto, project);
        synchronized (operatorId.intern()) {
            FastUtils.checkNameAndSave(projectDto.getId(), isUpdate, name, project, mongoTemplate, Criteria.where(ColumnName.CREATE_$ID).is(new ObjectId(operatorId)));
        }
        return project.getId();
    }

    @Override
    public void assign(ProjectDto projectDto, UserVo operator) {
        if (projectDto.getProjectUsers() == null) {
            return;
        }
        String projectId = projectDto.getId();
        FastUtils.checkParams(projectId);
        Project project = new Project(projectId);
        ObjectId projectObjectId = new ObjectId(projectId);
        if (projectDto.getProjectUsers().size() > 0) {
            for (ProjectUser projectUser : projectDto.getProjectUsers()) {
                FastUtils.checkParams(projectUser.getUserId(), projectUser.getUserType());
                if (operator.getId().equals(projectUser.getUserId())) {
                    throw new ServiceException(ResultCode.PARAMS_ERROR);
                }
                projectUser.setProject(project);
                projectUser.setUser(new User(projectUser.getUserId()));
            }
            mongoTemplate.remove(new Query(Criteria.where(ColumnName.PROJECT_$ID).is(projectObjectId)), ProjectUser.class);
            mongoTemplate.insertAll(projectDto.getProjectUsers());
        } else {
            mongoTemplate.remove(new Query(Criteria.where(ColumnName.PROJECT_$ID).is(projectObjectId)), ProjectUser.class);
        }
    }

    @Override
    public List<Project> findList(ProjectDto projectDto, UserVo operator) {
        ObjectId operatorObjectId = new ObjectId(operator.getId());
        List<Project> projects = mongoTemplate.find(new Query(
                Criteria.where(ColumnName.CREATE_$ID).is(operatorObjectId)), Project.class);
        projects.forEach(project -> project.setUserType(Constant.UserType.CREATOR));
        List<ProjectUser> projectUsers = mongoTemplate.find(new Query(
                Criteria.where(ColumnName.USER_$ID).is(operatorObjectId)), ProjectUser.class);
        List<Project> userProjects = projectUsers.stream().filter(item -> item.getProject() != null)
                .map(projectUser -> projectUser.getProject().setUserType(projectUser.getUserType()))
                .collect(Collectors.toList());
        projects.addAll(userProjects);
        return projects;
    }

    @Override
    public List<Project> findListByGroup(ProjectDto projectDto, UserVo operator) {
        ObjectId operatorObjectId = new ObjectId(operator.getId());
        String groupId = projectDto.getGroupId();
        boolean findByGroup = groupId != null;
        Criteria criteria = Criteria.where(ColumnName.CREATE_$ID).is(operatorObjectId);
        if (findByGroup) {
            criteria = criteria.and(ColumnName.GROUP_ID).is(groupId);
        } else {
            criteria = criteria.and(ColumnName.GROUP_ID).is(null);
        }
        List<Project> projects = mongoTemplate.find(new Query(criteria), Project.class);
        projects.forEach(project -> project.setUserType(Constant.UserType.CREATOR));
        criteria = Criteria.where(ColumnName.USER_$ID).is(operatorObjectId);
        if (findByGroup) {
            criteria = criteria.and(ColumnName.PROJECT_$GROUP_ID).is(groupId);
        } else {
            criteria = criteria.and(ColumnName.PROJECT_$GROUP_ID).is(null);
        }
        List<ProjectUser> projectUsers = mongoTemplate.find(new Query(criteria), ProjectUser.class);
        List<Project> userProjects = projectUsers.stream().filter(item -> item.getProject() != null)
                .map(projectUser -> projectUser.getProject().setUserType(projectUser.getUserType()))
                .collect(Collectors.toList());
        projects.addAll(userProjects);
        return projects;
    }
}
