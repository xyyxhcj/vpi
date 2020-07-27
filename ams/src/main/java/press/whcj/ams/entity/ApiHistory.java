package press.whcj.ams.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.DBRef;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
public class ApiHistory implements Serializable {
    /**
    * id
    */
    private String id;

    /**
    * api id
    */
    private String apiId;

    /**
    * json backup
    */
    private String historyJson;

    private String desc;
    @DBRef
    @JsonBackReference("create")
    private User create;
    private LocalDateTime createTime;

    private static final long serialVersionUID = 1L;

    public String getCreateId() {
        return create == null ? null : create.getId();
    }

    public String getCreateName() {
        return create == null ? null : create.getUserName();
    }

    @Override
    public String toString() {
        return "ApiHistory{" +
                "id='" + id + '\'' +
                ", apiId='" + apiId + '\'' +
                ", historyJson='" + historyJson + '\'' +
                ", desc='" + desc + '\'' +
                ", createId=" + getCreateId() +
                ", createName=" + getCreateName() +
                ", createTime=" + createTime +
                '}';
    }
}
