package press.whcj.ams.web;

import java.time.LocalDateTime;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.User;
import press.whcj.ams.entity.dto.UserDTO;
import press.whcj.ams.entity.vo.UserVO;
import press.whcj.ams.service.UserService;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.PermUtils;
import press.whcj.ams.util.UserUtils;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@RestController
@RequestMapping("user")
public class UserController extends BaseController {
	@Resource
	private UserService userService;

	@PostMapping("add")
	public Result<String> add(@RequestBody UserDTO userDTO) {
		UserVO operator = UserUtils.getOperator();
		PermUtils.checkAdmin(operator);
		FastUtils.checkParams(userDTO.getLoginName());
		userDTO.setCreate(new User(operator.getId()));
		LocalDateTime now = LocalDateTime.now();
		userDTO.setCreateTime(now);
		userDTO.setUpdateTime(now);
		String id = userService.save(userDTO);
		return ok(id);
	}

	@PostMapping("edit")
	public Result<String> edit(@RequestBody UserDTO userDTO) {
		UserVO operator = UserUtils.getOperator();
		PermUtils.checkAdmin(operator);
		userDTO.setUpdate(new User(operator.getId()));
		userDTO.setUpdateTime(LocalDateTime.now());
		userService.save(userDTO);
		return ok(userDTO.getId());
	}


	@PostMapping("remove")
	public Result<Object> remove(@RequestBody UserDTO userDTO) {
		UserVO operator = UserUtils.getOperator();
		PermUtils.checkAdmin(operator);
		userService.remove(userDTO, operator);
		return ok();
	}

	@PostMapping("findDetail")
	public Result<UserVO> findDetail(@RequestBody UserDTO userDTO) {
		FastUtils.checkParams(userDTO.getId());
		return ok(userService.findDetail(userDTO.getId()));
	}


	@PostMapping("findPage")
	public Result<MongoPage<UserVO>> findPage(@RequestBody UserDTO userDTO) {
		return ok(userService.findPage(userDTO));
	}

	@PostMapping("login")
	public Result<UserVO> login(@RequestBody UserDTO userDTO, HttpServletRequest request) {
		return ok(userService.login(userDTO, request));
	}

	@PostMapping("loginOut")
	public Result<Object> loginOut() {
		getRequest().getSession().invalidate();
		return ok();
	}

	@PostMapping("update")
	public Result<String> update(@RequestBody UserDTO userDTO) {
		if (userDTO.getPassword() != null) {
			FastUtils.checkParams(userDTO.getOldPwd());
		}
		userDTO.setLoginName(null);
		String userId = UserUtils.getOperator().getId();
		userDTO.setId(userId);
		userDTO.setUpdate(new User(userId));
		userDTO.setUpdateTime(LocalDateTime.now());
		userService.save(userDTO);
		return ok(userDTO.getId());
	}
}
