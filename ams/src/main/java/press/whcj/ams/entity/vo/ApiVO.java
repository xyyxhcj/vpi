package press.whcj.ams.entity.vo;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import press.whcj.ams.entity.Api;
import press.whcj.ams.entity.ApiHeader;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ApiVO extends Api {
	private static final long serialVersionUID = 7302871133312701808L;
	private StructureVO requestParamVO;
	private StructureVO responseParamVO;
	private Collection<ApiHeader> requestHeaders;
	private Collection<ApiHeader> responseHeaders;
}
