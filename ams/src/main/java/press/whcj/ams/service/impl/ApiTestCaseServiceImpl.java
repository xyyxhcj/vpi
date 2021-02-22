package press.whcj.ams.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import press.whcj.ams.common.ColumnName;
import press.whcj.ams.common.Constant;
import press.whcj.ams.entity.ApiTestCase;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.dto.ApiTestCaseDTO;
import press.whcj.ams.entity.vo.UserVO;
import press.whcj.ams.service.ApiTestCaseService;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.PermUtils;
import press.whcj.ams.util.UserUtils;

/**
 * @author xyyxhcj@qq.com
 * @since 2020/06/23
 */
@Service
public class ApiTestCaseServiceImpl implements ApiTestCaseService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String save(ApiTestCase apiTestCase, UserVO operator) {
        FastUtils.checkParams(apiTestCase.getProjectId(), apiTestCase.getApiId(), apiTestCase.getRequestInfo(), apiTestCase.getResponseInfo());
        PermUtils.checkProjectWrite(mongoTemplate, apiTestCase.getProjectId(), operator);
        mongoTemplate.save(apiTestCase);
        return apiTestCase.getId();
    }

    @Override
    public void delete(ApiTestCaseDTO apiTestCaseDTO) {
        List<String> ids = apiTestCaseDTO.getIds();
        String projectId = apiTestCaseDTO.getProjectId();
        FastUtils.checkParams(ids, projectId);
        PermUtils.checkProjectWrite(mongoTemplate, projectId, UserUtils.getOperator());
        mongoTemplate.remove(new Query(Criteria.where(ColumnName.ID).in(ids)), ApiTestCase.class);
    }

    @Override
    public MongoPage<ApiTestCase> findPage(ApiTestCaseDTO apiTestCaseDTO) {
        FastUtils.checkParams(apiTestCaseDTO.getApiId());
        MongoPage<ApiTestCase> page = apiTestCaseDTO.getPage();
        Criteria criteria = Criteria.where(ColumnName.API_ID).is(apiTestCaseDTO.getApiId());
        if (!StringUtils.isEmpty(apiTestCaseDTO.getName())) {
            // i: case insensitive
            criteria = criteria.and(ColumnName.NAME).regex(".*" + apiTestCaseDTO.getName() + ".*", "i");
        }
        Query query = new Query(criteria);
        query.with(page.buildPageRequest()).with(QSort.by(Sort.Direction.DESC, ColumnName.CREATE_TIME));
        long total = mongoTemplate.count(query, ApiTestCase.class);
        page.setTotal(total);
        if (total == 0L) {
            return page;
        }
        return page.setRecords(mongoTemplate.find(query, ApiTestCase.class));
    }

    @Override
    public ApiTestCase details(ApiTestCaseDTO apiTestCaseDTO) {
        ApiTestCase apiTestCase = mongoTemplate.findById(apiTestCaseDTO.getId(), ApiTestCase.class, Constant.CollectionName.API_TESTCASE);
        FastUtils.checkNull(apiTestCase);
        return apiTestCase;
    }
}
