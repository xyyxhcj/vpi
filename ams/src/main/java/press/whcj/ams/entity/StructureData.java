package press.whcj.ams.entity;

import lombok.Getter;
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
public class StructureData implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;

	private String structureId;

	private String value;

	private String paramKey;

	/**
	 * 0string 1file 2json 3number 4double 5time 6boolean 7array 8object 9null
	 */
	private Byte paramType;
	private String parentId;
	private String paramDesc;

	/**
	 * 0-required 1-optional 2-special
	 */
	private Byte requireType;
	/**
	 * if reference other structure
	 **/
	private String referenceStructureId;
	private String projectId;
}
