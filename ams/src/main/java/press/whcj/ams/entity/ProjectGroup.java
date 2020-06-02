package press.whcj.ams.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ProjectGroup extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String parentId;

	public ProjectGroup(String id) {
		super(id);
	}
}
