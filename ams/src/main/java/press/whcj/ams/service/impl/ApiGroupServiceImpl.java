package press.whcj.ams.service.impl;

import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import press.whcj.ams.common.ColumnName;
import press.whcj.ams.common.Constant;
import press.whcj.ams.entity.Api;
import press.whcj.ams.entity.ApiGroup;
import press.whcj.ams.entity.vo.ApiGroupVO;
import press.whcj.ams.entity.vo.UserVO;
import press.whcj.ams.service.ApiGroupService;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.PermUtils;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Service
public class ApiGroupServiceImpl implements ApiGroupService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String save(ApiGroup apiGroupDTO, UserVO operator) {
        boolean isUpdate = apiGroupDTO.getId() != null;
        String name = apiGroupDTO.getName();
        String projectId = apiGroupDTO.getProjectId();
        FastUtils.checkParams(name, projectId);
        PermUtils.checkProjectWrite(mongoTemplate, projectId, operator);
        ApiGroup apiGroup;
        if (isUpdate) {
            apiGroup = mongoTemplate.findById(apiGroupDTO.getId(), ApiGroup.class);
            FastUtils.checkNull(apiGroup);
            Objects.requireNonNull(apiGroup).setUpdate(null);
        } else {
            apiGroup = new ApiGroup();
        }
        FastUtils.copyProperties(apiGroupDTO, apiGroup);
        if (apiGroupDTO.getParentId() != null) {
            // maybe = ''
            apiGroup.setParentId(apiGroupDTO.getParentId());
        }
        synchronized (projectId.intern()) {
            FastUtils.checkNameAndSave(apiGroupDTO.getId(), isUpdate, name, apiGroup, mongoTemplate, Criteria.where(ColumnName.PROJECT_ID).is(projectId));
        }
        return apiGroup.getId();
    }

    @Override
    public List<ApiGroupVO> findList(ApiGroup apiGroupDTO) {
        FastUtils.checkParams(apiGroupDTO.getProjectId());
        Criteria definition = Criteria.where(ColumnName.PROJECT_ID).is(apiGroupDTO.getProjectId());
        if (!StringUtils.isEmpty(apiGroupDTO.getParentId())) {
            definition = definition.and(ColumnName.PARENT_ID).is(apiGroupDTO.getParentId());
        }
        return mongoTemplate.find(new Query(definition).with(Sort.by(ColumnName.SORT)),
                ApiGroupVO.class, Constant.CollectionName.API_GROUP);
    }

    @Override
    public void delete(ApiGroup apiGroupDTO) {
        String apiGroupId = apiGroupDTO.getId();
        FastUtils.checkParams(apiGroupId);
        apiGroupDTO = mongoTemplate.findById(apiGroupId, ApiGroup.class);
        if (apiGroupDTO == null) {
            return;
        }
        // concat sub & parent
        mongoTemplate.updateMulti(new Query(Criteria.where(ColumnName.PARENT_ID).is(apiGroupId)),
                Update.update(ColumnName.PARENT_ID, apiGroupDTO.getParentId()), ApiGroup.class);
        mongoTemplate.updateMulti(new Query(Criteria.where(ColumnName.GROUP_$ID).is(new ObjectId(apiGroupId))),
                Update.update(ColumnName.GROUP, new ApiGroup(apiGroupDTO.getParentId())), Api.class);
        mongoTemplate.remove(apiGroupDTO);
    }
}
