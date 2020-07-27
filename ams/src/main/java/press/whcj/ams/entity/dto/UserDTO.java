package press.whcj.ams.entity.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.vo.UserVO;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@ToString(callSuper = true)
public class UserDTO extends UserVO {
	private static final long serialVersionUID = 3250096621816867864L;
	private MongoPage<UserVO> page = new MongoPage<>();
	private String oldPwd;
	private List<String> ids = new ArrayList<>();
}
