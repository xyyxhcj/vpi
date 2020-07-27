package press.whcj.ams.service;


import java.util.List;

import press.whcj.ams.entity.Project;
import press.whcj.ams.entity.dto.ProjectDTO;
import press.whcj.ams.entity.vo.UserVO;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public interface ProjectService {
    /**
     * save
     *
     * @param projectDTO projectDTO
     * @param operator   operator
     * @return id
     */
    String save(ProjectDTO projectDTO, UserVO operator);

    /**
     * assign permissions
     *
     * @param projectDTO projectDTO
     * @param operator   operator
     */
    void assign(ProjectDTO projectDTO, UserVO operator);

    /**
     * findList
     *
     * @param projectDTO projectDTO
     * @param operator   operator
     * @return List
     */
    List<Project> findList(ProjectDTO projectDTO, UserVO operator);

    /**
     * findListByGroupForOther
     *
     * @param projectDTO projectDTO
     * @param operator   operator
     * @return java.util.List<press.whcj.ams.entity.Project>
     * @author xyyxhcj@qq.com
     * @date 2020/1/3 8:51
     **/
    List<Project> findListByGroupForOther(ProjectDTO projectDTO, UserVO operator);

    /**
     * remove
     *
     * @param projectId projectId
     * @param operator  operator
     * @author xyyxhcj@qq.com
     * @date 2020/2/10 10:36
     **/
    void remove(String projectId, UserVO operator);

    /**
     * findListByGroupForOwner
     *
     * @param projectDTO projectDTO
     * @param operator   operator
     * @return java.util.List<press.whcj.ams.entity.Project>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:33
     **/
    List<Project> findListByGroupForOwner(ProjectDTO projectDTO, UserVO operator);
}
