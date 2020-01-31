package press.whcj.ams.service.impl;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import press.whcj.ams.common.ColumnName;
import press.whcj.ams.common.Constant;
import press.whcj.ams.entity.*;
import press.whcj.ams.entity.dto.ApiDto;
import press.whcj.ams.entity.dto.StructureDto;
import press.whcj.ams.entity.vo.ApiVo;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.service.ApiService;
import press.whcj.ams.service.StructureService;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.PermUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Service
public class ApiServiceImpl implements ApiService {
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private StructureService structureService;

    @Override
    public String save(ApiDto apiDto, UserVo operator) {
        boolean isUpdate = apiDto.getId() != null;
        String name = apiDto.getName();
        String projectId = apiDto.getProjectId();
        FastUtils.checkParams(name, projectId);
        PermUtils.checkProjectWrite(mongoTemplate, projectId, operator);
        Api api;
        if (isUpdate) {
            api = mongoTemplate.findById(apiDto.getId(), Api.class);
            FastUtils.checkNull(api);
            // cjTodo 2019/12/31 保存历史
            Objects.requireNonNull(api).setUpdate(null);
        } else {
            api = new Api();
        }
        FastUtils.copyProperties(apiDto, api);
        if (!StringUtils.isEmpty(apiDto.getGroupId())) {
            api.setGroup(new ApiGroup(apiDto.getGroupId()));
        }
        String reqParamsId = saveApiParams(apiDto.getRequestParamDto(), operator, projectId);
        String respParamsId = saveApiParams(apiDto.getResponseParamDto(), operator, projectId);
        api.setRequestParam(new Structure(reqParamsId));
        api.setResponseParam(new Structure(respParamsId));
        mongoTemplate.save(api);
        String apiId = api.getId();
        if (isUpdate) {
            mongoTemplate.remove(new Query(Criteria.where(ColumnName.API_ID).is(apiId)), ApiHeader.class);
        }
        if (!CollectionUtils.isEmpty(apiDto.getRequestHeaders())) {
            for (ApiHeader header : apiDto.getRequestHeaders()) {
                header.setApiId(apiId);
                header.setIsRequest(Constant.Is.YES);
            }
            mongoTemplate.insertAll(apiDto.getRequestHeaders());
        }
        if (!CollectionUtils.isEmpty(apiDto.getResponseHeaders())) {
            for (ApiHeader header : apiDto.getResponseHeaders()) {
                header.setApiId(apiId);
                header.setIsRequest(Constant.Is.NO);
            }
            mongoTemplate.insertAll(apiDto.getResponseHeaders());
        }
        return apiId;
    }

    @Override
    public MongoPage<ApiVo> findPage(ApiDto apiDto) {
        FastUtils.checkParams(apiDto.getProjectId());
        MongoPage<ApiVo> page = apiDto.getPage();
        Criteria criteria = Criteria.where(ColumnName.PROJECT_ID).is(apiDto.getProjectId());
        if (!StringUtils.isEmpty(apiDto.getGroupId())) {
            criteria = criteria.and(ColumnName.GROUP_$ID).is(apiDto.getGroupId());
        }
        Query query = new Query(criteria);
        query.with(page.buildPageRequest()).with(QSort.by(Sort.Direction.DESC, ColumnName.UPDATE_TIME));
        page.setTotal(mongoTemplate.count(query, Api.class));
        return page.setRecords(mongoTemplate.find(query, ApiVo.class, Constant.CollectionName.API));
    }

    @Override
    public ApiVo findDetail(ApiDto apiDto) {
        String apiId = apiDto.getId();
        FastUtils.checkParams(apiId);
        ApiVo detail = mongoTemplate.findById(apiId, ApiVo.class, Constant.CollectionName.API);
        FastUtils.checkNull(detail);
        if (Objects.requireNonNull(detail).getRequestParam() != null) {
            detail.setRequestParamVo(structureService.getStructureVoById(detail.getRequestParam().getId()));
        }
        if (detail.getResponseParam() != null) {
            detail.setResponseParamVo(structureService.getStructureVoById(detail.getResponseParam().getId()));
        }
        List<ApiHeader> headers = mongoTemplate.find(new Query(Criteria.where(ColumnName.API_ID).is(apiId)), ApiHeader.class);
        detail.setRequestHeaders(new LinkedList<>());
        detail.setResponseHeaders(new LinkedList<>());
        for (ApiHeader header : headers) {
            if (Constant.Is.YES.equals(header.getIsRequest())) {
                detail.getRequestHeaders().add(header);
            } else {
                detail.getResponseHeaders().add(header);
            }
        }
        return detail;
    }

    private String saveApiParams(StructureDto paramDto, UserVo operator, String projectId) {
        if (paramDto != null) {
            if (StringUtils.isEmpty(paramDto.getId())) {
                paramDto.setType(Constant.StructureType.API_CREATE);
            }
            paramDto.setProjectId(projectId);
            paramDto.setCheckName(false);
            paramDto.setName(UUID.randomUUID().toString());
            LocalDateTime now = LocalDateTime.now();
            User update = new User(operator.getId());
            if (StringUtils.isEmpty(paramDto.getId())) {
                paramDto.setCreate(update);
                paramDto.setCreateTime(now);
                paramDto.setUpdateTime(now);
            } else {
                paramDto.setUpdate(update);
                paramDto.setUpdateTime(now);
            }
            return structureService.save(paramDto, operator);
        }
        return null;
    }
}
