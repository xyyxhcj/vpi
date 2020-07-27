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
public class ApiTestHistory implements Serializable {
    /**
    * id
    */
    private String id;

    private String apiId;

    private String url;

    private String method;

    private Integer requestTime;

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

    @Override
    public String toString() {
        return "ApiTestHistory{" +
                "id='" + id + '\'' +
                ", apiId='" + apiId + '\'' +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", requestTime=" + requestTime +
                ", requestInfo='" + requestInfo + '\'' +
                ", responseInfo='" + responseInfo + '\'' +
                ", createId=" + getCreateId() +
                ", createName=" + getCreateName() +
                ", createTime=" + createTime +
                '}';
    }
}
