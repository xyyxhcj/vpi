package press.whcj.ams.web;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import press.whcj.ams.entity.ApiTestCase;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.dto.ApiTestCaseDTO;
import press.whcj.ams.entity.vo.UserVO;
import press.whcj.ams.service.ApiTestCaseService;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.UserUtils;

/**
 * @author xyyxhcj@qq.com
 * @since 2020/02/06
 */
@RestController
@RequestMapping("apiTestCase")
public class ApiTestCaseController extends BaseController {
    @Resource
    private ApiTestCaseService apiTestCaseService;
    @PostMapping("add")
    public Result<String> add(@RequestBody ApiTestCase apiTestCase) {
        UserVO operator = UserUtils.getOperator();
        apiTestCase.initCreate(operator);
        String id = apiTestCaseService.save(apiTestCase, operator);
        return ok(id);
    }

    @PostMapping("edit")
    public Result<String> edit(@RequestBody ApiTestCase apiTestCase) {
        FastUtils.checkParams(apiTestCase.getId());
        UserVO operator = UserUtils.getOperator();
        apiTestCase.initUpdate(operator);
        apiTestCaseService.save(apiTestCase, operator);
        return ok(apiTestCase.getId());
    }

    @PostMapping("delete")
    public Result<Object> delete(@RequestBody ApiTestCaseDTO apiTestCaseDTO) {
        apiTestCaseService.delete(apiTestCaseDTO);
        return ok();
    }

    @PostMapping("findPage")
    public Result<MongoPage<ApiTestCase>> findPage(@RequestBody ApiTestCaseDTO apiTestCaseDTO) {
        return ok(apiTestCaseService.findPage(apiTestCaseDTO));
    }

    @PostMapping("detail")
    public Result<Object> findDetail(@RequestBody ApiTestCaseDTO apiTestCaseDTO){
        return ok(apiTestCaseService.findDetail(apiTestCaseDTO));
    }

}
