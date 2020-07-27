package press.whcj.ams.entity.vo;


import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import press.whcj.ams.entity.ApiGroup;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ApiGroupVO extends ApiGroup {
	private static final long serialVersionUID = 5720192602674985186L;
	private List<ApiGroupVO> childList;
}
