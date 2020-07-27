package press.whcj.ams.entity.dto;


import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import press.whcj.ams.entity.MongoPage;
import press.whcj.ams.entity.Project;
import press.whcj.ams.entity.ProjectUser;
import press.whcj.ams.entity.vo.ProjectVO;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ProjectDTO extends ProjectVO {
	private static final long serialVersionUID = -2799089198826287508L;
	private String userId;
	private List<ProjectUser> projectUsers;
	private MongoPage<ProjectVO> page = new MongoPage<>();
	private List<Project> projects = new ArrayList<>();
	private String envId;
}
