package press.whcj.ams.service;

import press.whcj.ams.entity.ProjectGroup;
import press.whcj.ams.entity.dto.ProjectDto;
import press.whcj.ams.entity.vo.ProjectGroupVo;
import press.whcj.ams.entity.vo.UserVo;

import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public interface ProjectGroupService {
    /**
     * save
     *
     * @param projectGroupDto projectGroupDto
     * @param operator        operator
     * @return java.lang.String
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:33
     **/
    String save(ProjectGroup projectGroupDto, UserVo operator);

    /**
     * findList
     *
     * @param projectGroupDto projectGroupDto
     * @param operator        operator
     * @return java.util.List<press.whcj.ams.entity.vo.ProjectGroupVo>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:33
     **/
    List<ProjectGroupVo> findList(ProjectGroup projectGroupDto, UserVo operator);

    /**
     * findDetail
     *
     * @param projectGroup projectGroup
     * @return press.whcj.ams.entity.vo.ProjectGroupVo
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:33
     **/
    ProjectGroupVo findDetail(ProjectGroup projectGroup);

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
     * @param projectGroupDto projectGroupDto
     * @param operator        operator
     * @return java.util.List<press.whcj.ams.entity.vo.ProjectGroupVo>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:33
     **/
    List<ProjectGroupVo> findListByParentForOwner(ProjectGroup projectGroupDto, UserVo operator);

    /**
     * findListByParentForOther
     *
     * @param projectGroupDto projectGroupDto
     * @param operator        operator
     * @return java.util.List<press.whcj.ams.entity.vo.ProjectGroupVo>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:33
     **/
    List<ProjectGroupVo> findListByParentForOther(ProjectGroup projectGroupDto, UserVo operator);

    /**
     * move project&group to other group
     *
     * @param projectDto projectDto
     * @param operator   operator
     */
    void moveGroup(ProjectDto projectDto, UserVo operator);
}
