package press.whcj.ams.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ProjectUser implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	/**
	 * 1-admin 2-readAndWrite 3-read
	 */
	private Byte userType;

	private String remark;
	@DBRef
	private Project project;
	@DBRef
	private User user;
	@Transient
	private String userId;
	@Transient
	private String projectId;
}
