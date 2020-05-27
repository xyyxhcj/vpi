package press.whcj.ams.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.DBRef;
import press.whcj.ams.entity.vo.UserVo;

import java.time.LocalDateTime;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class BaseEntity {
    private String id;
    @DBRef
    @JsonBackReference("create")
    private User create;
    private LocalDateTime createTime;
    @DBRef
    @JsonBackReference("update")
    private User update;
    private LocalDateTime updateTime;

    public BaseEntity(String id) {
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


    public void initCreate(UserVo operator) {
        LocalDateTime now = LocalDateTime.now();
        setCreate(new User(operator.getId()));
        setCreateTime(now);
        setUpdateTime(now);
    }

    public void initUpdate(UserVo operator) {
        setUpdate(new User(operator.getId()));
        setUpdateTime(LocalDateTime.now());
    }
}
