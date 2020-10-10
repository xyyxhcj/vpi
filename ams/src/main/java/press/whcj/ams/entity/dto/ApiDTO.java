package press.whcj.ams.entity.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.vo.ApiVO;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ApiDTO extends ApiVO {
	private static final long serialVersionUID = -2107807918182928550L;
	private String groupId;
	private StructureDTO requestParamDTO = new StructureDTO();
	private StructureDTO responseParamDTO = new StructureDTO();
	private MongoPage<ApiVO> page = new MongoPage<>();
	private List<String> ids = new ArrayList<>();
	private String structureId;
	private String nameOrUri;
	private List<String> groupIds = new ArrayList<>();
	private String envId;
}
