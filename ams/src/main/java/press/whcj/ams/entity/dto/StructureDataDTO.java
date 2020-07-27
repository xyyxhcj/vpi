package press.whcj.ams.entity.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import press.whcj.ams.entity.StructureData;
import press.whcj.ams.entity.vo.StructureDataVO;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@ToString(callSuper = true)
public class StructureDataDTO extends StructureDataVO {
	private static final long serialVersionUID = 5003585460691996919L;
	private StructureData parent;
}
