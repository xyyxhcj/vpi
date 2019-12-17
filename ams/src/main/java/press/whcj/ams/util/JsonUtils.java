package press.whcj.ams.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import press.whcj.ams.config.JacksonConfig;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author xyyxhcj@qq.com
 * @since 2018/2/6
 */
@Component
public class JsonUtils {
	private static ObjectMapper MAPPER = new ObjectMapper();
	public static ObjectMapper NON_NULL_MAPPER;
	public static ObjectMapper SNAKE_CASE_MAPPER;
	public static ObjectMapper NON_NULL_SNAKE_CASE_MAPPER;
	private final static Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

	@Resource
	private ObjectMapper objectMapper;

	@PostConstruct
	void init() {
		MAPPER = objectMapper;
		enableEnumUse2String();
		NON_NULL_MAPPER = build(new ObjectMapper(), JsonInclude.Include.NON_NULL, null);
		SNAKE_CASE_MAPPER = build(new ObjectMapper(), null, PropertyNamingStrategy.SNAKE_CASE);
		NON_NULL_SNAKE_CASE_MAPPER = build(new ObjectMapper(), JsonInclude.Include.NON_NULL, PropertyNamingStrategy.SNAKE_CASE);
	}

	/**
	 * Object/Map To Json
	 *
	 * @param data data
	 * @return json
	 */
	public static String object2Json(@NotNull final Object data) {
		return object2Str(MAPPER, data, "write to json string error:");
	}

	/**
	 * Object/Map To Json ,without null
	 *
	 * @param data data
	 * @return json
	 */
	public static String object2JsonIgNull(@NotNull final Object data) {
		return object2Str(NON_NULL_MAPPER, data, "write to json string error:");
	}

	/**
	 * Object/Map To Json/xml
	 *
	 * @param mapper xmlMapper->xml objectMapper->json
	 * @param data   data
	 * @return json
	 */
	public static String object2Str(@NotNull final ObjectMapper mapper, @NotNull final Object data) {
		return object2Str(mapper, data, "write to string error:");
	}

	/**
	 * Object/Map To str
	 *
	 * @param mapper xmlMapper->xml objectMapper->json
	 * @param data   data
	 * @param msg    exceptionMsg
	 * @return str
	 */
	private static String object2Str(@NotNull final ObjectMapper mapper, @NotNull final Object data, final String msg) {
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(data);
		} catch (IOException e) {
			LOGGER.warn(msg + data, e);
		}
		return jsonString;
	}

	/**
	 * str(json/xml) to object
	 *
	 * @param mapper   xmlMapper->xml objectMapper->json
	 * @param jsonData data
	 * @param msg      exceptionMsg
	 * @return str
	 */
	public static <T> T str2Pojo(@NotNull final ObjectMapper mapper, final String jsonData, final Class<T> beanType, final String msg) {
		if (StringUtils.isEmpty(jsonData)) {
			return null;
		}
		T result = null;
		try {
			result = mapper.readValue(jsonData, beanType);
		} catch (IOException e) {
			LOGGER.warn(msg + jsonData, e);
		}
		return result;
	}

	/**
	 * json to pojo/map< String,Map >
	 *
	 * @param jsonData jsonData
	 * @param beanType beanType
	 * @param <T>      T
	 * @return return
	 */
	public static <T> T json2Pojo(final String jsonData, final Class<T> beanType) {
		return str2Pojo(MAPPER, jsonData, beanType, "parse json string error:");
	}

	/**
	 * json to list
	 *
	 * @param jsonData jsonData
	 * @param beanType beanType
	 * @param <T>      List
	 * @return List
	 */
	public static <T> T json2List(final String jsonData, final Class beanType) {
		return json2JavaType(jsonData, buildCollectionType(List.class, beanType));
	}

	/**
	 * json to < pojo > list/Map
	 * Collection: List< Bean >, buildCollectionType(),buildMapType()
	 * buildCollectionType -> 'public static < T > List< T > jsonToList(String jsonData, Class<T> beanType)'
	 *
	 * @param jsonData jsonData
	 * @param javaType from buildCollectionType/buildMapType
	 * @param <T>      T
	 * @return return
	 */
	public static <T> T json2JavaType(final String jsonData, final JavaType javaType) {
		if (StringUtils.isEmpty(jsonData)) {
			return null;
		}
		T result = null;
		try {
			result = MAPPER.readValue(jsonData, javaType);
		} catch (IOException e) {
			LOGGER.warn("parse json string error:" + jsonData, e);
		}
		return result;
	}

	/**
	 * user Enum toString,not name
	 */
	public static void enableEnumUse2String() {
		MAPPER.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
	}

	/**
	 * Json To Object/Map< String,Bean >
	 *
	 * @param jsonData      jsonData
	 * @param typeReference new TypeReference< LinkedHashMap< String, Bean > >() {}
	 * @param <T>           T
	 * @return return
	 */
	public static <T> T json2MapPojo(final String jsonData, final TypeReference<T> typeReference) {
		if (StringUtils.isEmpty(jsonData)) {
			return null;
		}
		T t = null;
		try {
			t = MAPPER.readValue(jsonData, typeReference);
		} catch (IOException e) {
			LOGGER.warn("json to map error:" + jsonData, e);
		}
		return t;
	}

	/**
	 * buildCollectionType
	 */
	public static JavaType buildCollectionType(final Class<? extends Collection> collectionClass, final Class<?> elementClass) {
		return MAPPER.getTypeFactory().constructCollectionType(collectionClass, elementClass);
	}

	/**
	 * buildMapType
	 */
	public static JavaType buildMapType(final Class<? extends Map> mapClass, final Class<?> keyClass, final Class<?> valueClass) {
		return MAPPER.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
	}

	/**
	 * only update json exist properties
	 */
	public static void update(final String jsonData, final Object object) {
		if (StringUtils.isEmpty(jsonData)) {
			return;
		}
		try {
			MAPPER.readerForUpdating(object).readValue(jsonData);
		} catch (IOException e) {
			LOGGER.warn("update json string:" + jsonData + " to object:" + object + " error.", e);
		}
	}

	/**
	 * to jsonp
	 */
	public static String toJsonp(final String functionName, final Object object) {
		return object2Json(new JSONPObject(functionName, object));
	}

	/**
	 * json to JsonNode
	 *
	 * @param json json
	 * @return JsonNode
	 */
	public static JsonNode readTree(final String json) {
		JsonNode jsonNode = null;
		try {
			jsonNode = MAPPER.readTree(json);
		} catch (IOException e) {
			LOGGER.warn("json to jsonNode error:" + json, e);
		}
		return jsonNode;
	}

	/**
	 * build ObjectMapper
	 *
	 * @param include                Include.NON_NULL Include.NON_EMPTY
	 * @param propertyNamingStrategy convertType:PropertyNamingStrategy.SNAKE_CASE
	 * @return json
	 */
	public static <T extends ObjectMapper> ObjectMapper build(final T mapper, final JsonInclude.Include include, final PropertyNamingStrategy propertyNamingStrategy) {
		// define included: NON_NULL,NON_EMPTY,...
		if (include != null) {
			mapper.setSerializationInclusion(include);
		}
		if (propertyNamingStrategy != null) {
			mapper.setPropertyNamingStrategy(propertyNamingStrategy);
		}
		JacksonConfig.addLocalDateTimeSerializer(mapper);
		// ignore properties(str exist,pojo not exist)
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return mapper;
	}
}
