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

import javax.annotation.Resource;

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
        page.setTotal(mongoTemplate.count(query, ApiTestHistory.class));
        return page.setRecords(mongoTemplate.find(query, ApiTestHistory.class));
    }
}
