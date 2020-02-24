package press.whcj.ams.web;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.User;
import press.whcj.ams.entity.dto.UserDto;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.service.UserService;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.PermUtils;
import press.whcj.ams.util.UserUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@RestController
@RequestMapping("user")
public class UserController extends BaseController {
	@Resource
	private UserService userService;


	@RequestMapping("add")
	public Result<String> add(@RequestBody UserDto userDto) {
		UserVo operator = UserUtils.getOperator();
		PermUtils.checkAdmin(operator);
		FastUtils.checkParams(userDto.getLoginName());
		userDto.setCreate(new User(operator.getId()));
		LocalDateTime now = LocalDateTime.now();
		userDto.setCreateTime(now);
		userDto.setUpdateTime(now);
		String id = userService.save(userDto);
		return ok(id);
	}

	@RequestMapping("edit")
	public Result<String> edit(@RequestBody UserDto userDto) {
		UserVo operator = UserUtils.getOperator();
		PermUtils.checkAdmin(operator);
		userDto.setUpdate(new User(operator.getId()));
		userDto.setUpdateTime(LocalDateTime.now());
		userService.save(userDto);
		return ok(userDto.getId());
	}


	@RequestMapping("remove")
	public Result<Object> remove(@RequestBody UserDto userDto) {
		UserVo operator = UserUtils.getOperator();
		PermUtils.checkAdmin(operator);
		userService.remove(userDto, operator);
		return ok();
	}

	@RequestMapping("findDetail")
	public Result<UserVo> findDetail(@RequestBody UserDto userDto) {
		FastUtils.checkParams(userDto.getId());
		return ok(userService.findDetail(userDto.getId()));
	}


	@RequestMapping("findPage")
	public Result<MongoPage<UserVo>> findPage(@RequestBody UserDto userDto) {
		return ok(userService.findPage(userDto));
	}

	@RequestMapping("login")
	public Result<UserVo> login(@RequestBody UserDto userDto, HttpServletRequest request) {
		return ok(userService.login(userDto, request));
	}

	@RequestMapping("loginOut")
	public Result<Object> loginOut() {
		getRequest().getSession().invalidate();
		return ok();
	}

	@RequestMapping("update")
	public Result<String> update(@RequestBody UserDto userDto) {
		if (userDto.getPassword() != null) {
			FastUtils.checkParams(userDto.getOldPwd());
		}
		userDto.setLoginName(null);
		String userId = UserUtils.getOperator().getId();
		userDto.setId(userId);
		userDto.setUpdate(new User(userId));
		userDto.setUpdateTime(LocalDateTime.now());
		userService.save(userDto);
		return ok(userDto.getId());
	}
}
