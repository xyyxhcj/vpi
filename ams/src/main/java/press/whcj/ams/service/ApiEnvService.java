package press.whcj.ams.service;

import press.whcj.ams.entity.ApiEnv;
import press.whcj.ams.entity.vo.UserVo;

import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019-12-30
 */

public interface ApiEnvService {
    String save(ApiEnv apiEnvDto, UserVo operator);

    List<ApiEnv> findList(ApiEnv apiEnvDto);
}
