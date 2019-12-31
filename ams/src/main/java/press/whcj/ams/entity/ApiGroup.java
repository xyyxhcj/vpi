package press.whcj.ams.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@Accessors(chain = true)
public class ApiGroup extends BaseEntity implements Serializable {
    private String name;
    private String projectId;
    private String parentId;
    private Byte sort;

    private static final long serialVersionUID = 1L;

    public ApiGroup() {
    }

    public ApiGroup(String id) {
        super(id);
    }
}
