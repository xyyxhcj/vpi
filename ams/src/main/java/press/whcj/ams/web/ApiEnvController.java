package press.whcj.ams.web;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import press.whcj.ams.entity.ApiEnv;
import press.whcj.ams.entity.User;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.service.ApiEnvService;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.UserUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 * // cjTodo 2019/12/30 no test
 */
@RestController
@RequestMapping("apiEnv")
public class ApiEnvController extends BaseController {
	@Resource
	private ApiEnvService apiEnvService;

	@RequestMapping("add")
	public Result<String> add(@RequestBody ApiEnv apiEnvDto) {
		UserVo operator = UserUtils.getOperator();
		LocalDateTime now = LocalDateTime.now();
		apiEnvDto.setCreate(new User(operator.getId()));
		apiEnvDto.setCreateTime(now);
		apiEnvDto.setUpdateTime(now);
		String id = apiEnvService.save(apiEnvDto, operator);
		return ok(id);
	}

	@RequestMapping("edit")
	public Result<String> edit(@RequestBody ApiEnv apiEnvDto) {
		FastUtils.checkParams(apiEnvDto.getId());
		UserVo operator = UserUtils.getOperator();
		apiEnvDto.setUpdate(new User(operator.getId()));
		apiEnvDto.setUpdateTime(LocalDateTime.now());
		apiEnvService.save(apiEnvDto, operator);
		return ok(apiEnvDto.getId());
	}

	@RequestMapping("findList")
	public Result<List<ApiEnv>> findList(@RequestBody ApiEnv apiEnvDto) {
		return ok(apiEnvService.findList(apiEnvDto));
	}
}
