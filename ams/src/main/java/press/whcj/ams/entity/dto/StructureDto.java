package press.whcj.ams.entity.dto;

import lombok.Getter;
import lombok.Setter;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.vo.StructureVo;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
public class StructureDto extends StructureVo {
	private static final long serialVersionUID = 3823736257675941808L;
	private MongoPage<StructureVo> page = new MongoPage<>();
	private boolean isCheckName = true;

	public StructureDto() {
	}
}
