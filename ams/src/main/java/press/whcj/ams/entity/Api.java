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
public class Api implements Serializable {

	private String id;

	private String name;
	@DBRef
	private ApiGroup group;

	private String projectId;

	private String apiUri;

	/**
	 * failure example
	 */
	private String apiFailureMock;

	/**
	 * success example
	 */
	private String apiSuccessMock;

	/**
	 * 0post 1get
	 */
	private Byte apiRequestType;
	@DBRef
	@JsonBackReference("requestParam")
	private Structure requestParam;
	@DBRef
	@JsonBackReference("resultParam")
	private Structure resultParam;

	/**
	 * 0-enable 1-maintaining 2-deprecated 3-pending 4-plan 5-develop 6-test 7-docking 8-bug
	 */
	private Byte apiStatus;

	private String desc;

	/**
	 * 0json 1form-data
	 */
	private Byte requestParamType;
	@DBRef
	@JsonBackReference("create")
	private User create;

	private LocalDateTime createTime;
	@DBRef
	@JsonBackReference("update")
	private User update;

	private LocalDateTime updateTime;

	private static final long serialVersionUID = 1L;

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
