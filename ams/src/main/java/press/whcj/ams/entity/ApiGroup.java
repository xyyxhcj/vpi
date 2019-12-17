package press.whcj.ams.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
public class ApiGroup implements Serializable {
    private String id;
    private String name;
    private String projectId;
    private String parentId;
    private Byte sort;
    @DBRef
    @JsonBackReference("create")
    private User create;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @DBRef
    @JsonBackReference("update")
    private User update;

    private static final long serialVersionUID = 1L;

    public ApiGroup() {
    }

    public ApiGroup(String id) {
        this.id = id;
    }

    public String getCreateId() {
        return create == null ? null : create.getId();
    }

    public String getCreateName() {
        return create == null ? null : create.getUserName();
    }

    public String getUpdateId() {
        return update == null ? null : update.getId();
    }

    public String getUpdateName() {
        return update == null ? null : update.getUserName();
    }
}
