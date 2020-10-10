package press.whcj.ams.service;

import java.util.List;

import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.dto.ApiDTO;
import press.whcj.ams.entity.vo.ApiVO;
import press.whcj.ams.entity.vo.UserVO;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public interface ApiService {
    /**
     * save
     *
     * @param apiDTO   apiDTO
     * @param operator operator
     * @return java.lang.String
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    String save(ApiDTO apiDTO, UserVO operator);

    /**
     * findPage
     *
     * @param apiDTO apiDTO
     * @return press.whcj.ams.entity.MongoPage<press.whcj.ams.entity.vo.ApiVO>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    MongoPage<ApiVO> findPage(ApiDTO apiDTO);

    /**
     * findDetail
     *
     * @param apiDTO apiDTO
     * @return press.whcj.ams.entity.vo.ApiVO
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    ApiVO findDetail(ApiDTO apiDTO);

    /**
     * saveMock
     *
     * @param apiDTO apiDTO
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    void saveMock(ApiDTO apiDTO);

    /**
     * remove
     *
     * @param apiDTO apiDTO
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    void remove(ApiDTO apiDTO);

    /**
     * switchStatus
     *
     * @param apiDTO   apiDTO
     * @param operator operator
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    void switchStatus(ApiDTO apiDTO, UserVO operator);

    /**
     * findReferenceApi
     *
     * @param apiDTO apiDTO
     * @return java.util.List<press.whcj.ams.entity.vo.ApiVO>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    List<ApiVO> findReferenceApi(ApiDTO apiDTO);

    /**
     * moveGroup
     *
     * @param apiDTO   apiDTO
     * @param operator operator
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    void moveGroup(ApiDTO apiDTO, UserVO operator);

    /**
     * findAllDetail
     *
     * @param apiDTO apiDTO
     * @return java.util.List<press.whcj.ams.entity.vo.ApiVO>
     * @author xyyxhcj@qq.com
     * @date 2020/5/22 9:11
     **/
    List<ApiVO> findAllDetail(ApiDTO apiDTO);
}
