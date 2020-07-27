package press.whcj.ams.service.impl;

import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import press.whcj.ams.common.ColumnName;
import press.whcj.ams.entity.ApiEnv;
import press.whcj.ams.entity.vo.UserVO;
import press.whcj.ams.service.ApiEnvService;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.PermUtils;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/30
 */
@Service
public class ApiEnvServiceImpl implements ApiEnvService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String save(ApiEnv apiEnvDTO, UserVO operator) {
        boolean isUpdate = apiEnvDTO.getId() != null;
        String name = apiEnvDTO.getName();
        String projectId = apiEnvDTO.getProjectId();
        FastUtils.checkParams(name, projectId);
        PermUtils.checkProjectWrite(mongoTemplate, projectId, operator);
        ApiEnv apiEnv;
        if (isUpdate) {
            apiEnv = mongoTemplate.findById(apiEnvDTO.getId(), ApiEnv.class);
            FastUtils.checkNull(apiEnv);
            Objects.requireNonNull(apiEnv).setUpdate(null);
        } else {
            apiEnv = new ApiEnv();
        }
        FastUtils.copyProperties(apiEnvDTO, apiEnv);
        synchronized (projectId.intern()) {
            FastUtils.checkNameAndSave(apiEnvDTO.getId(), isUpdate, name, apiEnv, mongoTemplate, Criteria.where(ColumnName.PROJECT_ID).is(projectId));
        }
        return apiEnvDTO.getId();
    }

    @Override
    public List<ApiEnv> findList(ApiEnv apiEnvDTO) {
        FastUtils.checkParams(apiEnvDTO.getProjectId());
        return mongoTemplate.find(new Query(Criteria.where(ColumnName.PROJECT_ID)
                .is(apiEnvDTO.getProjectId())), ApiEnv.class);
    }

    @Override
    public void delete(ApiEnv apiEnvDTO, UserVO operator) {
        FastUtils.checkParams(apiEnvDTO.getId());
        ApiEnv apiEnv = mongoTemplate.findById(apiEnvDTO.getId(), ApiEnv.class);
        if (apiEnv == null) {
            return;
        }
        PermUtils.checkProjectWrite(mongoTemplate, apiEnv.getProjectId(), operator);
        mongoTemplate.remove(apiEnv);
    }
}
