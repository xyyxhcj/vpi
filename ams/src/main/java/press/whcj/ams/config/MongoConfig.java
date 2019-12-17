package press.whcj.ams.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.session.data.mongo.JacksonMongoSessionConverter;
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import press.whcj.ams.common.Constant;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@SpringBootConfiguration
@EnableMongoHttpSession(maxInactiveIntervalInSeconds = Constant.SysConfig.SESSION_TIME_OUT)
public class MongoConfig {
	@Bean
	public JacksonMongoSessionConverter mongoSessionConverter() {
		return new JacksonMongoSessionConverter();
	}

	@Bean
	public HttpSessionIdResolver httpSessionIdResolver() {
		return new HeaderHttpSessionIdResolver(Constant.SysConfig.AUTH_HEADER);
	}

	@Bean
	@Resource
	public MongoDbFactory mongoDbFactory(MongoPoolProperties properties) {
		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
		builder.connectionsPerHost(properties.getMaxConnectionsPerHost());
		builder.minConnectionsPerHost(properties.getMinConnectionsPerHost());
		builder.threadsAllowedToBlockForConnectionMultiplier(
				properties.getThreadsAllowedToBlockForConnectionMultiplier());
		builder.serverSelectionTimeout(properties.getServerSelectionTimeout());
		builder.maxWaitTime(properties.getMaxWaitTime());
		builder.maxConnectionIdleTime(properties.getMaxConnectionIdleTime());
		builder.maxConnectionLifeTime(properties.getMaxConnectionLifeTime());
		builder.connectTimeout(properties.getConnectTimeout());
		builder.socketTimeout(properties.getSocketTimeout());
		builder.sslEnabled(properties.getSslEnabled());
		builder.sslInvalidHostNameAllowed(properties.getSslInvalidHostNameAllowed());
		builder.heartbeatFrequency(properties.getHeartbeatFrequency());
		builder.minHeartbeatFrequency(properties.getMinHeartbeatFrequency());
		builder.heartbeatConnectTimeout(properties.getHeartbeatConnectTimeout());
		builder.heartbeatSocketTimeout(properties.getHeartbeatSocketTimeout());
		builder.localThreshold(properties.getLocalThreshold());
		MongoClientOptions mongoClientOptions = builder.build();
		List<ServerAddress> serverAddressList = new LinkedList<>();
		for (String address : properties.getAddress()) {
			String[] hostAndPort = address.split(":");
			String host = hostAndPort[0];
			int port = Integer.parseInt(hostAndPort[1]);
			ServerAddress serverAddress = new ServerAddress(host, port);
			serverAddressList.add(serverAddress);
		}
		MongoCredential credential = MongoCredential.createCredential(properties.getUsername(), properties.getDatabase(), properties.getPassword());
		MongoClient mongoClient = new MongoClient(serverAddressList, credential, mongoClientOptions);
		return new SimpleMongoDbFactory(mongoClient, properties.getDatabase());
	}

	@Bean
	@Primary
	public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory) {
		return new MongoTemplate(mongoDbFactory);
	}

	@Bean
	public DefaultMongoTypeMapper defaultMongoTypeMapper() {
		return new DefaultMongoTypeMapper(null);
	}
}
