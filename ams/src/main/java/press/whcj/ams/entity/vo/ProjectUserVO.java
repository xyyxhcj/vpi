package press.whcj.ams.entity.vo;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import press.whcj.ams.entity.ProjectUser;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ProjectUserVO extends ProjectUser {
	private static final long serialVersionUID = 54773277303819897L;
	private String userName;
}
