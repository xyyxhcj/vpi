package press.whcj.ams.service.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

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
import press.whcj.ams.entity.dto.ApiDTO;
import press.whcj.ams.entity.dto.StructureDTO;
import press.whcj.ams.entity.dto.StructureDataDTO;
import press.whcj.ams.entity.vo.ApiVO;
import press.whcj.ams.entity.vo.StructureVO;
import press.whcj.ams.entity.vo.UserVO;
import press.whcj.ams.service.ApiService;
import press.whcj.ams.service.StructureService;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.JsonUtils;
import press.whcj.ams.util.PermUtils;
import press.whcj.ams.util.UserUtils;

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
    public String save(ApiDTO apiDTO, UserVO operator) {
        boolean isUpdate = apiDTO.getId() != null;
        String name = apiDTO.getName();
        String projectId = apiDTO.getProjectId();
        FastUtils.checkParams(name, projectId);
        PermUtils.checkProjectWrite(mongoTemplate, projectId, operator);
        Api api;
        if (isUpdate) {
            api = mongoTemplate.findById(apiDTO.getId(), Api.class);
            FastUtils.checkNull(api);
            Objects.requireNonNull(api).setUpdate(null);
        } else {
            api = new Api();
        }
        FastUtils.copyProperties(apiDTO, api);
        if (StringUtils.isEmpty(apiDTO.getGroupId())) {
            api.setGroup(null);
        } else {
            api.setGroup(new ApiGroup(apiDTO.getGroupId()));
        }
        // save request params
        String reqParamsId = apiDTO.getRequestParamDTO().getId();
        if (apiDTO.getRequestParamDTO().isReference()) {
            api.setReqParamIsReference(true);
        }
        if (!api.isReqParamIsReference()) {
            reqParamsId = saveApiParams(apiDTO.getRequestParamDTO(), operator, projectId);
        }
        api.setRequestParam(new Structure(reqParamsId));
        // save response params
        String respParamsId = apiDTO.getResponseParamDTO().getId();
        if (apiDTO.getResponseParamDTO().isReference()) {
            api.setRespParamIsReference(true);
        }
        if (!api.isRespParamIsReference()) {
            respParamsId = saveApiParams(apiDTO.getResponseParamDTO(), operator, projectId);
        }
        api.setResponseParam(new Structure(respParamsId));
        mongoTemplate.save(api);
        String apiId = api.getId();
        if (isUpdate) {
            mongoTemplate.remove(new Query(Criteria.where(ColumnName.API_ID).is(apiId)), ApiHeader.class);
        }
        if (!CollectionUtils.isEmpty(apiDTO.getRequestHeaders())) {
            for (ApiHeader header : apiDTO.getRequestHeaders()) {
                header.setApiId(apiId);
                header.setIsRequest(Constant.Is.YES);
            }
            mongoTemplate.insertAll(apiDTO.getRequestHeaders());
        }
        if (!CollectionUtils.isEmpty(apiDTO.getResponseHeaders())) {
            for (ApiHeader header : apiDTO.getResponseHeaders()) {
                header.setApiId(apiId);
                header.setIsRequest(Constant.Is.NO);
            }
            mongoTemplate.insertAll(apiDTO.getResponseHeaders());
        }
        return apiId;
    }

    @Override
    public MongoPage<ApiVO> findPage(ApiDTO apiDTO) {
        FastUtils.checkParams(apiDTO.getProjectId());
        MongoPage<ApiVO> page = apiDTO.getPage();
        Criteria criteria = Criteria.where(ColumnName.PROJECT_ID).is(apiDTO.getProjectId())
                .and(ColumnName.IS_DEL).ne(Constant.Is.YES);
        // concat query condition
        if (!apiDTO.getGroupIds().isEmpty()) {
            List<ObjectId> ids = apiDTO.getGroupIds().stream().map(ObjectId::new).collect(Collectors.toList());
            criteria = criteria.and(ColumnName.GROUP_$ID).in(ids);
        }
        if (!StringUtils.isEmpty(apiDTO.getNameOrUri())) {
            BsonRegularExpression expression = new BsonRegularExpression("^.*" + apiDTO.getNameOrUri() + ".*$", "i");
            criteria = criteria.orOperator(Criteria.where(ColumnName.NAME).regex(expression),
                    Criteria.where(ColumnName.API_URI).regex(expression));
        }
        if (apiDTO.getApiStatus() != null) {
            criteria = criteria.and(ColumnName.API_STATUS).is(apiDTO.getApiStatus());
        }
        Query query = new Query(criteria);
        query.with(page.buildPageRequest()).with(QSort.by(Sort.Direction.DESC, ColumnName.UPDATE_TIME));
        long total = mongoTemplate.count(query, Api.class);
        page.setTotal(total);
        if (total == 0L) {
            return page;
        }
        return page.setRecords(mongoTemplate.find(query, ApiVO.class, Constant.CollectionName.API));
    }

    @Override
    public ApiVO findDetail(ApiDTO apiDTO) {
        String apiId = apiDTO.getId();
        FastUtils.checkParams(apiId);
        ApiVO detail = mongoTemplate.findById(apiId, ApiVO.class, Constant.CollectionName.API);
        FastUtils.checkNull(detail);
        if (Objects.requireNonNull(detail).getRequestParam() != null) {
            detail.setRequestParamVO(structureService.getStructureVOById(detail.getRequestParam().getId()));
        }
        if (detail.getResponseParam() != null) {
            detail.setResponseParamVO(structureService.getStructureVOById(detail.getResponseParam().getId()));
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
    public void saveMock(ApiDTO apiDTO) {
        FastUtils.checkParams(apiDTO.getId());
        String updateColumn;
        String mock;
        if (!StringUtils.isEmpty(apiDTO.getApiSuccessMock())) {
            mock = apiDTO.getApiSuccessMock();
            updateColumn = ColumnName.API_SUCCESS_MOCK;
        } else {
            FastUtils.checkParams(apiDTO.getApiFailureMock());
            mock = apiDTO.getApiFailureMock();
            updateColumn = ColumnName.API_FAILURE_MOCK;
        }
        mongoTemplate.updateFirst(new Query(Criteria.where(ColumnName.ID).is(apiDTO.getId())), Update.update(updateColumn, mock), Api.class);
    }

    @Override
    public void remove(ApiDTO apiDTO) {
        List<String> ids = apiDTO.getIds();
        FastUtils.checkParams(ids);
        UserVO operator = UserUtils.getOperator();
        PermUtils.checkProjectWrite(mongoTemplate, apiDTO.getProjectId(), operator);
        mongoTemplate.updateMulti(new Query(Criteria.where(ColumnName.ID).in(ids)),
                Update.update(ColumnName.IS_DEL, Constant.Is.YES)
                        .set(ColumnName.UPDATE_TIME, LocalDateTime.now())
                        .set(ColumnName.UPDATE, new User(operator.getId())),
                Api.class);
    }

    @Override
    public void switchStatus(ApiDTO apiDTO, UserVO operator) {
        PermUtils.checkProjectWrite(mongoTemplate, apiDTO.getProjectId(), UserUtils.getOperator());
        mongoTemplate.updateMulti(new Query(Criteria.where(ColumnName.ID).in(apiDTO.getIds())),
                Update.update(ColumnName.API_STATUS, apiDTO.getApiStatus())
                        .set(ColumnName.UPDATE, new User(operator.getId()))
                        .set(ColumnName.UPDATE_TIME, LocalDateTime.now()), Api.class);
    }

    @Override
    public List<ApiVO> findReferenceApi(ApiDTO apiDTO) {
        String structureId = apiDTO.getStructureId();
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
                ApiVO.class, Constant.CollectionName.API);
    }

    @Override
    public void moveGroup(ApiDTO apiDTO, UserVO operator) {
        PermUtils.checkProjectWrite(mongoTemplate, apiDTO.getProjectId(), operator);
        mongoTemplate.updateMulti(new Query(Criteria.where(ColumnName.ID).in(apiDTO.getIds())),
                Update.update(ColumnName.GROUP, new ApiGroup(apiDTO.getGroupId()))
                        .set(ColumnName.UPDATE, new User(operator.getId()))
                        .set(ColumnName.UPDATE_TIME, LocalDateTime.now()), Api.class);
    }

    @Override
    public List<ApiVO> findAllDetail(ApiDTO apiDTO) {
        String projectId = apiDTO.getProjectId();
        FastUtils.checkParams(projectId);
        // get all api
        Query query = new Query(Criteria.where(ColumnName.PROJECT_ID).is(projectId)
                .and(ColumnName.IS_DEL).ne(Constant.Is.YES))
                .with(QSort.by(Sort.Direction.DESC, ColumnName.UPDATE_TIME));
        List<ApiVO> apiVOList = mongoTemplate.find(query, ApiVO.class, Constant.CollectionName.API);
        if (apiVOList.isEmpty()) {
            return apiVOList;
        }
        // get environment environment's header
        ApiEnv env = getEnv(apiDTO.getEnvId());
        List<ApiHeader> envHeaders = getEnvHeaders(env);
        boolean addEnvUri = env != null && !StringUtils.isEmpty(env.getFrontUri());
        boolean addEnvHeader = !envHeaders.isEmpty();
        // get all structure
        query = new Query(Criteria.where(ColumnName.PROJECT_ID).is(projectId).and(ColumnName.IS_DEL).ne(Constant.Is.YES));
        List<StructureVO> structureVOList = mongoTemplate.find(query, StructureVO.class, Constant.CollectionName.STRUCTURE);
        // get all structureData
        List<StructureDataDTO> structureDataList = mongoTemplate.find(query, StructureDataDTO.class, Constant.CollectionName.STRUCTURE_DATA);
        // collect to dict
        Map<String, List<StructureDataDTO>> rootListDict = new LinkedHashMap<>();
        Map<String, StructureVO> structureDict = structureVOList.stream().collect(Collectors.toMap(Structure::getId, v -> v));
        Map<String, StructureDataDTO> structureDataDict = structureDataList.stream().collect(Collectors.toMap(StructureData::getId, v -> v));
        Map<String, List<ApiHeader>> headersDict = mongoTemplate.find(query, ApiHeader.class).stream().collect(Collectors.groupingBy(ApiHeader::getApiId));
        for (StructureDataDTO dataDTO : structureDataList) {
            if (StringUtils.isEmpty(dataDTO.getParentId())) {
                if (!StringUtils.isEmpty(dataDTO.getReferenceStructureId())) {
                    // if reference
                    StructureVO structureVO = structureDict.get(dataDTO.getReferenceStructureId());
                    dataDTO.setReferenceStructureName(structureVO.getName());
                    dataDTO.setSubList(structureVO.getDataList());
                }
                rootListDict.computeIfAbsent(dataDTO.getStructureId(), k -> new LinkedList<>()).add(dataDTO);
            } else {
                structureDataDict.get(dataDTO.getParentId()).getSubList().add(dataDTO);
            }
        }
        structureVOList.forEach(structureVO -> {
            List<StructureDataDTO> rootList = rootListDict.get(structureVO.getId());
            if (rootList != null) {
                structureVO.getDataList().addAll(rootList);
            }
        });
        apiVOList.forEach(apiVO -> {
            if (apiVO.getRequestParam() != null) {
                apiVO.setRequestParamVO(structureDict.get(apiVO.getRequestParam().getId()));
            }
            if (apiVO.getResponseParam() != null) {
                apiVO.setResponseParamVO(structureDict.get(apiVO.getResponseParam().getId()));
            }
            apiVO.setRequestHeaders(new LinkedHashSet<>());
            apiVO.setResponseHeaders(new LinkedHashSet<>());
            List<ApiHeader> headers = headersDict.get(apiVO.getId());
            if (headers != null) {
                for (ApiHeader header : headers) {
                    if (Constant.Is.YES.equals(header.getIsRequest())) {
                        apiVO.getRequestHeaders().add(header);
                    } else {
                        apiVO.getResponseHeaders().add(header);
                    }
                }
            }
            if (addEnvUri) {
                String apiUri = apiVO.getApiUri();
                if (apiUri == null) {
                    apiUri = "";
                }
                apiVO.setApiUri(env.getFrontUri() + apiUri);
            }
            if (addEnvHeader) {
                apiVO.getRequestHeaders().addAll(envHeaders);
            }
        });
        return apiVOList;
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

    private String saveApiParams(StructureDTO paramDTO, UserVO operator, String projectId) {
        if (StringUtils.isEmpty(paramDTO.getId())) {
            paramDTO.setType(Constant.StructureType.API_CREATE);
        }
        paramDTO.setProjectId(projectId);
        paramDTO.setCheckName(false);
        paramDTO.setName(UUID.randomUUID().toString());
        LocalDateTime now = LocalDateTime.now();
        User update = new User(operator.getId());
        if (StringUtils.isEmpty(paramDTO.getId())) {
            paramDTO.setCreate(update);
            paramDTO.setCreateTime(now);
            paramDTO.setUpdateTime(now);
        } else {
            paramDTO.setUpdate(update);
            paramDTO.setUpdateTime(now);
        }
        return structureService.save(paramDTO, operator);
    }
}
