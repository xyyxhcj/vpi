package press.whcj.ams.entity.vo;

import lombok.Getter;
import lombok.Setter;
import press.whcj.ams.entity.Api;
import press.whcj.ams.entity.ApiHeader;

import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
public class ApiVo extends Api {
	private static final long serialVersionUID = 7302871133312701808L;
	private StructureVo requestParamVo;
	private StructureVo resultParamVo;
	private List<ApiHeader> headers;
}
