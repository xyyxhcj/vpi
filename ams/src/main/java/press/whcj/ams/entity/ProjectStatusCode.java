package press.whcj.ams.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
public class ProjectStatusCode implements Serializable {
    /**
    * id
    */
    private String id;
    private String code;
    private String desc;
    private String groupId;

    private static final long serialVersionUID = 1L;
}
