package press.whcj.ams.service;

import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.dto.ApiDto;
import press.whcj.ams.entity.vo.ApiVo;
import press.whcj.ams.entity.vo.UserVo;

import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public interface ApiService {
	String save(ApiDto apiDto, UserVo operator);

	MongoPage<ApiVo> findPage(ApiDto apiDto);

	ApiVo findDetail(ApiDto apiDto);

	void saveMock(ApiDto apiDto);

    void remove(ApiDto apiDto);

	void switchStatus(ApiDto apiDto, UserVo operator);

    List<ApiVo> findReferenceApi(ApiDto apiDto);
}
