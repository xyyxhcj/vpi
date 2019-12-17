package press.whcj.ams.entity.dto;


import lombok.Getter;
import lombok.Setter;
import press.whcj.ams.entity.vo.ProjectUserVo;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
public class ProjectUserDto extends ProjectUserVo {
	private static final long serialVersionUID = 3567117338199578528L;
	private Byte isDel;
}
