package app.planentnine.springsessionless.adapter.config;

import app.planentnine.springsessionless.adapter.persistence.mybatis.PostgresUserRepository;
import app.planentnine.springsessionless.adapter.util.UuidTypeHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MybatisConfig {
    
    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setTypeHandlers(new UuidTypeHandler());
        
        return sessionFactoryBean.getObject();
    }
    
    @Bean
    public MapperFactoryBean<PostgresUserRepository> postgresUserRepository (SqlSessionFactory sqlSessionFactory) {
        MapperFactoryBean<PostgresUserRepository> factoryBean = new MapperFactoryBean<>(PostgresUserRepository.class);
        factoryBean.setSqlSessionFactory(sqlSessionFactory);
        return factoryBean;
    }
}
