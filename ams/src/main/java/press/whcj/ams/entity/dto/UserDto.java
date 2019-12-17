package press.whcj.ams.entity.dto;

import lombok.Getter;
import lombok.Setter;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.vo.UserVo;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
public class UserDto extends UserVo {
	private static final long serialVersionUID = 3250096621816867864L;
	private MongoPage<UserVo> page = new MongoPage<>();
	private String oldPwd;
}
