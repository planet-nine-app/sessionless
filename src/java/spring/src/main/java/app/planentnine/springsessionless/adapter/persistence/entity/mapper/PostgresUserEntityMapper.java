package app.planentnine.springsessionless.adapter.persistence.entity.mapper;

import app.planentnine.springsessionless.adapter.persistence.entity.PostgresUserEntity;
import app.planentnine.springsessionless.application.domain.User;
import org.springframework.stereotype.Component;

@Component
public class PostgresUserEntityMapper {
    public User map(PostgresUserEntity postgresUserEntity){
        return new User(
                postgresUserEntity.getId(),
                postgresUserEntity.getUserUuid(),
                postgresUserEntity.getPublicKey(),
                postgresUserEntity.getDateCreated()
        );
    }
    
    public PostgresUserEntity map(User user){
        return PostgresUserEntity.builder()
                .id(user.id())
                .userUuid(user.userUuid())
                .publicKey(user.publicKey())
                .dateCreated(user.dateCreated())
                .build();
    }
}
