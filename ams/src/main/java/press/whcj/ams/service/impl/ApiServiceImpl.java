package press.whcj.ams.service.impl;

import org.bson.BsonRegularExpression;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.querydsl.QSort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import press.whcj.ams.common.ColumnName;
import press.whcj.ams.common.Constant;
import press.whcj.ams.entity.*;
import press.whcj.ams.entity.dto.ApiDto;
import press.whcj.ams.entity.dto.StructureDataDto;
import press.whcj.ams.entity.dto.StructureDto;
import press.whcj.ams.entity.vo.ApiVo;
import press.whcj.ams.entity.vo.StructureVo;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.service.ApiService;
import press.whcj.ams.service.StructureService;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.JsonUtils;
import press.whcj.ams.util.PermUtils;
import press.whcj.ams.util.UserUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
            Objects.requireNonNull(api).setUpdate(null);
        } else {
            api = new Api();
        }
        FastUtils.copyProperties(apiDto, api);
        if (StringUtils.isEmpty(apiDto.getGroupId())) {
            api.setGroup(null);
        } else {
            api.setGroup(new ApiGroup(apiDto.getGroupId()));
        }
        // save request params
        String reqParamsId = apiDto.getRequestParamDto().getId();
        if (apiDto.getRequestParamDto().isReference()) {
            api.setReqParamIsReference(true);
        }
        if (!api.isReqParamIsReference()) {
            reqParamsId = saveApiParams(apiDto.getRequestParamDto(), operator, projectId);
        }
        api.setRequestParam(new Structure(reqParamsId));
        // save response params
        String respParamsId = apiDto.getResponseParamDto().getId();
        if (apiDto.getResponseParamDto().isReference()) {
            api.setRespParamIsReference(true);
        }
        if (!api.isRespParamIsReference()) {
            respParamsId = saveApiParams(apiDto.getResponseParamDto(), operator, projectId);
        }
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
        Criteria criteria = Criteria.where(ColumnName.PROJECT_ID).is(apiDto.getProjectId())
                .and(ColumnName.IS_DEL).ne(Constant.Is.YES);
        // concat query condition
        if (!apiDto.getGroupIds().isEmpty()) {
            List<ObjectId> ids = apiDto.getGroupIds().stream().map(ObjectId::new).collect(Collectors.toList());
            criteria = criteria.and(ColumnName.GROUP_$ID).in(ids);
        }
        if (!StringUtils.isEmpty(apiDto.getNameOrUri())) {
            BsonRegularExpression expression = new BsonRegularExpression("^.*" + apiDto.getNameOrUri() + ".*$", "i");
            criteria = criteria.orOperator(Criteria.where(ColumnName.NAME).regex(expression),
                    Criteria.where(ColumnName.API_URI).regex(expression));
        }
        if (apiDto.getApiStatus() != null) {
            criteria = criteria.and(ColumnName.API_STATUS).is(apiDto.getApiStatus());
        }
        Query query = new Query(criteria);
        query.with(page.buildPageRequest()).with(QSort.by(Sort.Direction.DESC, ColumnName.UPDATE_TIME));
        long total = mongoTemplate.count(query, Api.class);
        page.setTotal(total);
        if (total == 0L) {
            return page;
        }
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

    @Override
    public void saveMock(ApiDto apiDto) {
        FastUtils.checkParams(apiDto.getId());
        String updateColumn;
        String mock;
        if (!StringUtils.isEmpty(apiDto.getApiSuccessMock())) {
            mock = apiDto.getApiSuccessMock();
            updateColumn = ColumnName.API_SUCCESS_MOCK;
        } else {
            FastUtils.checkParams(apiDto.getApiFailureMock());
            mock = apiDto.getApiFailureMock();
            updateColumn = ColumnName.API_FAILURE_MOCK;
        }
        mongoTemplate.updateFirst(new Query(Criteria.where(ColumnName.ID).is(apiDto.getId())), Update.update(updateColumn, mock), Api.class);
    }

    @Override
    public void remove(ApiDto apiDto) {
        List<String> ids = apiDto.getIds();
        FastUtils.checkParams(ids);
        UserVo operator = UserUtils.getOperator();
        PermUtils.checkProjectWrite(mongoTemplate, apiDto.getProjectId(), operator);
        mongoTemplate.updateMulti(new Query(Criteria.where(ColumnName.ID).in(ids)),
                Update.update(ColumnName.IS_DEL, Constant.Is.YES)
                        .set(ColumnName.UPDATE_TIME, LocalDateTime.now())
                        .set(ColumnName.UPDATE, new User(operator.getId())),
                Api.class);
    }

    @Override
    public void switchStatus(ApiDto apiDto, UserVo operator) {
        PermUtils.checkProjectWrite(mongoTemplate, apiDto.getProjectId(), UserUtils.getOperator());
        mongoTemplate.updateMulti(new Query(Criteria.where(ColumnName.ID).in(apiDto.getIds())),
                Update.update(ColumnName.API_STATUS, apiDto.getApiStatus())
                        .set(ColumnName.UPDATE, new User(operator.getId()))
                        .set(ColumnName.UPDATE_TIME, LocalDateTime.now()), Api.class);
    }

    @Override
    public List<ApiVo> findReferenceApi(ApiDto apiDto) {
        String structureId = apiDto.getStructureId();
        FastUtils.checkParams(structureId);
        // find used structureIds
        List<StructureData> structureDataList = mongoTemplate.find(new Query(
                Criteria.where(ColumnName.REFERENCE_STRUCTURE_ID).is(structureId)), StructureData.class);
        LinkedHashSet<ObjectId> ids = structureDataList.stream()
                .map(structureData -> new ObjectId(structureData.getStructureId()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        ids.add(new ObjectId(structureId));
        return mongoTemplate.find(
                new Query(Criteria.where(ColumnName.IS_DEL).ne(Constant.Is.YES)
                        .orOperator(Criteria.where(ColumnName.REQUEST_PARAM_$ID).in(ids),
                                Criteria.where(ColumnName.RESPONSE_PARAM_$ID).in(ids))),
                ApiVo.class, Constant.CollectionName.API);
    }

    @Override
    public void moveGroup(ApiDto apiDto, UserVo operator) {
        PermUtils.checkProjectWrite(mongoTemplate, apiDto.getProjectId(), operator);
        mongoTemplate.updateMulti(new Query(Criteria.where(ColumnName.ID).in(apiDto.getIds())),
                Update.update(ColumnName.GROUP, new ApiGroup(apiDto.getGroupId()))
                        .set(ColumnName.UPDATE, new User(operator.getId()))
                        .set(ColumnName.UPDATE_TIME, LocalDateTime.now()), Api.class);
    }

    @Override
    public List<ApiVo> findAllDetail(ApiDto apiDto) {
        String projectId = apiDto.getProjectId();
        FastUtils.checkParams(projectId);
        // get all api
        Query query = new Query(Criteria.where(ColumnName.PROJECT_ID).is(projectId)
                .and(ColumnName.IS_DEL).ne(Constant.Is.YES))
                .with(QSort.by(Sort.Direction.DESC, ColumnName.UPDATE_TIME));
        List<ApiVo> apiVoList = mongoTemplate.find(query, ApiVo.class, Constant.CollectionName.API);
        if (apiVoList.isEmpty()) {
            return apiVoList;
        }
        // get environment environment's header
        ApiEnv env = getEnv(apiDto.getEnvId());
        List<ApiHeader> envHeaders = getEnvHeaders(env);
        boolean addEnvUri = env != null && !StringUtils.isEmpty(env.getFrontUri());
        boolean addEnvHeader = !envHeaders.isEmpty();
        // get all structure
        query = new Query(Criteria.where(ColumnName.PROJECT_ID).is(projectId).and(ColumnName.IS_DEL).ne(Constant.Is.YES));
        List<StructureVo> structureVoList = mongoTemplate.find(query, StructureVo.class, Constant.CollectionName.STRUCTURE);
        // get all structureData
        List<StructureDataDto> structureDataList = mongoTemplate.find(query, StructureDataDto.class, Constant.CollectionName.STRUCTURE_DATA);
        // collect to dict
        Map<String, List<StructureDataDto>> rootListDict = new LinkedHashMap<>();
        Map<String, StructureVo> structureDict = structureVoList.stream().collect(Collectors.toMap(Structure::getId, v -> v));
        Map<String, StructureDataDto> structureDataDict = structureDataList.stream().collect(Collectors.toMap(StructureData::getId, v -> v));
        Map<String, List<ApiHeader>> headersDict = mongoTemplate.find(query, ApiHeader.class).stream().collect(Collectors.groupingBy(ApiHeader::getApiId));
        for (StructureDataDto dataDto : structureDataList) {
            if (StringUtils.isEmpty(dataDto.getParentId())) {
                if (!StringUtils.isEmpty(dataDto.getReferenceStructureId())) {
                    // if reference
                    StructureVo structureVo = structureDict.get(dataDto.getReferenceStructureId());
                    dataDto.setReferenceStructureName(structureVo.getName());
                    dataDto.setSubList(structureVo.getDataList());
                }
                rootListDict.computeIfAbsent(dataDto.getStructureId(), k -> new LinkedList<>()).add(dataDto);
            } else {
                structureDataDict.get(dataDto.getParentId()).getSubList().add(dataDto);
            }
        }
        structureVoList.forEach(structureVo -> {
            List<StructureDataDto> rootList = rootListDict.get(structureVo.getId());
            if (rootList != null) {
                structureVo.getDataList().addAll(rootList);
            }
        });
        apiVoList.forEach(apiVo -> {
            if (apiVo.getRequestParam() != null) {
                apiVo.setRequestParamVo(structureDict.get(apiVo.getRequestParam().getId()));
            }
            if (apiVo.getResponseParam() != null) {
                apiVo.setResponseParamVo(structureDict.get(apiVo.getResponseParam().getId()));
            }
            apiVo.setRequestHeaders(new LinkedHashSet<>());
            apiVo.setResponseHeaders(new LinkedHashSet<>());
            List<ApiHeader> headers = headersDict.get(apiVo.getId());
            if (headers != null) {
                for (ApiHeader header : headers) {
                    if (Constant.Is.YES.equals(header.getIsRequest())) {
                        apiVo.getRequestHeaders().add(header);
                    } else {
                        apiVo.getResponseHeaders().add(header);
                    }
                }
            }
            if (addEnvUri) {
                String apiUri = apiVo.getApiUri();
                if (apiUri == null) {
                    apiUri = "";
                }
                apiVo.setApiUri(env.getFrontUri() + apiUri);
            }
            if (addEnvHeader) {
                apiVo.getRequestHeaders().addAll(envHeaders);
            }
        });
        return apiVoList;
    }

    private List<ApiHeader> getEnvHeaders(@Nullable ApiEnv env) {
        if (env == null) {
            return Collections.emptyList();
        }
        List<ApiHeader> headers = JsonUtils.json2List(env.getEnvHeader(), ApiHeader.class);
        if (headers == null) {
            return Collections.emptyList();
        }
        return headers;
    }

    @Nullable
    private ApiEnv getEnv(String envId) {
        if (StringUtils.isEmpty(envId)) {
            return null;
        }
        return mongoTemplate.findById(envId, ApiEnv.class);
    }

    private String saveApiParams(StructureDto paramDto, UserVo operator, String projectId) {
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
}
