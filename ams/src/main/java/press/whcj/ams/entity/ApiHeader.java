package press.whcj.ams.entity;


import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"name"})
@ToString
public class ApiHeader implements Serializable {
    private String id;

    private String apiId;

    private String name;

    private String value;

    private String desc;

    /**
     * 0-required 1-optional
     */
    private Byte requireType;

    private Byte isRequest;

    private static final long serialVersionUID = 1L;

    private String projectId;
}
