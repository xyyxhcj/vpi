package press.whcj.ams.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ApiTestCase extends BaseEntity implements Serializable {
    private String projectId;

    private String apiId;

    private String name;

    private String url;

    private String method;

    /**
    * json
    */
    private String requestInfo;

    /**
    * json
    */
    private String responseInfo;

    /**
     * like 'data.code'
     **/
    private String checkField;

    private String checkValue;

    private static final long serialVersionUID = 1L;
}
