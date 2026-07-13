package com.fission.backend.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(MybatisPlusProperties.class)
public class MybatisPlusConfig {

    private final MybatisPlusProperties properties;

    public MybatisPlusConfig(MybatisPlusProperties properties) {
        this.properties = properties;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        if (this.properties.getMapperLocations() != null) {
            factoryBean.setMapperLocations(this.properties.resolveMapperLocations());
        }

        MybatisConfiguration configuration = new MybatisConfiguration();
        if (this.properties.getConfiguration() != null) {
            MybatisPlusProperties.CoreConfiguration core = this.properties.getConfiguration();
            if (core.getMapUnderscoreToCamelCase() != null) {
                configuration.setMapUnderscoreToCamelCase(core.getMapUnderscoreToCamelCase());
            }
            if (core.getLogImpl() != null) {
                configuration.setLogImpl(core.getLogImpl());
            }
        }
        factoryBean.setConfiguration(configuration);

        if (this.properties.getGlobalConfig() != null) {
            factoryBean.setGlobalConfig(this.properties.getGlobalConfig());
        }

        return factoryBean.getObject();
    }
}
