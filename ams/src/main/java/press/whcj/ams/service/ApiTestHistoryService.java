package press.whcj.ams.service;

import press.whcj.ams.entity.ApiTestHistory;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.dto.ApiTestHistoryDto;
import press.whcj.ams.entity.vo.UserVo;

/**
 * @author xyyxhcj@qq.com
 * @since 2020-02-08
 */

public interface ApiTestHistoryService {
    String save(ApiTestHistory apiTestHistory, UserVo operator);

    MongoPage<ApiTestHistory> findPage(ApiTestHistoryDto apiTestHistoryDto);
}
