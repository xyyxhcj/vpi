package press.whcj.ams.service;

import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.dto.StructureDto;
import press.whcj.ams.entity.vo.StructureVo;
import press.whcj.ams.entity.vo.UserVo;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public interface StructureService {
    /**
     * findPage
     *
     * @param structureDto structureDto
     * @return press.whcj.ams.entity.MongoPage<press.whcj.ams.entity.vo.StructureVo>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:31
     **/
    MongoPage<StructureVo> findPage(StructureDto structureDto);

    /**
     * save
     *
     * @param structureDto structureDto
     * @param operator     operator
     * @return java.lang.String
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:31
     **/
    String save(StructureDto structureDto, UserVo operator);

    /**
     * findDetail
     *
     * @param structureDto structureDto
     * @return press.whcj.ams.entity.vo.StructureVo
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:31
     **/
    StructureVo findDetail(StructureDto structureDto);

    /**
     * getStructureVoById
     *
     * @param structureId structureId
     * @return press.whcj.ams.entity.vo.StructureVo
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:31
     **/
    StructureVo getStructureVoById(String structureId);

    /**
     * remove
     *
     * @param structureDto structureDto
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:31
     **/
    void remove(StructureDto structureDto);
}
