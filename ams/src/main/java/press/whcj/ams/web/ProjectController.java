package press.whcj.ams.web;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zeroturnaround.zip.ZipUtil;
import press.whcj.ams.common.Constant;
import press.whcj.ams.entity.Project;
import press.whcj.ams.entity.ProjectGroup;
import press.whcj.ams.entity.ProjectUser;
import press.whcj.ams.entity.User;
import press.whcj.ams.entity.dto.ProjectDto;
import press.whcj.ams.entity.dto.ProjectUserDto;
import press.whcj.ams.entity.vo.ProjectGroupVo;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.service.ProjectGroupService;
import press.whcj.ams.service.ProjectService;
import press.whcj.ams.service.ProjectUserService;
import press.whcj.ams.support.BaseController;
import press.whcj.ams.support.Result;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.PermUtils;
import press.whcj.ams.util.UserUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@RestController
@RequestMapping("project")
public class ProjectController extends BaseController {
    @Resource
    private ProjectService projectService;
    @Resource
    private ProjectUserService projectUserService;
    @Resource
    private ProjectGroupService projectGroupService;
    @Resource
    private MongoTemplate mongoTemplate;

    @PostMapping("add")
    public Result<String> add(@RequestBody ProjectDto projectDto) {
        UserVo operator = UserUtils.getOperator();
        projectDto.initCreate(operator);
        String id = projectService.save(projectDto, operator);
        return ok(id);
    }

    @PostMapping("edit")
    public Result<String> edit(@RequestBody ProjectDto projectDto) {
        FastUtils.checkParams(projectDto.getId());
        UserVo operator = UserUtils.getOperator();
        projectDto.initUpdate(operator);
        projectService.save(projectDto, operator);
        return ok(projectDto.getId());
    }

    @PostMapping("assign")
    public Result<Object> assign(@RequestBody ProjectDto projectDto) {
        FastUtils.checkParams(projectDto.getId());
        UserVo operator = UserUtils.getOperator();
        PermUtils.checkProjectAdmin(mongoTemplate, projectDto.getId(), operator);
        projectService.assign(projectDto, operator);
        return ok(true);
    }

    @PostMapping("findProjectUser")
    public Result<List<User>> findProjectUser(@RequestBody ProjectUser projectUser) {
        return ok(projectUserService.findProjectUser(projectUser));
    }

    @PostMapping("findList")
    public Result<List<Project>> findList(@RequestBody ProjectDto projectDto) {
        return ok(projectService.findList(projectDto, UserUtils.getOperator()));
    }

    @PostMapping("findListByGroupForOwner")
    public Result<List<Object>> findListByGroupForOwner(@RequestBody ProjectDto projectDto) {
        ProjectGroup projectGroupDto = new ProjectGroup();
        if (StringUtils.isEmpty(projectDto.getGroupId())) {
            projectDto.setGroupId(null);
        }
        projectGroupDto.setParentId(projectDto.getGroupId());
        UserVo operator = UserUtils.getOperator();
        List<ProjectGroupVo> listByParent = projectGroupService.findListByParentForOwner(projectGroupDto, operator);
        List<Project> listByGroup = projectService.findListByGroupForOwner(projectDto, operator);
        List<Object> result = new LinkedList<>(listByParent);
        result.addAll(listByGroup);
        return ok(result);
    }

    @PostMapping("findListByGroupForOther")
    public Result<List<Object>> findListByGroupForOther(@RequestBody ProjectDto projectDto) {
        ProjectGroup projectGroupDto = new ProjectGroup();
        if (StringUtils.isEmpty(projectDto.getGroupId())) {
            projectDto.setGroupId(null);
        }
        projectGroupDto.setParentId(projectDto.getGroupId());
        UserVo operator = UserUtils.getOperator();
        List<ProjectGroupVo> listByParent = projectGroupService.findListByParentForOther(projectGroupDto, operator);
        List<Project> listByGroup = projectService.findListByGroupForOther(projectDto, operator);
        List<Object> result = new LinkedList<>(listByParent);
        result.addAll(listByGroup);
        return ok(result);
    }

    @PostMapping("editProjectUser")
    public Result<Object> editProjectUser(@RequestBody ProjectUserDto projectUserDto) {
        FastUtils.checkParams(projectUserDto.getProjectId());
        UserVo operator = UserUtils.getOperator();
        PermUtils.checkProjectAdmin(mongoTemplate, projectUserDto.getProjectId(), operator);
        projectUserService.edit(projectUserDto);
        return ok();
    }

    @PostMapping("remove")
    public Result<Object> remove(@RequestBody ProjectDto projectDto) {
        String projectId = projectDto.getId();
        FastUtils.checkParams(projectId);
        UserVo operator = UserUtils.getOperator();
        PermUtils.checkProjectOwner(mongoTemplate, projectId, operator);
        projectService.remove(projectId, operator);
        return ok();
    }

    @PostMapping("exportHtml")
    public void exportHtml(@RequestBody ProjectDto projectDto) throws Exception {
        String projectId = projectDto.getId();
        FastUtils.checkParams(projectId);
        String url = String.format(Constant.Url.EXPORT_URL, projectId, projectDto.getName(), projectDto.getEnvId());

        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        ChromeDriver driver = new ChromeDriver(options);
        driver.get(Constant.SysConfig.FRONT_HOST + url);
        Thread.sleep(5000);
        List<WebElement> elements = driver.findElements(By.className("el-table__row"));
        Document doc = Jsoup.parse(driver.getPageSource());
        driver.close();
        Elements links = doc.select("link[href]");
        String pre = Constant.Character.POINT;
        for (Element link : links) {
            link.attr("href", pre + link.attr("href"));
        }
        links = doc.select("script[src]");
        for (Element link : links) {
            link.attr("src", pre + link.attr("src"));
        }
        ByteArrayInputStream input = null;
        FileOutputStream output = null;
        try {
            input = new ByteArrayInputStream(doc.toString().getBytes());
            Runtime.getRuntime().exec("rm -rf /data/exportVpiHtml");
            File file = new File("/data/exportVpiHtml/");
            file.mkdirs();
            output = new FileOutputStream("/data/exportVpiHtml/testExport.html");
            IOUtils.copyLarge(input, output);

            Runtime.getRuntime().exec("cp -r /opt/uploadFile/assets /data/exportVpiHtml/");
            ZipUtil.pack(new File("/data/exportVpiHtml"), new File("/data/exportVpiHtml.zip"));
        } finally {
            IOUtils.closeQuietly(input, output);
        }
    }
}
