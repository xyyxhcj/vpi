package press.whcj.ams.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.dto.ApiDTO;
import press.whcj.ams.entity.vo.ApiVO;
import press.whcj.ams.entity.vo.UserVO;
import press.whcj.ams.service.ApiService;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.UserUtils;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@RestController
@RequestMapping("api")
public class ApiController extends BaseController {
    @Resource
    private ApiService apiService;

    @PostMapping("add")
    public Result<String> add(@RequestBody ApiDTO apiDTO) {
        UserVO operator = UserUtils.getOperator();
        apiDTO.initCreate(operator);
        String id = apiService.save(apiDTO, operator);
        return ok(id);
    }

    @PostMapping("saveMock")
    public Result<String> saveMock(@RequestBody ApiDTO apiDTO) {
        apiService.saveMock(apiDTO);
        return ok();
    }

    @PostMapping("edit")
    public Result<String> edit(@RequestBody ApiDTO apiDTO) {
        FastUtils.checkParams(apiDTO.getId());
        UserVO operator = UserUtils.getOperator();
        apiDTO.initUpdate(operator);
        apiService.save(apiDTO, operator);
        return ok(apiDTO.getId());
    }

    @PostMapping("switchStatus")
    public Result<Object> switchStatus(@RequestBody ApiDTO apiDTO) {
        FastUtils.checkParams(apiDTO.getApiStatus(), apiDTO.getIds(), apiDTO.getProjectId());
        apiService.switchStatus(apiDTO, UserUtils.getOperator());
        return ok();
    }

    @PostMapping("moveGroup")
    public Result<Object> moveGroup(@RequestBody ApiDTO apiDTO) {
        FastUtils.checkParams(apiDTO.getGroupId(), apiDTO.getIds(), apiDTO.getProjectId());
        apiService.moveGroup(apiDTO, UserUtils.getOperator());
        return ok();
    }

    @PostMapping("findPage")
    public Result<MongoPage<ApiVO>> findPage(@RequestBody ApiDTO apiDTO) {
        return ok(apiService.findPage(apiDTO));
    }

    @PostMapping("findDetail")
    public Result<ApiVO> findDetail(@RequestBody ApiDTO apiDTO) {
        return ok(apiService.findDetail(apiDTO));
    }

    @PostMapping("vpi/findAllDetail")
    public Result<List<ApiVO>> findAllDetail(@RequestBody ApiDTO apiDTO) {
        return ok(apiService.findAllDetail(apiDTO));
    }

    @PostMapping("remove")
    public Result<Object> remove(@RequestBody ApiDTO apiDTO) {
        apiService.remove(apiDTO);
        return ok();
    }

    @PostMapping("findReferenceApi")
    public Result<List<ApiVO>> findReferenceApi(@RequestBody ApiDTO apiDTO) {
        return ok(apiService.findReferenceApi(apiDTO));
    }
}
