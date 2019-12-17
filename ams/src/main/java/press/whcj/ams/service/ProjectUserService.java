package press.whcj.ams.service;


import press.whcj.ams.entity.ProjectUser;
import press.whcj.ams.entity.User;
import press.whcj.ams.entity.dto.ProjectUserDto;

import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public interface ProjectUserService {
	/**
	 * edit
	 *
	 * @param projectUserDto projectUserDto
	 */
	void edit(ProjectUserDto projectUserDto);

	/**
	 * findProjectUser
	 *
	 * @param projectUser projectUser
	 * @return list
	 */
	List<User> findProjectUser(ProjectUser projectUser);
}
