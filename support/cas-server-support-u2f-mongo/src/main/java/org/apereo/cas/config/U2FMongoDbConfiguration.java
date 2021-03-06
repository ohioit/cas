package org.apereo.cas.config;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.CipherExecutor;
import org.apereo.cas.adaptors.u2f.storage.U2FMongoDbDeviceRepository;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.adaptors.u2f.storage.U2FDeviceRepository;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.mongo.MongoDbConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is {@link U2FMongoDbConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Configuration("u2fMongoDbConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class U2FMongoDbConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("u2fRegistrationRecordCipherExecutor")
    private CipherExecutor u2fRegistrationRecordCipherExecutor;
    
    @Bean
    public U2FDeviceRepository u2fDeviceRepository() {
        final var u2f = casProperties.getAuthn().getMfa().getU2f();
        
        final var factory = new MongoDbConnectionFactory();
        final var mongoProps = u2f.getMongo();
        final var mongoTemplate = factory.buildMongoTemplate(mongoProps);

        factory.createCollection(mongoTemplate, mongoProps.getCollection(), mongoProps.isDropCollection());
        
        final LoadingCache<String, String> requestStorage =
                Caffeine.newBuilder()
                        .expireAfterWrite(u2f.getExpireRegistrations(), u2f.getExpireRegistrationsTimeUnit())
                        .build(key -> StringUtils.EMPTY);
        final var repo = new U2FMongoDbDeviceRepository(requestStorage, mongoTemplate, u2f.getExpireRegistrations(),
                u2f.getExpireDevicesTimeUnit(), mongoProps.getCollection());
        repo.setCipherExecutor(this.u2fRegistrationRecordCipherExecutor);
        return repo;
    }

}
