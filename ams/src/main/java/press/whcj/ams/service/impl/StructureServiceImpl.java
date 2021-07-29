package press.whcj.ams.service.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.bson.BsonRegularExpression;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import press.whcj.ams.common.ColumnName;
import press.whcj.ams.common.Constant;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.Structure;
import press.whcj.ams.entity.StructureData;
import press.whcj.ams.entity.User;
import press.whcj.ams.entity.dto.ApiDTO;
import press.whcj.ams.entity.dto.StructureDTO;
import press.whcj.ams.entity.dto.StructureDataDTO;
import press.whcj.ams.entity.vo.ApiVO;
import press.whcj.ams.entity.vo.StructureVO;
import press.whcj.ams.entity.vo.UserVO;
import press.whcj.ams.exception.ResultCode;
import press.whcj.ams.exception.ServiceException;
import press.whcj.ams.service.ApiService;
import press.whcj.ams.service.StructureService;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.PermUtils;
import press.whcj.ams.util.UserUtils;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Service
public class StructureServiceImpl implements StructureService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private ApiService apiService;

    @Override
    public MongoPage<StructureVO> findPage(StructureDTO structureDTO) {
        FastUtils.checkParams(structureDTO.getProjectId());
        MongoPage<StructureVO> page = structureDTO.getPage();
        Criteria criteria = Criteria.where(ColumnName.PROJECT_ID).is(structureDTO.getProjectId())
                .and(ColumnName.TYPE).is(Constant.StructureType.USER_CREATE)
                .and(ColumnName.IS_DEL).ne(Constant.Is.YES);
        String nameOrRemark = structureDTO.getNameOrRemark();
        if (!StringUtils.isEmpty(nameOrRemark)) {
            BsonRegularExpression expression = new BsonRegularExpression("^.*" + nameOrRemark + ".*$", "i");
            criteria = criteria.orOperator(Criteria.where(ColumnName.NAME).regex(expression),
                    Criteria.where(ColumnName.REMARK).regex(expression));
        }
        Query query = new Query(criteria);
        query.with(page.buildPageRequest()).with(QSort.by(Sort.Direction.DESC, ColumnName.UPDATE_TIME));
        long total = mongoTemplate.count(query, Structure.class);
        page.setTotal(total);
        if (total == 0L) {
            return page;
        }
        return page.setRecords(mongoTemplate.find(query, StructureVO.class, Constant.CollectionName.STRUCTURE));
    }

    @Override
    public String save(StructureDTO structureDTO, UserVO operator) {
        boolean isUpdate = !StringUtils.isEmpty(structureDTO.getId());
        String name = structureDTO.getName();
        String projectId = structureDTO.getProjectId();
        FastUtils.checkParams(name, projectId);
        PermUtils.checkProjectWrite(mongoTemplate, projectId, operator);
        Structure structure = buildStructure(structureDTO, isUpdate);
        if (structureDTO.isCheckName()) {
            synchronized (projectId.intern()) {
                FastUtils.checkNameAndSave(structureDTO.getId(), isUpdate, name, structure, mongoTemplate, Criteria.where(ColumnName.PROJECT_ID).is(projectId).and(ColumnName.TYPE).is(Constant.StructureType.USER_CREATE));
            }
        } else {
            mongoTemplate.save(structure);
        }
        String structureId = structure.getId();
        saveStructureData(structureDTO, structureId);
        return structureId;
    }

    private Structure buildStructure(StructureDTO structureDTO, boolean isUpdate) {
        Structure structure;
        if (isUpdate) {
            structure = mongoTemplate.findById(structureDTO.getId(), Structure.class);
            FastUtils.checkNull(structure);
            Objects.requireNonNull(structure).setUpdate(null);
        } else {
            structure = new Structure();
        }
        FastUtils.copyProperties(structureDTO, structure);
        return structure;
    }

    private void saveStructureData(StructureDTO structureDTO, String structureId) {
        mongoTemplate.remove(new Query(Criteria.where(ColumnName.STRUCTURE_ID).is(structureId)), StructureData.class);
        String projectId = structureDTO.getProjectId();
        if (!structureDTO.getDataList().isEmpty()) {
            List<StructureData> structureDataList = new LinkedList<>();
            List<StructureDataDTO> subDataList = new LinkedList<>();
            for (StructureDataDTO structureDataDTO : structureDTO.getDataList()) {
                getStructureData(structureId, projectId, subDataList, structureDataList, structureDataDTO);
            }
            mongoTemplate.insertAll(structureDataList);
            while (!subDataList.isEmpty()) {
                ArrayList<StructureDataDTO> tempSubs = new ArrayList<>(subDataList);
                subDataList.clear();
                List<StructureData> insertSubs = new LinkedList<>();
                for (StructureDataDTO dataDTO : tempSubs) {
                    StructureData insertSub = getStructureData(structureId, projectId, subDataList, insertSubs, dataDTO);
                    insertSub.setParentId(dataDTO.getParent().getId());
                }
                mongoTemplate.insertAll(insertSubs);
            }
        }
    }

    private StructureData getStructureData(String structureId, String projectId, List<StructureDataDTO> dtoList, List<StructureData> insertStructureDataList, StructureDataDTO dataDTO) {
        StructureData insertSub = new StructureData();
        insertStructureDataList.add(insertSub);
        FastUtils.copyProperties(dataDTO, insertSub);
        if (StringUtils.isEmpty(dataDTO.getReferenceStructureId())) {
            collectSubs(dtoList, insertSub, dataDTO);
        }
        return insertSub.setStructureId(structureId).setProjectId(projectId);
    }

    @Override
    public StructureVO findDetail(StructureDTO structureDTO) {
        String structureId = structureDTO.getId();
        FastUtils.checkParams(structureId);
        return getStructureById(structureId);
    }

    @Override
    public StructureVO getStructureById(String structureId) {
        StructureVO detail = mongoTemplate.findById(structureId, StructureVO.class, Constant.CollectionName.STRUCTURE);
        FastUtils.checkNull(detail);
        List<StructureDataDTO> allDataList = mongoTemplate.find(new Query(Criteria.where(ColumnName.STRUCTURE_ID).is(structureId)), StructureDataDTO.class, Constant.CollectionName.STRUCTURE_DATA);
        List<StructureDataDTO> rootList = new LinkedList<>();
        Objects.requireNonNull(detail).setDataList(rootList);
        Map<String, StructureDataDTO> allDataDict = allDataList.stream().collect(Collectors.toMap(StructureData::getId, v -> v));
        for (StructureDataDTO dataDTO : allDataList) {
            if (StringUtils.isEmpty(dataDTO.getParentId())) {
                // 父ID为空，表示为根结点
                rootList.add(dataDTO);
            } else {
                // put sub list
                allDataDict.get(dataDTO.getParentId()).getSubList().add(dataDTO);
            }
            if (!StringUtils.isEmpty(dataDTO.getReferenceStructureId())) {
                // 存在引用数据，放入
                StructureVO structureVO = getStructureById(dataDTO.getReferenceStructureId());
                dataDTO.setReferenceStructureName(structureVO.getName());
                dataDTO.setSubList(structureVO.getDataList());
            }
        }
        return detail;
    }

    @Override
    public void remove(StructureDTO structureDTO) {
        String structureId = structureDTO.getId();
        FastUtils.checkParams(structureId);
        Structure existStructure = mongoTemplate.findById(structureId, Structure.class);
        FastUtils.checkNull(existStructure);
        UserVO operator = UserUtils.getOperator();
        PermUtils.checkProjectWrite(mongoTemplate, Objects.requireNonNull(existStructure).getProjectId(), operator);
        // check can remove?
        ApiDTO apiDTO = new ApiDTO();
        apiDTO.setStructureId(structureId);
        List<ApiVO> referenceApis = apiService.findReferenceApi(apiDTO);
        if (!referenceApis.isEmpty()) {
            throw new ServiceException(ResultCode.STRUCTURE_USED);
        }
        // remove
        existStructure.setIsDel(Constant.Is.YES);
        existStructure.setUpdate(new User(operator.getId()));
        existStructure.setUpdateTime(LocalDateTime.now());
        mongoTemplate.save(existStructure);
    }

    /**
     * collect sub list
     *
     * @param subDataList if has sub, collect to subDataList
     * @param parent      parent
     * @param parentDTO   parentDTO
     * @author xyyxhcj@qq.com
     * @date 2019/12/19 11:34
     **/
    private void collectSubs(final List<StructureDataDTO> subDataList, final StructureData parent, final StructureDataDTO parentDTO) {
        if (!parentDTO.getSubList().isEmpty()) {
            for (StructureDataDTO sub : parentDTO.getSubList()) {
                sub.setParent(parent);
                subDataList.add(sub);
            }
        }
    }
}
