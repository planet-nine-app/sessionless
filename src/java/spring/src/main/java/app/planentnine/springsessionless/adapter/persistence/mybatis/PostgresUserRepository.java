package app.planentnine.springsessionless.adapter.persistence.mybatis;

import app.planentnine.springsessionless.adapter.persistence.entity.PostgresUserEntity;
import app.planentnine.springsessionless.adapter.util.UuidTypeHandler;
import app.planentnine.springsessionless.application.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public interface PostgresUserRepository {
    @Select("SELECT * FROM user WHERE user_uuid = #{userUuid}")
    @Results(id = "userResultMap", value = {
            @Result(property = "id", column = "id", javaType = UUID.class, typeHandler = UuidTypeHandler.class),
            @Result(property = "userUuid", column = "user_uuid", javaType = UUID.class, typeHandler = UuidTypeHandler.class),
            @Result(property = "publicKey", column = "public_key", javaType = String.class),
            @Result(property = "dateCrated", column = "date_created", javaType = LocalDateTime.class)
    })
    Optional<PostgresUserEntity> loadUserByUuid(@Param("userUuid") UUID userUuid);
    
    @Insert("INSERT INTO user (id, user_uuid, public_key, date_created) " +
            "VALUES #{id}, #{userUuid}, #{publicKey}, #{dateCreated}")
    void createNewUser(PostgresUserEntity postgresUserEntity);
}
