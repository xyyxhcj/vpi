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
public class ApiTestHistory implements Serializable {
    /**
    * id
    */
    private String id;

    private String apiId;

    /**
    * json
    */
    private String requestInfo;

    /**
    * json
    */
    private String responseInfo;
    @DBRef
    @JsonBackReference("create")
    private User create;

    /**
    * test time
    */
    private LocalDateTime createTime;

    private static final long serialVersionUID = 1L;

    public String getCreateId() {
        return create == null ? null : create.getId();
    }

    public String getCreateName() {
        return create == null ? null : create.getUserName();
    }
}
