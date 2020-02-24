package press.whcj.ams.service.impl;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import press.whcj.ams.common.ColumnName;
import press.whcj.ams.entity.ApiTestHistory;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.dto.ApiTestHistoryDto;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.service.ApiTestHistoryService;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.PermUtils;
import press.whcj.ams.util.UserUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2020/02/08
 */
@Service
public class ApiTestHistoryServiceImpl implements ApiTestHistoryService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String save(ApiTestHistory apiTestHistory, UserVo operator) {
        FastUtils.checkParams(apiTestHistory.getApiId(), apiTestHistory.getRequestInfo(), apiTestHistory.getResponseInfo());
        mongoTemplate.save(apiTestHistory);
        return apiTestHistory.getId();
    }

    @Override
    public MongoPage<ApiTestHistory> findPage(ApiTestHistoryDto apiTestHistoryDto) {
        FastUtils.checkParams(apiTestHistoryDto.getApiId());
        MongoPage<ApiTestHistory> page = apiTestHistoryDto.getPage();
        Criteria criteria = Criteria.where(ColumnName.API_ID).is(apiTestHistoryDto.getApiId());
        if (!StringUtils.isEmpty(apiTestHistoryDto.getOperateId())) {
            criteria = criteria.and(ColumnName.CREATE_$ID).is(new ObjectId(apiTestHistoryDto.getOperateId()));
        }
        Query query = new Query(criteria);
        query.with(page.buildPageRequest()).with(QSort.by(Sort.Direction.DESC, ColumnName.CREATE_TIME));
        long total = mongoTemplate.count(query, ApiTestHistory.class);
        page.setTotal(total);
        if (total == 0L) {
            return page;
        }
        return page.setRecords(mongoTemplate.find(query, ApiTestHistory.class));
    }

    @Override
    public void delete(ApiTestHistoryDto apiTestHistoryDto) {
        List<String> ids = apiTestHistoryDto.getIds();
        String projectId = apiTestHistoryDto.getProjectId();
        FastUtils.checkParams(ids, projectId);
        PermUtils.checkProjectWrite(mongoTemplate, projectId, UserUtils.getOperator());
        mongoTemplate.remove(new Query(Criteria.where(ColumnName.ID).in(ids)), ApiTestHistory.class);
    }
}
