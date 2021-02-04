package press.whcj.ams.service;

import press.whcj.ams.entity.ApiTestCase;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.dto.ApiTestCaseDTO;
import press.whcj.ams.entity.vo.UserVO;

/**
 * @author xyyxhcj@qq.com
 * @since 2020-06-23
 */

public interface ApiTestCaseService {
    /**
     * save
     *
     * @param apiTestCase apiTestCase
     * @param operator    operator
     * @return java.lang.String
     * @author xyyxhcj@qq.com
     * @date 2020/6/23 11:06
     **/
    String save(ApiTestCase apiTestCase, UserVO operator);

    /**
     * delete
     *
     * @param apiTestCaseDTO apiTestCaseDO
     * @author xyyxhcj@qq.com
     * @date 2020/7/25 8:59
     **/
    void delete(ApiTestCaseDTO apiTestCaseDTO);

    /**
     * findPage
     *
     * @param apiTestCaseDTO apiTestCaseDO
     * @return press.whcj.ams.entity.MongoPage<press.whcj.ams.entity.ApiTestCase>
     * @author xyyxhcj@qq.com
     * @date 2020/7/25 9:01
     **/
    MongoPage<ApiTestCase> findPage(ApiTestCaseDTO apiTestCaseDTO);

    ApiTestCase details(ApiTestCaseDTO apiTestCaseDTO);
}
