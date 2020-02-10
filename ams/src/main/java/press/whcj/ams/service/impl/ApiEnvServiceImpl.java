package press.whcj.ams.service.impl;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import press.whcj.ams.common.ColumnName;
import press.whcj.ams.entity.ApiEnv;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.service.ApiEnvService;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.PermUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/30
 */
@Service
public class ApiEnvServiceImpl implements ApiEnvService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String save(ApiEnv apiEnvDto, UserVo operator) {
        boolean isUpdate = apiEnvDto.getId() != null;
        String name = apiEnvDto.getName();
        String projectId = apiEnvDto.getProjectId();
        FastUtils.checkParams(name, projectId);
        PermUtils.checkProjectWrite(mongoTemplate, projectId, operator);
        ApiEnv apiEnv;
        if (isUpdate) {
            apiEnv = mongoTemplate.findById(apiEnvDto.getId(), ApiEnv.class);
            FastUtils.checkNull(apiEnv);
            Objects.requireNonNull(apiEnv).setUpdate(null);
        } else {
            apiEnv = new ApiEnv();
        }
        FastUtils.copyProperties(apiEnvDto, apiEnv);
        synchronized (projectId.intern()) {
            FastUtils.checkNameAndSave(apiEnvDto.getId(), isUpdate, name, apiEnv, mongoTemplate, Criteria.where(ColumnName.PROJECT_ID).is(projectId));
        }
        return apiEnvDto.getId();
    }

    @Override
    public List<ApiEnv> findList(ApiEnv apiEnvDto) {
        FastUtils.checkParams(apiEnvDto.getProjectId());
        return mongoTemplate.find(new Query(Criteria.where(ColumnName.PROJECT_ID)
                .is(apiEnvDto.getProjectId())), ApiEnv.class);
    }

    @Override
    public void delete(ApiEnv apiEnvDto, UserVo operator) {
        FastUtils.checkParams(apiEnvDto.getId());
        ApiEnv apiEnv = mongoTemplate.findById(apiEnvDto.getId(), ApiEnv.class);
        if (apiEnv == null) {
            return;
        }
        PermUtils.checkProjectWrite(mongoTemplate, apiEnv.getProjectId(), operator);
        mongoTemplate.remove(apiEnv);
    }
}
