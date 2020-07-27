package press.whcj.ams.entity.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import press.whcj.ams.entity.ApiTestHistory;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.vo.ApiTestHistoryVO;

/**
 * @author xyyxhcj@qq.com
 * @since 2020/02/08
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ApiTestHistoryDTO extends ApiTestHistoryVO {
    private static final long serialVersionUID = 319496661689506649L;
    private MongoPage<ApiTestHistory> page = new MongoPage<>();
    private String operateId;
    private List<String> ids = new ArrayList<>();
    private String projectId;
}
