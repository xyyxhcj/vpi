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
}
