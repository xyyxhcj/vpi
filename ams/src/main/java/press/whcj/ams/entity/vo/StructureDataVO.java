package press.whcj.ams.entity.vo;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import press.whcj.ams.entity.StructureData;
import press.whcj.ams.entity.dto.StructureDataDTO;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@ToString(callSuper = true)
public class StructureDataVO extends StructureData {
	private static final long serialVersionUID = -8242864518498538638L;
	private List<StructureDataDTO> subList = new LinkedList<>();
	private String referenceStructureName;
}
