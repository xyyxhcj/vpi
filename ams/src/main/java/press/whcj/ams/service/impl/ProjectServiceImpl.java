package press.whcj.ams.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import press.whcj.ams.common.ColumnName;
import press.whcj.ams.common.Constant;
import press.whcj.ams.entity.Project;
import press.whcj.ams.entity.ProjectGroup;
import press.whcj.ams.entity.ProjectUser;
import press.whcj.ams.entity.User;
import press.whcj.ams.entity.dto.ProjectDTO;
import press.whcj.ams.entity.vo.UserVO;
import press.whcj.ams.exception.ResultCode;
import press.whcj.ams.exception.ServiceException;
import press.whcj.ams.service.ProjectService;
import press.whcj.ams.util.FastUtils;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Service
public class ProjectServiceImpl implements ProjectService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String save(ProjectDTO projectDTO, UserVO operator) {
        boolean isUpdate = projectDTO.getId() != null;
        String name = projectDTO.getName();
        String operatorId = operator.getId();
        FastUtils.checkParams(name);
        if (projectDTO.getGroupId() != null) {
            ProjectGroup existProjectGroup = mongoTemplate.findById(projectDTO.getGroupId(), ProjectGroup.class);
            FastUtils.checkNull(existProjectGroup);
            if (!operator.getId().equals(Objects.requireNonNull(existProjectGroup).getCreateId())) {
                // can't save to other people's group
                throw new ServiceException(ResultCode.PERMISSION_DENIED);
            }
        }
        Project project;
        if (isUpdate) {
            project = mongoTemplate.findById(projectDTO.getId(), Project.class);
            FastUtils.checkNull(project);
            if (!operatorId.equals(Objects.requireNonNull(project).getCreateId())) {
                throw new ServiceException(ResultCode.PERMISSION_DENIED);
            }
            project.setUpdate(null);
        } else {
            project = new Project();
        }
        FastUtils.copyProperties(projectDTO, project);
        synchronized (operatorId.intern()) {
            FastUtils.checkNameAndSave(projectDTO.getId(), isUpdate, name, project, mongoTemplate, Criteria.where(ColumnName.CREATE_$ID).is(new ObjectId(operatorId)));
        }
        return project.getId();
    }

    @Override
    public void assign(ProjectDTO projectDTO, UserVO operator) {
        if (projectDTO.getProjectUsers() == null) {
            return;
        }
        String projectId = projectDTO.getId();
        FastUtils.checkParams(projectId);
        Project project = new Project(projectId);
        ObjectId projectObjectId = new ObjectId(projectId);
        if (projectDTO.getProjectUsers().size() > 0) {
            for (ProjectUser projectUser : projectDTO.getProjectUsers()) {
                FastUtils.checkParams(projectUser.getUserId(), projectUser.getUserType());
                if (operator.getId().equals(projectUser.getUserId())) {
                    throw new ServiceException(ResultCode.PARAMS_ERROR);
                }
                projectUser.setProject(project);
                projectUser.setUser(new User(projectUser.getUserId()));
                projectUser.setId(null);
            }
            mongoTemplate.remove(new Query(Criteria.where(ColumnName.PROJECT_$ID).is(projectObjectId)), ProjectUser.class);
            mongoTemplate.insertAll(projectDTO.getProjectUsers());
        } else {
            mongoTemplate.remove(new Query(Criteria.where(ColumnName.PROJECT_$ID).is(projectObjectId)), ProjectUser.class);
        }
    }

    @Override
    public List<Project> findList(ProjectDTO projectDTO, UserVO operator) {
        ObjectId operatorObjectId = new ObjectId(operator.getId());
        List<Project> projects = mongoTemplate.find(new Query(
                Criteria.where(ColumnName.CREATE_$ID).is(operatorObjectId)
                        .and(ColumnName.IS_DEL).ne(Constant.Is.YES)), Project.class);
        projects.forEach(project -> project.setUserType(Constant.UserType.CREATOR));
        List<ProjectUser> projectUsers = mongoTemplate.find(new Query(
                Criteria.where(ColumnName.USER_$ID).is(operatorObjectId)
                        .and(ColumnName.PROJECT_$IS_DEL).ne(Constant.Is.YES)), ProjectUser.class);
        List<Project> userProjects = projectUsers.stream().filter(item -> item.getProject() != null)
                .map(projectUser -> projectUser.getProject().setUserType(projectUser.getUserType()))
                .collect(Collectors.toList());
        projects.addAll(userProjects);
        return projects;
    }

    @Override
    public List<Project> findListByGroupForOther(ProjectDTO projectDTO, UserVO operator) {
        ObjectId operatorObjectId = new ObjectId(operator.getId());
        String groupId = projectDTO.getGroupId();
        boolean findByGroup = groupId != null;
        Criteria criteria = Criteria.where(ColumnName.USER_$ID).is(operatorObjectId)
                .and(ColumnName.PROJECT_$IS_DEL).ne(Constant.Is.YES);
        if (findByGroup) {
            criteria = criteria.and(ColumnName.PROJECT_$GROUP_ID).is(new ObjectId(groupId));
        } else {
            criteria = criteria.and(ColumnName.PROJECT_$GROUP_ID).in(null, "");
        }
        List<ProjectUser> projectUsers = mongoTemplate.find(new Query(criteria), ProjectUser.class);
        return projectUsers.stream()
                .map(projectUser -> projectUser.getProject().setUserType(projectUser.getUserType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> findListByGroupForOwner(ProjectDTO projectDTO, UserVO operator) {
        ObjectId operatorObjectId = new ObjectId(operator.getId());
        String groupId = projectDTO.getGroupId();
        boolean findByGroup = groupId != null;
        Criteria criteria = Criteria.where(ColumnName.CREATE_$ID).is(operatorObjectId)
                .and(ColumnName.IS_DEL).ne(Constant.Is.YES);
        if (findByGroup) {
            criteria = criteria.and(ColumnName.GROUP_ID).is(groupId);
        } else {
            criteria = criteria.and(ColumnName.GROUP_ID).in(null, "");
        }
        List<Project> projects = mongoTemplate.find(new Query(criteria), Project.class);
        projects.forEach(project -> project.setUserType(Constant.UserType.CREATOR));
        return projects;
    }

    @Override
    public void remove(String projectId, UserVO operator) {
        mongoTemplate.updateFirst(new Query(Criteria.where(ColumnName.ID).is(projectId)),
                Update.update(ColumnName.IS_DEL, Constant.Is.YES)
                        .set(ColumnName.UPDATE_TIME, LocalDateTime.now())
                        .set(ColumnName.UPDATE, new User(operator.getId())),
                Project.class);
    }
}
