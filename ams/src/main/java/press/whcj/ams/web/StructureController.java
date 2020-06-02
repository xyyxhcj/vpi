package press.whcj.ams.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import press.whcj.ams.common.Constant;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.User;
import press.whcj.ams.entity.dto.StructureDto;
import press.whcj.ams.entity.vo.StructureVo;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.service.StructureService;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.UserUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@RestController
@RequestMapping("structure")
public class StructureController extends BaseController {
	@Resource
	private StructureService structureService;

	@PostMapping("findPage")
	public Result<MongoPage<StructureVo>> findPage(@RequestBody StructureDto structureDto) {
		return ok(structureService.findPage(structureDto));
	}

	@PostMapping("add")
	public Result<String> add(@RequestBody StructureDto structureDto) {
		UserVo operator = UserUtils.getOperator();
		LocalDateTime now = LocalDateTime.now();
		structureDto.setCreate(new User(operator.getId()));
		structureDto.setCreateTime(now);
		structureDto.setUpdateTime(now);
		structureDto.setType(Constant.StructureType.USER_CREATE);
		String id = structureService.save(structureDto, operator);
		return ok(id);
	}

	@PostMapping("edit")
	public Result<String> edit(@RequestBody StructureDto structureDto) {
		FastUtils.checkParams(structureDto.getId());
		UserVo operator = UserUtils.getOperator();
		structureDto.setUpdate(new User(operator.getId()));
		structureDto.setUpdateTime(LocalDateTime.now());
		structureService.save(structureDto, operator);
		return ok(structureDto.getId());
	}

	@PostMapping("remove")
	public Result<Object> remove(@RequestBody StructureDto structureDto) {
		structureService.remove(structureDto);
		return ok();
	}

	@PostMapping("findDetail")
	public Result<StructureVo> findDetail(@RequestBody StructureDto structureDto) {
		return ok(structureService.findDetail(structureDto));
	}
}
