package press.whcj.ams.entity.dto;

import lombok.Getter;
import lombok.Setter;
import press.whcj.ams.entity.StructureData;
import press.whcj.ams.entity.vo.StructureDataVo;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
public class StructureDataDto extends StructureDataVo {
	private static final long serialVersionUID = 5003585460691996919L;
	private StructureData parent;
}
