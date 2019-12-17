package press.whcj.ams.entity;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
public class ApiEnv implements Serializable {
	private String id;
	private String name;
	private String projectId;
	private String desc;
	private String frontUri;

	/**
	 * headerList：[{"key":"var1","value":"val1","explain":"..."},...]
	 */
	private String envHeader;
	private String globalVariable;
	private String additionalVariable;
	private static final long serialVersionUID = 1L;
}
