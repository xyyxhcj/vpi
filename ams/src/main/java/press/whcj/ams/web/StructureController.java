package press.whcj.ams.web;

import java.time.LocalDateTime;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import press.whcj.ams.common.Constant;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.User;
import press.whcj.ams.entity.dto.StructureDTO;
import press.whcj.ams.entity.vo.StructureVO;
import press.whcj.ams.entity.vo.UserVO;
import press.whcj.ams.service.StructureService;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.UserUtils;

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
	public Result<MongoPage<StructureVO>> findPage(@RequestBody StructureDTO structureDTO) {
		return ok(structureService.findPage(structureDTO));
	}

	@PostMapping("add")
	public Result<String> add(@RequestBody StructureDTO structureDTO) {
		UserVO operator = UserUtils.getOperator();
		LocalDateTime now = LocalDateTime.now();
		structureDTO.setCreate(new User(operator.getId()));
		structureDTO.setCreateTime(now);
		structureDTO.setUpdateTime(now);
		structureDTO.setType(Constant.StructureType.USER_CREATE);
		String id = structureService.save(structureDTO, operator);
		return ok(id);
	}

	@PostMapping("edit")
	public Result<String> edit(@RequestBody StructureDTO structureDTO) {
		FastUtils.checkParams(structureDTO.getId());
		UserVO operator = UserUtils.getOperator();
		structureDTO.setUpdate(new User(operator.getId()));
		structureDTO.setUpdateTime(LocalDateTime.now());
		structureService.save(structureDTO, operator);
		return ok(structureDTO.getId());
	}

	@PostMapping("remove")
	public Result<Object> remove(@RequestBody StructureDTO structureDTO) {
		structureService.remove(structureDTO);
		return ok();
	}

	@PostMapping("findDetail")
	public Result<StructureVO> findDetail(@RequestBody StructureDTO structureDTO) {
		return ok(structureService.findDetail(structureDTO));
	}
}
