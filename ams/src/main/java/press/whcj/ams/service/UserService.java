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
    /**
     * save
     *
     * @param userDto userDto
     * @return java.lang.String
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:30
     **/
    String save(UserDto userDto);

    /**
     * remove user
     *
     * @param userDto  userDto
     * @param operator operator
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:30
     **/
    void remove(UserDto userDto, UserVo operator);

    /**
     * findDetail
     *
     * @param id id
     * @return press.whcj.ams.entity.vo.UserVo
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:30
     **/
    UserVo findDetail(String id);

    /**
     * findPage
     *
     * @param userDto userDto
     * @return press.whcj.ams.entity.MongoPage<press.whcj.ams.entity.vo.UserVo>
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:29
     **/
    MongoPage<UserVo> findPage(UserDto userDto);

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
     * @param userDto userDto
     * @param request request
     * @return press.whcj.ams.entity.vo.UserVo
     * @author xyyxhcj@qq.com
     * @date 2020/5/18 19:25
     **/
    UserVo login(UserDto userDto, HttpServletRequest request);
}
