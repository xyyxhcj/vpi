package press.whcj.ams.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@Accessors(chain = true)
public class Project extends BaseEntity implements Serializable {
	private String name;
	private String groupId;
	/**
	 * 0web 1app 2pc 3others
	 */
	private Byte projectType;
	/**
	 * 0-creator 1-admin 2-readAndWrite 3-read
	 **/
	@Transient
	private Byte userType;

	private static final long serialVersionUID = 1L;

	public Project() {
	}

	public Project(String id) {
		super(id);
	}
}
