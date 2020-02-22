package press.whcj.ams.web;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.dto.ApiDto;
import press.whcj.ams.entity.vo.ApiVo;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.service.ApiService;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.UserUtils;

import javax.annotation.Resource;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@RestController
@RequestMapping("api")
public class ApiController extends BaseController {
	@Resource
	private ApiService apiService;

	@RequestMapping("add")
	public Result<String> add(@RequestBody ApiDto apiDto) {
		UserVo operator = UserUtils.getOperator();
		apiDto.initCreate(operator);
		String id = apiService.save(apiDto, operator);
		return ok(id);
	}

	@RequestMapping("saveMock")
	public Result<String> saveMock(@RequestBody ApiDto apiDto) {
		apiService.saveMock(apiDto);
		return ok();
	}

	@RequestMapping("edit")
	public Result<String> edit(@RequestBody ApiDto apiDto) {
		FastUtils.checkParams(apiDto.getId());
		UserVo operator = UserUtils.getOperator();
		apiDto.initUpdate(operator);
		apiService.save(apiDto, operator);
		return ok(apiDto.getId());
	}

	@RequestMapping("switchStatus")
	public Result<Object> switchStatus(@RequestBody ApiDto apiDto) {
		FastUtils.checkParams(apiDto.getApiStatus(), apiDto.getIds());
		apiService.switchStatus(apiDto, UserUtils.getOperator());
		return ok();
	}

	@RequestMapping("findPage")
	public Result<MongoPage<ApiVo>> findPage(@RequestBody ApiDto apiDto) {
		return ok(apiService.findPage(apiDto));
	}

	@RequestMapping("findDetail")
	public Result<ApiVo> findDetail(@RequestBody ApiDto apiDto) {
		return ok(apiService.findDetail(apiDto));
	}

	@RequestMapping("remove")
	public Result<Object> remove(@RequestBody ApiDto apiDto) {
		apiService.remove(apiDto);
		return ok();
	}
}
