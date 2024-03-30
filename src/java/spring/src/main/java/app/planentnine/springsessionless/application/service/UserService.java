package app.planentnine.springsessionless.application.service;

import app.planentnine.springsessionless.application.domain.User;
import app.planentnine.springsessionless.application.port.incoming.CreateUserUseCase;
import app.planentnine.springsessionless.application.port.outgoing.CreateUserPort;
import app.planentnine.springsessionless.application.util.Sessionless;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService implements CreateUserUseCase {
    private final CreateUserPort createUserPort;
    
    @Autowired
    public UserService(CreateUserPort createUserPort){
        this.createUserPort = createUserPort;
    }
    
    @Override
    public User createUser(User user) {
        User createdUser = new User(
                UUID.randomUUID(),
                Sessionless.generateUuid(),
                user.publicKey(),
                LocalDateTime.now()
        );
        return createUserPort.createUser(createdUser);
    }
}
