package press.whcj.ams.entity.vo;

import lombok.Getter;
import lombok.Setter;
import press.whcj.ams.entity.Structure;
import press.whcj.ams.entity.dto.StructureDataDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
public class StructureVo extends Structure {
	private static final long serialVersionUID = -6918218483871047332L;
	private List<StructureDataDto> dataList = new ArrayList<>();

	public StructureVo() {
	}
}
