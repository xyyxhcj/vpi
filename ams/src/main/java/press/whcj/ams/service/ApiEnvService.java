package press.whcj.ams.service;

import press.whcj.ams.entity.ApiEnv;
import press.whcj.ams.entity.vo.UserVo;

import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019-12-30
 */

public interface ApiEnvService {
    /**
     * save
     *
     * @param apiEnvDto apiEnvDto
     * @param operator  operator
     * @return java.lang.String
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:35
     **/
    String save(ApiEnv apiEnvDto, UserVo operator);

    /**
     * findList
     *
     * @param apiEnvDto apiEnvDto
     * @return java.util.List<press.whcj.ams.entity.ApiEnv>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:35
     **/
    List<ApiEnv> findList(ApiEnv apiEnvDto);

    /**
     * delete
     *
     * @param apiEnvDto apiEnvDto
     * @param operator  operator
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:35
     **/
    void delete(ApiEnv apiEnvDto, UserVo operator);
}
