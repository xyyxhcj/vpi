package press.whcj.ams.entity;


import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * api environment
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
public class ApiEnv extends BaseEntity implements Serializable {
    private String name;
    private String projectId;
    private String desc;
    private String frontUri;

    /**
     * headerList：[{"name":"auth","value":"asd","desc":"Token","requireType":0,"nameIsEmpty":false},...]
     */
    private String envHeader;
    private String globalVariable;
    private String additionalVariable;
    private static final long serialVersionUID = 1L;
}
