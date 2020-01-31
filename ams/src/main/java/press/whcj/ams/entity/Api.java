package press.whcj.ams.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@Accessors(chain = true)
public class Api extends BaseEntity implements Serializable {
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
	@JsonBackReference("responseParam")
	private Structure responseParam;

	/**
	 * 0-enable 1-maintaining 2-deprecated 3-pending 4-plan 5-develop 6-test 7-docking 8-bug
	 */
	private Byte apiStatus;

	private String desc;

	/**
	 * 0json 1form-data
	 */
	private Byte requestParamType;

	/**
	 * 0json 1binary
	 */
	private Byte responseParamType;

	private static final long serialVersionUID = 1L;
}
