package press.whcj.ams.entity.vo;

import lombok.Getter;
import lombok.Setter;
import press.whcj.ams.entity.StructureData;
import press.whcj.ams.entity.dto.StructureDataDto;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
public class StructureDataVo extends StructureData {
	private static final long serialVersionUID = -8242864518498538638L;
	private List<StructureDataDto> subList = new LinkedList<>();
	private String referenceStructureName;
}
