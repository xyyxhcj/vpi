package press.whcj.ams.entity.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import press.whcj.ams.entity.vo.ProjectUserVO;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ProjectUserDTO extends ProjectUserVO {
	private static final long serialVersionUID = 3567117338199578528L;
	private Byte isDel;
}
