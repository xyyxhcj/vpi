package press.whcj.ams.util;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import press.whcj.ams.common.ColumnName;
import press.whcj.ams.common.Constant;
import press.whcj.ams.exception.ResultCode;
import press.whcj.ams.exception.ServiceException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.beans.PropertyDescriptor;
import java.nio.charset.Charset;
import java.util.*;


/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */

public class FastUtils {
	private final static Logger LOGGER = LoggerFactory.getLogger(FastUtils.class);

	/**
	 * split string use delimited
	 *
	 * @param src       sliced string
	 * @param delimited delimited
	 * @param <T>       T
	 * @return list<T>
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> splitStr(String src, String delimited) {
		List<T> list = new LinkedList<>();
		if (src == null || "".equals(src.trim())) {
			return list;
		}
		int index;
		int length = delimited.length();
		String str;
		while ((index = src.indexOf(delimited)) != -1) {
			str = src.substring(0, index);
			if (str.length() != 0) {
				list.add((T) str.trim());
			}
			src = src.substring(index + length);
		}
		if (src.length() != 0) {
			list.add((T) src.trim());
		}
		return list;
	}

	/**
	 * check not null
	 *
	 * @param objects objects
	 */
	public static void checkNull(Object... objects) {
		for (Object obj : objects) {
			if (obj == null) {
				throw new ServiceException(ResultCode.RECORD_NOT_EXIST);
			}
		}
	}

	/**
	 * check request parameter
	 *
	 * @param objects parameters
	 */
	public static void checkParams(Object... objects) {
		for (Object obj : objects) {
			if (obj instanceof Collection) {
				if (CollectionUtils.isEmpty((Collection<?>) obj)) {
					throw new ServiceException(ResultCode.PARAMS_NOT);
				}
			} else if (StringUtils.isEmpty(obj)) {
				throw new ServiceException(ResultCode.PARAMS_NOT);
			}
		}
	}

	/**
	 * get request parameters
	 */

	public static StringBuilder getRequestParams(HttpServletRequest request) {
		Set<Map.Entry<String, String[]>> entries = request.getParameterMap().entrySet();
		StringBuilder sb = new StringBuilder();
		if (entries.size() != 0) {
			for (Map.Entry<String, String[]> entry : entries) {
				sb.append(Constant.Character.AND).append(entry.getKey()).append(Constant.Character.EQUALS).append(String.join(Constant.Character.COMMA, entry.getValue()));
			}
		}
		return sb;
	}

	/**
	 * logger print requestBody
	 */
	public static void loggerRequestBody(Logger logger, HttpServletRequest request) {
		if (request instanceof ContentCachingRequestWrapper) {
			ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
			logger.error("REQUEST_BODY：\n{}", new String(wrapper.getContentAsByteArray(), Charset.forName(wrapper.getCharacterEncoding())));
		}
	}

	/**
	 * shallow copy
	 *
	 * @param source      source
	 * @param destination dest
	 */
	public static <T> @NotNull T copyProperties(final Object source, @NotNull final T destination) {
		BeanUtils.copyProperties(source, destination, getNullAndEmptyPropertyNames(source));
		return destination;
	}

	/**
	 * get bean's null&empty properties
	 *
	 * @param obj obj
	 * @return java.lang.String[]
	 * @author xyyxhcj@qq.com
	 * @date 2019/12/3 14:59
	 **/
	public static String[] getNullAndEmptyPropertyNames(Object obj) {
		final BeanWrapper src = new BeanWrapperImpl(obj);
		PropertyDescriptor[] pds = src.getPropertyDescriptors();
		Set<String> emptyNames = new HashSet<>();
		for (PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (StringUtils.isEmpty(srcValue)) {
				emptyNames.add(pd.getName());
			}
		}
		return emptyNames.toArray(new String[0]);
	}

	/**
	 * deep copy
	 *
	 * @param source      source
	 * @param destination dest
	 */
	public static <T> T deepCopy(final Object source, final T destination) {
		return deepCopy(source, destination, buildMapper());
	}

	/**
	 * deep copy use custom mapper
	 *
	 * @param source     source
	 * @param destination dest
	 * @param mapper      custom mapper
	 */
	public static <T> T deepCopy(final Object source, final T destination, Mapper mapper) {
		mapper.map(source, destination);
		return destination;
	}

	/**
	 * build Mapper
	 *
	 * @return BeanMapper
	 */
	public static Mapper buildMapper() {
		return DozerBeanMapperBuilder.buildDefault();
	}

	public static <T> void checkNameAndSave(String id, boolean isUpdate, String name, T t, MongoTemplate mongoTemplate, Criteria checkNameCriteria) {
		checkNameCriteria = checkNameCriteria.and(ColumnName.NAME).is(name).and(ColumnName.IS_DEL).ne(Constant.Is.YES);
		if (isUpdate) {
			checkNameCriteria = checkNameCriteria.and(ColumnName.ID).ne(id);
		}
		Object checkNameExist = mongoTemplate.findOne(new Query(checkNameCriteria), t.getClass());
		if (checkNameExist != null) {
			throw new ServiceException(ResultCode.NAME_EXIST);
		}
		mongoTemplate.save(t);
	}
}
