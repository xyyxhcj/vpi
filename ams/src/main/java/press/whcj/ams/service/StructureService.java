package press.whcj.ams.service;

import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.dto.StructureDTO;
import press.whcj.ams.entity.vo.StructureVO;
import press.whcj.ams.entity.vo.UserVO;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public interface StructureService {
    /**
     * findPage
     *
     * @param structureDTO structureDTO
     * @return press.whcj.ams.entity.MongoPage<press.whcj.ams.entity.vo.StructureVO>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:31
     **/
    MongoPage<StructureVO> findPage(StructureDTO structureDTO);

    /**
     * save
     *
     * @param structureDTO structureDTO
     * @param operator     operator
     * @return java.lang.String
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:31
     **/
    String save(StructureDTO structureDTO, UserVO operator);

    /**
     * findDetail
     *
     * @param structureDTO structureDTO
     * @return press.whcj.ams.entity.vo.StructureVO
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:31
     **/
    StructureVO findDetail(StructureDTO structureDTO);

    /**
     * getStructureVOById
     *
     * @param structureId structureId
     * @return press.whcj.ams.entity.vo.StructureVO
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:31
     **/
    StructureVO getStructureById(String structureId);

    /**
     * remove
     *
     * @param structureDTO structureDTO
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:31
     **/
    void remove(StructureDTO structureDTO);
}
