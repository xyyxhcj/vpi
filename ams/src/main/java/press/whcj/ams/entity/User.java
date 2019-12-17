package press.whcj.ams.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@ToString
@Accessors(chain = true)
public class User implements Serializable {
	private static final long serialVersionUID = -2750695756940876706L;
	private String id;
	private String userName;
	private String loginName;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	private Byte isAdmin;
	private String phone;
	private String email;
	private String avatarUrl;
	private String remark;
	private LocalDateTime createTime;
	private LocalDateTime updateTime;
	@DBRef
	@JsonBackReference("create")
	private User create;
	@DBRef
	@JsonBackReference("update")
	private User update;
	@Transient
	private Byte userType;

	public User() {
	}

	public User(String id) {
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
