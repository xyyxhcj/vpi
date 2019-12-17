package press.whcj.ams.entity.vo;


import lombok.Getter;
import lombok.Setter;
import press.whcj.ams.entity.ApiGroup;

import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
public class ApiGroupVo extends ApiGroup {
	private static final long serialVersionUID = 5720192602674985186L;
	private List<ApiGroupVo> childList;
}
