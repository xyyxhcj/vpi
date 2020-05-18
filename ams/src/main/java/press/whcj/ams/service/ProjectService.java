package press.whcj.ams.service;


import press.whcj.ams.entity.Project;
import press.whcj.ams.entity.dto.ProjectDto;
import press.whcj.ams.entity.vo.UserVo;

import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public interface ProjectService {
    /**
     * save
     *
     * @param projectDto projectDto
     * @param operator   operator
     * @return id
     */
    String save(ProjectDto projectDto, UserVo operator);

    /**
     * assign permissions
     *
     * @param projectDto projectDto
     * @param operator   operator
     */
    void assign(ProjectDto projectDto, UserVo operator);

    /**
     * findList
     *
     * @param projectDto projectDto
     * @param operator   operator
     * @return List
     */
    List<Project> findList(ProjectDto projectDto, UserVo operator);

    /**
     * findListByGroupForOther
     *
     * @param projectDto projectDto
     * @param operator   operator
     * @return java.util.List<press.whcj.ams.entity.Project>
     * @author xyyxhcj@qq.com
     * @date 2020/1/3 8:51
     **/
    List<Project> findListByGroupForOther(ProjectDto projectDto, UserVo operator);

    /**
     * remove
     *
     * @param projectId projectId
     * @param operator  operator
     * @author xyyxhcj@qq.com
     * @date 2020/2/10 10:36
     **/
    void remove(String projectId, UserVo operator);

    /**
     * findListByGroupForOwner
     *
     * @param projectDto projectDto
     * @param operator   operator
     * @return java.util.List<press.whcj.ams.entity.Project>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:33
     **/
    List<Project> findListByGroupForOwner(ProjectDto projectDto, UserVo operator);
}
