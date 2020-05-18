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
    /**
     * save
     *
     * @param apiTestHistory apiTestHistory
     * @param operator       operator
     * @return java.lang.String
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:33
     **/
    String save(ApiTestHistory apiTestHistory, UserVo operator);

    /**
     * findPage
     *
     * @param apiTestHistoryDto apiTestHistoryDto
     * @return press.whcj.ams.entity.MongoPage<press.whcj.ams.entity.ApiTestHistory>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:33
     **/
    MongoPage<ApiTestHistory> findPage(ApiTestHistoryDto apiTestHistoryDto);

    /**
     * delete
     *
     * @param apiTestHistoryDto apiTestHistoryDto
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    void delete(ApiTestHistoryDto apiTestHistoryDto);
}
