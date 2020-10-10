package press.whcj.ams.service;

import javax.servlet.http.HttpServletRequest;

import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.dto.UserDTO;
import press.whcj.ams.entity.vo.UserVO;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public interface UserService {
    /**
     * save
     *
     * @param userDTO userDTO
     * @return java.lang.String
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:30
     **/
    String save(UserDTO userDTO);

    /**
     * remove user
     *
     * @param userDTO  userDTO
     * @param operator operator
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:30
     **/
    void remove(UserDTO userDTO, UserVO operator);

    /**
     * findDetail
     *
     * @param id id
     * @return press.whcj.ams.entity.vo.UserVO
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:30
     **/
    UserVO findDetail(String id);

    /**
     * findPage
     *
     * @param userDTO userDTO
     * @return press.whcj.ams.entity.MongoPage<press.whcj.ams.entity.vo.UserVO>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:29
     **/
    MongoPage<UserVO> findPage(UserDTO userDTO);

    /**
     * add first admin
     *
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:24
     **/
    void init();

    /**
     * login
     *
     * @param userDTO userDTO
     * @param request request
     * @return press.whcj.ams.entity.vo.UserVO
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:25
     **/
    UserVO login(UserDTO userDTO, HttpServletRequest request);
}
