package press.whcj.ams.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class ProjectGroup extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String parentId;

	public ProjectGroup(String id) {
		super(id);
	}
}
