package press.whcj.ams.entity.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.vo.StructureVO;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class StructureDTO extends StructureVO {
	private static final long serialVersionUID = 3823736257675941808L;
	private MongoPage<StructureVO> page = new MongoPage<>();
	private boolean checkName = true;
	private boolean reference;
	private String nameOrRemark;
}
