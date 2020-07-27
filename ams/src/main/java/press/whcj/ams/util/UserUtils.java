package press.whcj.ams.util;

import org.springframework.stereotype.Component;

import press.whcj.ams.common.Constant;
import press.whcj.ams.entity.vo.UserVO;
import press.whcj.ams.exception.ResultCode;
import press.whcj.ams.exception.ServiceException;
import press.whcj.ams.support.BaseController;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Component
public class UserUtils {
    public static UserVO getOperator() {
        Object userInfo = BaseController.getRequest().getSession().getAttribute(Constant.SessionKey.USER_INFO);
        if (userInfo == null) {
            throw new ServiceException(ResultCode.USER_TOKEN_EXPIRED);
        }
        return JsonUtils.json2Pojo((String) userInfo, UserVO.class);
    }
}
