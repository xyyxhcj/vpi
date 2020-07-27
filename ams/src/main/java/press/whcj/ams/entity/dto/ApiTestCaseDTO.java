package press.whcj.ams.entity.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import press.whcj.ams.entity.ApiTestCase;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.vo.ApiTestCaseVO;

/**
 * @author xyyxhcj@qq.com
 * @since 2020/07/25
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ApiTestCaseDTO extends ApiTestCaseVO {
    private static final long serialVersionUID = 194300348386760734L;
    private List<String> ids = new ArrayList<>();
    private MongoPage<ApiTestCase> page = new MongoPage<>();
}
