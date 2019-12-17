package press.whcj.ams.entity;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
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

    private static final long serialVersionUID = 1L;
}
