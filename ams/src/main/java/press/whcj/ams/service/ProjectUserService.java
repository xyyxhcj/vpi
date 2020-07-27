package press.whcj.ams.service;


import java.util.List;

import press.whcj.ams.entity.ProjectUser;
import press.whcj.ams.entity.User;
import press.whcj.ams.entity.dto.ProjectUserDTO;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public interface ProjectUserService {
	/**
	 * edit
	 *
	 * @param projectUserDTO projectUserDTO
	 */
	void edit(ProjectUserDTO projectUserDTO);

	/**
	 * findProjectUser
	 *
	 * @param projectUser projectUser
	 * @return list
	 */
	List<User> findProjectUser(ProjectUser projectUser);
}
