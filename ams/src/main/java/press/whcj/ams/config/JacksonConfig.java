package press.whcj.ams.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@SpringBootConfiguration
public class JacksonConfig {
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		addLocalDateTimeSerializer(objectMapper);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	}

	public static void addLocalDateTimeSerializer(ObjectMapper objectMapper) {
		JavaTimeModule timeModule = new JavaTimeModule();
		timeModule.addDeserializer(LocalDateTime.class, new JsonDateTimeDeserializer());
		timeModule.addSerializer(LocalDateTime.class, new JsonDateTimeSerializer());
		objectMapper.registerModule(timeModule);
	}

	static class JsonDateTimeSerializer extends JsonSerializer<LocalDateTime> {
		@Override
		public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
			jsonGenerator.writeNumber(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		}
	}

	static class JsonDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
		@Override
		public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
			return Instant.ofEpochMilli(jsonParser.getLongValue()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		}
	}
}
