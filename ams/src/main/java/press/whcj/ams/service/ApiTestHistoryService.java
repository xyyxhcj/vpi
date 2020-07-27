package press.whcj.ams.service;

import press.whcj.ams.entity.ApiTestHistory;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.dto.ApiTestHistoryDTO;
import press.whcj.ams.entity.vo.UserVO;

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
    String save(ApiTestHistory apiTestHistory, UserVO operator);

    /**
     * findPage
     *
     * @param apiTestHistoryDTO apiTestHistoryDTO
     * @return press.whcj.ams.entity.MongoPage<press.whcj.ams.entity.ApiTestHistory>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:33
     **/
    MongoPage<ApiTestHistory> findPage(ApiTestHistoryDTO apiTestHistoryDTO);

    /**
     * delete
     *
     * @param apiTestHistoryDTO apiTestHistoryDTO
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:34
     **/
    void delete(ApiTestHistoryDTO apiTestHistoryDTO);
}
