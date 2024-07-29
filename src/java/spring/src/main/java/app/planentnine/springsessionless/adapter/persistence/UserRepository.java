package app.planentnine.springsessionless.adapter.persistence;

import app.planentnine.springsessionless.adapter.persistence.entity.mapper.PostgresUserEntityMapper;
import app.planentnine.springsessionless.adapter.persistence.mybatis.PostgresUserRepository;
import app.planentnine.springsessionless.application.domain.User;
import app.planentnine.springsessionless.application.port.outgoing.CreateUserIfNotExistsPort;
import app.planentnine.springsessionless.application.port.outgoing.LoadUserByUserUuidPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Slf4j
public class UserRepository implements CreateUserIfNotExistsPort, LoadUserByUserUuidPort {
    
    private final PostgresUserRepository postgresUserRepository;
    private final PostgresUserEntityMapper postgresUserEntityMapper;
    
    @Autowired
    public UserRepository(PostgresUserRepository postgresUserRepository,
                          PostgresUserEntityMapper postgresUserEntityMapper){
        this.postgresUserRepository = postgresUserRepository;
        this.postgresUserEntityMapper = postgresUserEntityMapper;
    }
    
    @Override
    public User createUserIfNotExists(User user) {
        postgresUserRepository.createNewUser(postgresUserEntityMapper.map(user));
        return loadByUserUuid(user.userUUID())
                .orElseThrow(() -> new RuntimeException("Something went wrong creating new user: " + user.userUUID()));
    }
    
    @Override
    public Optional<User> loadByUserUuid(UUID userUuid) {
        return postgresUserRepository.loadUserByUuid(userUuid).map(postgresUserEntityMapper::map);
    }
}
