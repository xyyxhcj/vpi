package press.whcj.ams.service;


import java.util.List;

import press.whcj.ams.entity.ApiGroup;
import press.whcj.ams.entity.vo.ApiGroupVO;
import press.whcj.ams.entity.vo.UserVO;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public interface ApiGroupService {
    /**
     * findList
     *
     * @param apiGroupDTO apiGroupDTO
     * @return java.util.List<press.whcj.ams.entity.vo.ApiGroupVO>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    List<ApiGroupVO> findList(ApiGroup apiGroupDTO);

    /**
     * save
     *
     * @param apiGroupDTO apiGroupDTO
     * @param operator    operator
     * @return java.lang.String
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    String save(ApiGroup apiGroupDTO, UserVO operator);

    /**
     * delete
     *
     * @param apiGroupDTO apiGroupDTO
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    void delete(ApiGroup apiGroupDTO);
}
