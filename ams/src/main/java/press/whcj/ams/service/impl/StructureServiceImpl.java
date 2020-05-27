package press.whcj.ams.service.impl;

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
import press.whcj.ams.entity.dto.ApiDto;
import press.whcj.ams.entity.dto.StructureDataDto;
import press.whcj.ams.entity.dto.StructureDto;
import press.whcj.ams.entity.vo.ApiVo;
import press.whcj.ams.entity.vo.StructureVo;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.exception.ResultCode;
import press.whcj.ams.exception.ServiceException;
import press.whcj.ams.service.ApiService;
import press.whcj.ams.service.StructureService;
import press.whcj.ams.util.FastUtils;
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
public class StructureServiceImpl implements StructureService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private ApiService apiService;

    @Override
    public MongoPage<StructureVo> findPage(StructureDto structureDto) {
        FastUtils.checkParams(structureDto.getProjectId());
        MongoPage<StructureVo> page = structureDto.getPage();
        Query query = new Query(Criteria.where(ColumnName.PROJECT_ID).is(structureDto.getProjectId())
                .and(ColumnName.TYPE).is(Constant.StructureType.USER_CREATE)
                .and(ColumnName.IS_DEL).ne(Constant.Is.YES));
        query.with(page.buildPageRequest()).with(QSort.by(Sort.Direction.DESC, ColumnName.UPDATE_TIME));
        long total = mongoTemplate.count(query, Structure.class);
        page.setTotal(total);
        if (total == 0L) {
            return page;
        }
        return page.setRecords(mongoTemplate.find(query, StructureVo.class, Constant.CollectionName.STRUCTURE));
    }

    @Override
    public String save(StructureDto structureDto, UserVo operator) {
        boolean isUpdate = !StringUtils.isEmpty(structureDto.getId());
        String name = structureDto.getName();
        String projectId = structureDto.getProjectId();
        FastUtils.checkParams(name, projectId);
        PermUtils.checkProjectWrite(mongoTemplate, projectId, operator);
        Structure structure = buildStructure(structureDto, isUpdate);
        if (structureDto.isCheckName()) {
            synchronized (projectId.intern()) {
                FastUtils.checkNameAndSave(structureDto.getId(), isUpdate, name, structure, mongoTemplate, Criteria.where(ColumnName.PROJECT_ID).is(projectId).and(ColumnName.TYPE).is(Constant.StructureType.USER_CREATE));
            }
        } else {
            mongoTemplate.save(structure);
        }
        String structureId = structure.getId();
        saveStructureData(structureDto, structureId);
        return structureId;
    }

    private Structure buildStructure(StructureDto structureDto, boolean isUpdate) {
        Structure structure;
        if (isUpdate) {
            structure = mongoTemplate.findById(structureDto.getId(), Structure.class);
            FastUtils.checkNull(structure);
            Objects.requireNonNull(structure).setUpdate(null);
        } else {
            structure = new Structure();
        }
        FastUtils.copyProperties(structureDto, structure);
        return structure;
    }

    private void saveStructureData(StructureDto structureDto, String structureId) {
        mongoTemplate.remove(new Query(Criteria.where(ColumnName.STRUCTURE_ID).is(structureId)), StructureData.class);
        String projectId = structureDto.getProjectId();
        if (!structureDto.getDataList().isEmpty()) {
            List<StructureData> structureDataList = new LinkedList<>();
            List<StructureDataDto> subDataList = new LinkedList<>();
            for (StructureDataDto structureDataDto : structureDto.getDataList()) {
                getStructureData(structureId, projectId, subDataList, structureDataList, structureDataDto);
            }
            mongoTemplate.insertAll(structureDataList);
            while (!subDataList.isEmpty()) {
                ArrayList<StructureDataDto> tempSubs = new ArrayList<>(subDataList);
                subDataList.clear();
                List<StructureData> insertSubs = new LinkedList<>();
                for (StructureDataDto dataDto : tempSubs) {
                    StructureData insertSub = getStructureData(structureId, projectId, subDataList, insertSubs, dataDto);
                    insertSub.setParentId(dataDto.getParent().getId());
                }
                mongoTemplate.insertAll(insertSubs);
            }
        }
    }

    private StructureData getStructureData(String structureId, String projectId, List<StructureDataDto> dtoList, List<StructureData> insertStructureDataList, StructureDataDto dataDto) {
        StructureData insertSub = new StructureData();
        insertStructureDataList.add(insertSub);
        FastUtils.copyProperties(dataDto, insertSub);
        if (StringUtils.isEmpty(dataDto.getReferenceStructureId())) {
            collectSubs(dtoList, insertSub, dataDto);
        }
        return insertSub.setStructureId(structureId).setProjectId(projectId);
    }

    @Override
    public StructureVo findDetail(StructureDto structureDto) {
        String structureId = structureDto.getId();
        FastUtils.checkParams(structureId);
        return getStructureVoById(structureId);
    }

    @Override
    public StructureVo getStructureVoById(String structureId) {
        StructureVo detail = mongoTemplate.findById(structureId, StructureVo.class, Constant.CollectionName.STRUCTURE);
        FastUtils.checkNull(detail);
        List<StructureDataDto> allDataList = mongoTemplate.find(new Query(Criteria.where(ColumnName.STRUCTURE_ID).is(structureId)), StructureDataDto.class, Constant.CollectionName.STRUCTURE_DATA);
        List<StructureDataDto> rootList = new LinkedList<>();
        Objects.requireNonNull(detail).setDataList(rootList);
        Map<String, StructureDataDto> allDataDict = allDataList.stream().collect(Collectors.toMap(StructureData::getId, v -> v));
        for (StructureDataDto dataDto : allDataList) {
            if (StringUtils.isEmpty(dataDto.getParentId())) {
                if (!StringUtils.isEmpty(dataDto.getReferenceStructureId())) {
                    StructureVo structureVo = getStructureVoById(dataDto.getReferenceStructureId());
                    dataDto.setReferenceStructureName(structureVo.getName());
                    dataDto.setSubList(structureVo.getDataList());
                }
                rootList.add(dataDto);
            } else {
                // put sub list
                allDataDict.get(dataDto.getParentId()).getSubList().add(dataDto);
            }
        }
        return detail;
    }

    @Override
    public void remove(StructureDto structureDto) {
        String structureId = structureDto.getId();
        FastUtils.checkParams(structureId);
        Structure existStructure = mongoTemplate.findById(structureId, Structure.class);
        FastUtils.checkNull(existStructure);
        UserVo operator = UserUtils.getOperator();
        PermUtils.checkProjectWrite(mongoTemplate, Objects.requireNonNull(existStructure).getProjectId(), operator);
        // check can remove?
        ApiDto apiDto = new ApiDto();
        apiDto.setStructureId(structureId);
        List<ApiVo> referenceApis = apiService.findReferenceApi(apiDto);
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
     * @param parentDto   parentDto
     * @author xyyxhcj@qq.com
     * @date 2019/12/19 11:34
     **/
    private void collectSubs(final List<StructureDataDto> subDataList, final StructureData parent, final StructureDataDto parentDto) {
        if (!parentDto.getSubList().isEmpty()) {
            for (StructureDataDto sub : parentDto.getSubList()) {
                sub.setParent(parent);
                subDataList.add(sub);
            }
        }
    }
}
