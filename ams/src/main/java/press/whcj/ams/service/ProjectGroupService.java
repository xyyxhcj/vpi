package press.whcj.ams.service;

import java.util.List;

import press.whcj.ams.entity.ProjectGroup;
import press.whcj.ams.entity.dto.ProjectDTO;
import press.whcj.ams.entity.vo.ProjectGroupVO;
import press.whcj.ams.entity.vo.UserVO;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public interface ProjectGroupService {
    /**
     * save
     *
     * @param projectGroupDTO projectGroupDTO
     * @param operator        operator
     * @return java.lang.String
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:33
     **/
    String save(ProjectGroup projectGroupDTO, UserVO operator);

    /**
     * findList
     *
     * @param projectGroupDTO projectGroupDTO
     * @param operator        operator
     * @return java.util.List<press.whcj.ams.entity.vo.ProjectGroupVo>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:33
     **/
    List<ProjectGroupVO> findList(ProjectGroup projectGroupDTO, UserVO operator);

    /**
     * findDetail
     *
     * @param projectGroup projectGroup
     * @return press.whcj.ams.entity.vo.ProjectGroupVo
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:33
     **/
    ProjectGroupVO findDetail(ProjectGroup projectGroup);

    /**
     * delete
     *
     * @param projectGroup projectGroup
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:33
     **/
    void delete(ProjectGroup projectGroup);

    /**
     * findListByParentForOwner
     *
     * @param projectGroupDTO projectGroupDTO
     * @param operator        operator
     * @return java.util.List<press.whcj.ams.entity.vo.ProjectGroupVo>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:33
     **/
    List<ProjectGroupVO> findListByParentForOwner(ProjectGroup projectGroupDTO, UserVO operator);

    /**
     * findListByParentForOther
     *
     * @param projectGroupDTO projectGroupDTO
     * @param operator        operator
     * @return java.util.List<press.whcj.ams.entity.vo.ProjectGroupVo>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:33
     **/
    List<ProjectGroupVO> findListByParentForOther(ProjectGroup projectGroupDTO, UserVO operator);

    /**
     * move project&group to other group
     *
     * @param projectDTO projectDTO
     * @param operator   operator
     */
    void moveGroup(ProjectDTO projectDTO, UserVO operator);
}
