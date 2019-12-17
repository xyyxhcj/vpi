package press.whcj.ams.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.querydsl.QPageRequest;
import press.whcj.ams.common.Constant;

import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@Accessors(chain = true)
public class MongoPage<T> {
	private int current = Constant.SysConfig.PAGE_BEGIN;
	private int size = Constant.SysConfig.PAGE_SIZE;
	private long total;
	private List<T> records;

	public QPageRequest buildPageRequest() {
		return new QPageRequest(current, size);
	}
}
