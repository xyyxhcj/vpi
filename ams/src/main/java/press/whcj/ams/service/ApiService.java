package press.whcj.ams.service;

import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.dto.ApiDto;
import press.whcj.ams.entity.vo.ApiVo;
import press.whcj.ams.entity.vo.UserVo;

import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public interface ApiService {
    /**
     * save
     *
     * @param apiDto   apiDto
     * @param operator operator
     * @return java.lang.String
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    String save(ApiDto apiDto, UserVo operator);

    /**
     * findPage
     *
     * @param apiDto apiDto
     * @return press.whcj.ams.entity.MongoPage<press.whcj.ams.entity.vo.ApiVo>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    MongoPage<ApiVo> findPage(ApiDto apiDto);

    /**
     * findDetail
     *
     * @param apiDto apiDto
     * @return press.whcj.ams.entity.vo.ApiVo
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    ApiVo findDetail(ApiDto apiDto);

    /**
     * saveMock
     *
     * @param apiDto apiDto
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    void saveMock(ApiDto apiDto);

    /**
     * remove
     *
     * @param apiDto apiDto
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    void remove(ApiDto apiDto);

    /**
     * switchStatus
     *
     * @param apiDto   apiDto
     * @param operator operator
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    void switchStatus(ApiDto apiDto, UserVo operator);

    /**
     * findReferenceApi
     *
     * @param apiDto apiDto
     * @return java.util.List<press.whcj.ams.entity.vo.ApiVo>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    List<ApiVo> findReferenceApi(ApiDto apiDto);

    /**
     * moveGroup
     *
     * @param apiDto   apiDto
     * @param operator operator
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    void moveGroup(ApiDto apiDto, UserVo operator);

    /**
     * findAllDetail
     *
     * @param apiDto apiDto
     * @return java.util.List<press.whcj.ams.entity.vo.ApiVo>
     * @author xyyxhcj@qq.com
     * @date 2020/5/22 9:11
     **/
    List<ApiVo> findAllDetail(ApiDto apiDto);
}
