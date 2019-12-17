package press.whcj.ams.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
public class ProjectGroup implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private LocalDateTime createTime;
	private LocalDateTime updateTime;
	private String parentId;
	@DBRef
	@JsonBackReference("create")
	private User create;
	@DBRef
	@JsonBackReference("update")
	private User update;

	public ProjectGroup() {
	}

	public ProjectGroup(String id) {
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
