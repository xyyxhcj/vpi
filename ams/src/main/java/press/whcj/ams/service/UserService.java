package press.whcj.ams.service;

import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.dto.UserDto;
import press.whcj.ams.entity.vo.UserVo;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public interface UserService {
	String save(UserDto userDto);

	void remove(UserDto userDto, UserVo operator);

	UserVo findDetail(String id);

	MongoPage<UserVo> findPage(UserDto userDto);

	void init();

	UserVo login(UserDto userDto, HttpServletRequest request);
}
