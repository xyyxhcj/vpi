package press.whcj.ams.entity.dto;

import lombok.Getter;
import lombok.Setter;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.vo.ApiVo;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
public class ApiDto extends ApiVo {
	private static final long serialVersionUID = -2107807918182928550L;
	private String groupId;
	private StructureDto requestParamDto;
	private StructureDto resultParamDto;
	private MongoPage<ApiVo> page = new MongoPage<>();
}
