package press.whcj.ams.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Structure extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String projectId;
    private String name;
    private String remark;
    /**
     * 0-apiCreate 1-userCreate
     **/
    private Byte type;

    private Byte isDel;

    public Structure(String id) {
        super(id);
    }
}
