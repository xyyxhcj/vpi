package press.whcj.ams.service.impl;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import press.whcj.ams.common.ColumnName;
import press.whcj.ams.common.Constant;
import press.whcj.ams.entity.ProjectUser;
import press.whcj.ams.entity.User;
import press.whcj.ams.entity.dto.ProjectUserDto;
import press.whcj.ams.service.ProjectUserService;
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
public class ProjectUserServiceImpl implements ProjectUserService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public void edit(ProjectUserDto projectUserDto) {
        FastUtils.checkParams(projectUserDto.getId());
        if (Constant.Is.YES.equals(projectUserDto.getIsDel())) {
            mongoTemplate.remove(new Query(Criteria.where(ColumnName.ID).is(projectUserDto.getId())), ProjectUser.class);
        } else {
            ProjectUser projectUser = mongoTemplate.findById(projectUserDto.getId(), ProjectUser.class);
            FastUtils.checkNull(projectUser);
            mongoTemplate.save(Objects.requireNonNull(FastUtils.copyProperties(projectUserDto, projectUser)));
        }
    }

    @Override
    public List<User> findProjectUser(ProjectUser projectUser) {
        String projectId = projectUser.getProjectId();
        FastUtils.checkParams(projectId);
        Criteria criteria = Criteria.where(ColumnName.PROJECT_$ID).is(new ObjectId(projectId));
        if (projectUser.getUserType() != null & !Constant.UserType.CREATOR.equals(projectUser.getUserType())) {
            criteria = criteria.and(ColumnName.USER_TYPE).is(projectUser.getUserType());
        }
        List<ProjectUser> projectUsers = mongoTemplate.find(new Query(criteria), ProjectUser.class);
        return projectUsers.stream().filter(k -> k.getUser() != null)
                .map(item -> item.getUser().setUserType(item.getUserType()).setProjectUserId(item.getId())).collect(Collectors.toList());
    }
}
