package press.whcj.ams.util;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import press.whcj.ams.common.ColumnName;
import press.whcj.ams.common.Constant;
import press.whcj.ams.entity.Project;
import press.whcj.ams.entity.ProjectUser;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.exception.ResultCode;
import press.whcj.ams.exception.ServiceException;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public class PermUtils {
	public static void checkAdmin(UserVo operator) {
		if (!Constant.Is.YES.equals(operator.getIsAdmin())) {
			throw new ServiceException(ResultCode.PERMISSION_DENIED);
		}
	}

	public static void checkProjectOwner(MongoTemplate mongoTemplate, String projectId, UserVo operator) {
		boolean isOwner = isOwner(mongoTemplate, projectId, operator);
		if (!isOwner) {
			throw new ServiceException(ResultCode.PERMISSION_DENIED);
		}
	}

	public static void checkProjectAdmin(MongoTemplate mongoTemplate, String projectId, UserVo operator) {
		boolean isOwner = isOwner(mongoTemplate, projectId, operator);
		if (!isOwner) {
			ProjectUser projectUser = mongoTemplate.findOne(
					new Query(Criteria.where(ColumnName.PROJECT_$ID).is(new ObjectId(projectId))
							.and(ColumnName.USER_$ID).is(new ObjectId(operator.getId()))), ProjectUser.class);
			if (projectUser == null || !Constant.UserType.ADMIN.equals(projectUser.getUserType())) {
				throw new ServiceException(ResultCode.PERMISSION_DENIED);
			}
		}
	}

	private static boolean isOwner(MongoTemplate mongoTemplate, String projectId, UserVo operator) {
		Project project = mongoTemplate.findById(projectId, Project.class);
		return project != null && operator.getId().equals(project.getCreateId());
	}

	public static void checkProjectWrite(MongoTemplate mongoTemplate, String projectId, UserVo operator) {
		boolean isOwner = isOwner(mongoTemplate, projectId, operator);
		if (!isOwner) {
			ProjectUser projectUser = mongoTemplate.findOne(
					new Query(Criteria.where(ColumnName.PROJECT_$ID).is(new ObjectId(projectId))
							.and(ColumnName.USER_$ID).is(new ObjectId(operator.getId()))), ProjectUser.class);
			if (projectUser == null || !Constant.UserType.READ.equals(projectUser.getUserType())) {
				throw new ServiceException(ResultCode.PERMISSION_DENIED);
			}
		}
	}
}
