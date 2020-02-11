package press.whcj.ams.service;


import press.whcj.ams.entity.ApiGroup;
import press.whcj.ams.entity.vo.ApiGroupVo;
import press.whcj.ams.entity.vo.UserVo;

import java.util.List;

/**
 * cjTodo 2019/12/6 16:14
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public interface ApiGroupService {
	List<ApiGroupVo> findList(ApiGroup apiGroupDto);

	String save(ApiGroup apiGroupDto, UserVo operator);

    void delete(ApiGroup apiGroupDto);
}
