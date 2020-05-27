package press.whcj.ams.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.lang.NonNull;
import org.springframework.session.data.mongo.JacksonMongoSessionConverter;
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import press.whcj.ams.common.Constant;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author xyyxhcj@qq.com
 * @since 2019/12/31
 */
@SpringBootConfiguration
@EnableMongoHttpSession(maxInactiveIntervalInSeconds = Constant.SysConfig.SESSION_TIME_OUT)
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Resource
    private MongoPoolProperties properties;

    @Bean
    public JacksonMongoSessionConverter mongoSessionConverter() {
        return new JacksonMongoSessionConverter();
    }

    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return new HeaderHttpSessionIdResolver(Constant.SysConfig.AUTH_HEADER);
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, getDatabaseName());
    }
    //
    //@Bean
    //public DefaultMongoTypeMapper defaultMongoTypeMapper() {
    //    return new DefaultMongoTypeMapper(null);
    //}

    @Override
    protected void configureClientSettings(@NonNull MongoClientSettings.Builder builder) {
        List<ServerAddress> hosts = new LinkedList<>();
        for (String address : properties.getAddress()) {
            String[] hostAndPort = address.split(":");
            String host = hostAndPort[0];
            int port = Integer.parseInt(hostAndPort[1]);
            ServerAddress serverAddress = new ServerAddress(host, port);
            hosts.add(serverAddress);
        }
        builder.credential(MongoCredential.createCredential(properties.getUsername(), properties.getDatabase(), properties.getPassword()))
                .applyToConnectionPoolSettings(connectionPoolSettings ->
                        connectionPoolSettings.maxWaitTime(properties.getMaxWaitTime(), TimeUnit.SECONDS)
                                .maxConnectionIdleTime(properties.getMaxConnectionIdleTime(), TimeUnit.SECONDS)
                                .maxConnectionLifeTime(properties.getMaxConnectionLifeTime(), TimeUnit.SECONDS))
                .applyToSslSettings(sslSettings -> {
                    sslSettings.enabled(properties.getSslEnabled());
                    sslSettings.invalidHostNameAllowed(properties.getSslInvalidHostNameAllowed());
                })
                .applyToSocketSettings(socketSetting ->
                        socketSetting.connectTimeout(properties.getConnectTimeout(), TimeUnit.SECONDS))
                .applyToServerSettings(serverSettings ->
                        serverSettings.heartbeatFrequency(properties.getHeartbeatFrequency(), TimeUnit.SECONDS)
                                .minHeartbeatFrequency(properties.getMinHeartbeatFrequency(), TimeUnit.SECONDS))
                .applyToClusterSettings(clusterSettings ->
                        clusterSettings.serverSelectionTimeout(properties.getServerSelectionTimeout(), TimeUnit.SECONDS)
                                .localThreshold(properties.getLocalThreshold(), TimeUnit.SECONDS)
                                .hosts(hosts));
        ;
    }

    @Override
    @NonNull
    protected String getDatabaseName() {
        return properties.getDatabase();
    }
}
