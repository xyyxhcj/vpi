package press.whcj.ams.entity.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import press.whcj.ams.entity.User;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@ToString(callSuper = true)
@Accessors(chain = true)
public class UserVO extends User {
	private static final long serialVersionUID = 9212691497037754668L;
	private String auth;
}
