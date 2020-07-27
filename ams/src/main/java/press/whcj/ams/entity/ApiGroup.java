package press.whcj.ams.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 *
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class ApiGroup extends BaseEntity implements Serializable {
    private String name;
    private String projectId;
    private String parentId;
    private Byte sort;

    private static final long serialVersionUID = 1L;

    public ApiGroup(String id) {
        super(id);
    }
}
