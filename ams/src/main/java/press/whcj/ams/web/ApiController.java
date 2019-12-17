package press.whcj.ams.web;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.User;
import press.whcj.ams.entity.dto.ApiDto;
import press.whcj.ams.entity.vo.ApiVo;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.service.ApiService;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.UserUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;

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
		LocalDateTime now = LocalDateTime.now();
		apiDto.setCreate(new User(operator.getId()));
		apiDto.setCreateTime(now);
		apiDto.setUpdateTime(now);
		String id = apiService.save(apiDto, operator);
		return ok(id);
	}

	@RequestMapping("edit")
	public Result<String> edit(@RequestBody ApiDto apiDto) {
		FastUtils.checkParams(apiDto.getId());
		UserVo operator = UserUtils.getOperator();
		apiDto.setUpdate(new User(operator.getId()));
		apiDto.setUpdateTime(LocalDateTime.now());
		apiService.save(apiDto, operator);
		return ok(apiDto.getId());
	}

	@RequestMapping("findPage")
	public Result<MongoPage<ApiVo>> findPage(@RequestBody ApiDto apiDto) {
		return ok(apiService.findPage(apiDto));
	}

	@RequestMapping("findDetail")
	public Result<ApiVo> findDetail(@RequestBody ApiDto apiDto) {
		return ok(apiService.findDetail(apiDto));
	}
}
