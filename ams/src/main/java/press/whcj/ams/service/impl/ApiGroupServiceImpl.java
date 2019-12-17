package press.whcj.ams.service.impl;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import press.whcj.ams.common.ColumnName;
import press.whcj.ams.common.Constant;
import press.whcj.ams.entity.ApiGroup;
import press.whcj.ams.entity.vo.ApiGroupVo;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.service.ApiGroupService;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.PermUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Service
public class ApiGroupServiceImpl implements ApiGroupService {
	@Resource
	private MongoTemplate mongoTemplate;

	@Override
	public String save(ApiGroup apiGroupDto, UserVo operator) {
		boolean isUpdate = apiGroupDto.getId() != null;
		String name = apiGroupDto.getName();
		String projectId = apiGroupDto.getProjectId();
		FastUtils.checkParams(name, projectId);
		PermUtils.checkProjectWrite(mongoTemplate, projectId, operator);
		ApiGroup apiGroup;
		if (isUpdate) {
			apiGroup = mongoTemplate.findById(apiGroupDto.getId(), ApiGroup.class);
			FastUtils.checkNull(apiGroup);
			Objects.requireNonNull(apiGroup).setUpdate(null);
		} else {
			apiGroup = new ApiGroup();
		}
		FastUtils.copyProperties(apiGroupDto, apiGroup);
		synchronized (projectId.intern()) {
			FastUtils.checkNameAndSave(apiGroupDto.getId(), isUpdate, name, apiGroup, mongoTemplate, Criteria.where(ColumnName.PROJECT_ID).is(projectId));
		}
		return apiGroup.getId();
	}

	@Override
	public List<ApiGroupVo> findList(ApiGroup apiGroupDto) {
		FastUtils.checkParams(apiGroupDto.getProjectId());
		Criteria definition;
		if (StringUtils.isEmpty(apiGroupDto.getParentId())) {
			definition = new Criteria();
		} else {
			definition = Criteria.where(ColumnName.PARENT_ID).is(apiGroupDto.getParentId());
		}
		return mongoTemplate.find(new Query(definition).with(Sort.by(ColumnName.SORT)),
				ApiGroupVo.class, Constant.CollectionName.API_GROUP);
	}
}
