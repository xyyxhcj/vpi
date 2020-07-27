package press.whcj.ams.service.impl;

import java.util.List;

import javax.annotation.Resource;

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
import press.whcj.ams.entity.dto.ApiTestHistoryDTO;
import press.whcj.ams.entity.vo.UserVO;
import press.whcj.ams.service.ApiTestHistoryService;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.PermUtils;
import press.whcj.ams.util.UserUtils;

/**
 * @author xyyxhcj@qq.com
 * @since 2020/02/08
 */
@Service
public class ApiTestHistoryServiceImpl implements ApiTestHistoryService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String save(ApiTestHistory apiTestHistory, UserVO operator) {
        FastUtils.checkParams(apiTestHistory.getApiId(), apiTestHistory.getRequestInfo(), apiTestHistory.getResponseInfo());
        mongoTemplate.save(apiTestHistory);
        return apiTestHistory.getId();
    }

    @Override
    public MongoPage<ApiTestHistory> findPage(ApiTestHistoryDTO apiTestHistoryDTO) {
        FastUtils.checkParams(apiTestHistoryDTO.getApiId());
        MongoPage<ApiTestHistory> page = apiTestHistoryDTO.getPage();
        Criteria criteria = Criteria.where(ColumnName.API_ID).is(apiTestHistoryDTO.getApiId());
        if (!StringUtils.isEmpty(apiTestHistoryDTO.getOperateId())) {
            criteria = criteria.and(ColumnName.CREATE_$ID).is(new ObjectId(apiTestHistoryDTO.getOperateId()));
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
    public void delete(ApiTestHistoryDTO apiTestHistoryDTO) {
        List<String> ids = apiTestHistoryDTO.getIds();
        String projectId = apiTestHistoryDTO.getProjectId();
        FastUtils.checkParams(ids, projectId);
        PermUtils.checkProjectWrite(mongoTemplate, projectId, UserUtils.getOperator());
        mongoTemplate.remove(new Query(Criteria.where(ColumnName.ID).in(ids)), ApiTestHistory.class);
    }
}
