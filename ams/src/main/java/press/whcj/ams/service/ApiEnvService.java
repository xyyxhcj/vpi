package press.whcj.ams.service;

import java.util.List;

import press.whcj.ams.entity.ApiEnv;
import press.whcj.ams.entity.vo.UserVO;

/**
 * @author xyyxhcj@qq.com
 * @since 2019-12-30
 */

public interface ApiEnvService {
    /**
     * save
     *
     * @param apiEnvDTO apiEnvDTO
     * @param operator  operator
     * @return java.lang.String
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:35
     **/
    String save(ApiEnv apiEnvDTO, UserVO operator);

    /**
     * findList
     *
     * @param apiEnvDTO apiEnvDTO
     * @return java.util.List<press.whcj.ams.entity.ApiEnv>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:35
     **/
    List<ApiEnv> findList(ApiEnv apiEnvDTO);

    /**
     * delete
     *
     * @param apiEnvDTO apiEnvDTO
     * @param operator  operator
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:35
     **/
    void delete(ApiEnv apiEnvDTO, UserVO operator);
}
