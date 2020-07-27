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
