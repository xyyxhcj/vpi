package press.whcj.ams.service;


import press.whcj.ams.entity.ApiGroup;
import press.whcj.ams.entity.vo.ApiGroupVo;
import press.whcj.ams.entity.vo.UserVo;

import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public interface ApiGroupService {
    /**
     * findList
     *
     * @param apiGroupDto apiGroupDto
     * @return java.util.List<press.whcj.ams.entity.vo.ApiGroupVo>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    List<ApiGroupVo> findList(ApiGroup apiGroupDto);

    /**
     * save
     *
     * @param apiGroupDto apiGroupDto
     * @param operator    operator
     * @return java.lang.String
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    String save(ApiGroup apiGroupDto, UserVo operator);

    /**
     * delete
     *
     * @param apiGroupDto apiGroupDto
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    void delete(ApiGroup apiGroupDto);
}
