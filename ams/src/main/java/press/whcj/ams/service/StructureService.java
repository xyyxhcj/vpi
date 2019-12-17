package press.whcj.ams.service;

import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.dto.StructureDto;
import press.whcj.ams.entity.vo.StructureVo;
import press.whcj.ams.entity.vo.UserVo;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public interface StructureService {
	MongoPage<StructureVo> findPage(StructureDto structureDto);

	String save(StructureDto structureDto, UserVo operator);

	StructureVo findDetail(StructureDto structureDto);

	StructureVo getStructureVoById(String structureId);
}
