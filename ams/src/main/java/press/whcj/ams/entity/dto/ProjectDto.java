package press.whcj.ams.entity.dto;


import lombok.Getter;
import lombok.Setter;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.Project;
import press.whcj.ams.entity.ProjectUser;
import press.whcj.ams.entity.vo.ProjectVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
public class ProjectDto extends ProjectVo {
	private static final long serialVersionUID = -2799089198826287508L;
	private String userId;
	private List<ProjectUser> projectUsers;
	private MongoPage<ProjectVo> page = new MongoPage<>();
	List<Project> projects = new ArrayList<>();
	private String envId;
}
