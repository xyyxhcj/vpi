package press.whcj.ams.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@Accessors(chain = true)
public class Project implements Serializable {
	private String id;
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
	@DBRef
	@JsonBackReference("create")
	private User create;
	private LocalDateTime createTime;
	@DBRef
	@JsonBackReference("update")
	private User update;
	private LocalDateTime updateTime;

	private static final long serialVersionUID = 1L;

	public Project() {
	}

	public Project(String id) {
		this.id = id;
	}

	public String getCreateId() {
		return create == null ? null : create.getId();
	}

	public String getCreateName() {
		return create == null ? null : create.getUserName();
	}

	public String getUpdateId() {
		return update == null ? null : update.getId();
	}

	public String getUpdateName() {
		return update == null ? null : update.getUserName();
	}
}
