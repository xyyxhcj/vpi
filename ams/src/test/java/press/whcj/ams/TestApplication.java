package press.whcj.ams;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;
import org.zeroturnaround.zip.ZTFileUtil;
import org.zeroturnaround.zip.ZipUtil;
import press.whcj.ams.common.ColumnName;
import press.whcj.ams.entity.*;
import press.whcj.ams.entity.dto.ProjectDto;
import press.whcj.ams.entity.dto.UserDto;
import press.whcj.ams.entity.vo.UserVo;
import press.whcj.ams.util.FastUtils;
import press.whcj.ams.util.JsonUtils;
import press.whcj.ams.web.ProjectController;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@SpringBootTest(classes = {AmsApplication.class})
@RunWith(SpringRunner.class)
public class TestApplication {
	@Resource
	private MongoTemplate mongoTemplate;
	@Resource
	private ProjectController projectController;

	@Test
	public void testMongodb01() {
		User user = new User();
		user.setUserName("lx");
		user.setLoginName("lx");
		mongoTemplate.save(user);
		System.out.println(user);
	}

	@Test
	public void testMongodb02() {
		List<User> all = mongoTemplate.findAll(User.class);
		System.out.println(all);
	}

	@Test
	public void testMongodb03() {
		User user = new User();
		user.setUserName("xkl");
		user.setLoginName("xkl");
		user.setPassword("123");
		user.setPhone("1881");
		user.setEmail("1881");
		user.setAvatarUrl("1881");
		user.setRemark("1881");
		user.setCreateTime(LocalDateTime.now());
		user.setUpdateTime(LocalDateTime.now());
		mongoTemplate.insertAll(Collections.singleton(user));
	}

	@Test
	public void testMongodb04() {
		Query query = new Query(Criteria.where(ColumnName.ID).is("5dddec8e8f11f93d68b92636"));
		Update update = new Update().set("remark", "Developer");
		mongoTemplate.updateFirst(query, update, User.class);
	}

	@Test
	public void testMongodb05() {
		ProjectGroup projectGroup = new ProjectGroup();
		projectGroup.setName("test2");
		User user = new User();
		user.setId("5de75653b503821a6966e655");
		projectGroup.setUpdate(user);
		mongoTemplate.save(projectGroup);
		//LookupOperation lookupOperation = LookupOperation.newLookup().from("user").localField("userId").foreignField("id").as("userList");
		//总数查询
		//AggregationOperation match = Aggregation.match(Criteria.where("projectId").is("5de77d980daf4a4caa9bbc22"));
		/*Aggregation aggregation = Aggregation.newAggregation(
				match(new Criteria()),
				lookup("user",  "userId","_id", "user_list"));
		AggregationResults<Map> result = mongoTemplate.aggregate(aggregation, Constant.CollectionName.PROJECT_USER, Map.class);
		Iterator<Map> iterator = result.iterator();
		List<Map> tmp = new LinkedList<>();
		while (iterator.hasNext()) {
			Map next = iterator.next();
			System.out.println(next);
			tmp.add(next);
		}
		System.out.println(tmp);*/
/*
		LookupOperation lookupOperation = LookupOperation.newLookup().from("user").localField("id").foreignField("userId").as("userList");
		AggregationOperation match = Aggregation.match(Criteria.where("projectId").is("5de77d980daf4a4caa9bbc22"));
		List<Map> userList = mongoTemplate.aggregate(Aggregation.newAggregation(match, lookupOperation), Constant.CollectionName.PROJECT_USER, Map.class).getMappedResults();
*/
	}

	@Test
	public void testMongodb06() {
		ProjectGroup byId = mongoTemplate.findById("5de8b31d9d62405cdf2fd373", ProjectGroup.class);
		//ProjectGroup byId = mongoTemplate.findById("5de8b184145527334d3514d5", ProjectGroup.class);
		System.out.println(byId);
	}

	@Test
	public void testMongodb07() {
		ProjectGroup parent = mongoTemplate.findById("5de8b51bb89618490dd61c6d", ProjectGroup.class);
		ProjectGroup projectGroup = new ProjectGroup();
		mongoTemplate.save(projectGroup);
	}

	@Test
	public void testMongodb08() {
		ProjectGroup projectGroup = mongoTemplate.findById("5de8b5763806693fd40f1fac", ProjectGroup.class);
		System.out.println(projectGroup);
	}
	@Test
	public void testFastUtils01() {
		UserDto userSource = new UserDto();
		userSource.setUserName("梁萧");
		MongoPage<UserVo> page = new MongoPage<>();
		page.setSize(30);
		userSource.setPage(page);
		UserDto userDest = new UserDto();
		userDest.setPhone("test");
		FastUtils.copyProperties(userSource, userDest);
		System.out.println(userSource.getPage());
		System.out.println(userDest.getPage());
		UserDto copy = FastUtils.deepCopy(userSource, new UserDto());
		System.out.println(userSource);
		System.out.println(copy);
	}

	@Test
	public void testUpdateStructureDataProjectId() {
		List<StructureData> structureDataList = mongoTemplate.find(new Query(Criteria.where(ColumnName.PROJECT_ID).is(null)), StructureData.class);
		Map<String, Structure> structureMap = mongoTemplate.findAll(Structure.class).stream().collect(Collectors.toMap(Structure::getId, v -> v));
		structureDataList.forEach(data->{
			data.setProjectId(structureMap.get(data.getStructureId()).getProjectId());
			mongoTemplate.save(data);
		});
	}

	@Test
	public void testUpdateHeaderProjectId() {
		List<ApiHeader> headerList = mongoTemplate.find(new Query(Criteria.where(ColumnName.PROJECT_ID).is(null)), ApiHeader.class);
		Map<String, Api> apiMap = mongoTemplate.findAll(Api.class).stream().collect(Collectors.toMap(Api::getId, v -> v));
		headerList.forEach(header->{
			Api api = apiMap.get(header.getApiId());
			if (api == null) {
				return;
			}
			header.setProjectId(api.getProjectId());
			mongoTemplate.save(header);
		});

	}

	@Test
	public void testExportHtml() throws Exception {
		// {"id":"5de9bf91ecae1f3a0d7bf7cc","name":"Demo","envId":"5e3777c4bc1dfd3b727fa94e"}
		ProjectDto projectDto = JsonUtils.json2Pojo("{\"id\":\"5de9bf91ecae1f3a0d7bf7cc\",\"name\":\"Demo\",\"envId\":\"5e3777c4bc1dfd3b727fa94e\"}", ProjectDto.class);
		projectController.exportHtml(projectDto);
	}

	@Test
	public void testUrlDecode() throws Exception {
		System.out.println(StringEscapeUtils.unescapeXml("&lt; &gt; &amp; "));
	}

	@Test
	public void testZip() throws IOException {
		Collection<File> files = ZTFileUtil.listFiles(new File("D:\\tmp\\uploadFile"));
		System.out.println(files);
		ZipUtil.packEntries(files.toArray(new File[0]), new File("D:\\tmp\\2.zip"));
		//ZipUtil.pack(new File("D:\\tmp\\uploadFile"), new File("D:\\tmp\\1.zip"));
	}
}
