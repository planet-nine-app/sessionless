package app.planentnine.springsessionless.application.service;

import app.planentnine.springsessionless.application.domain.User;
import app.planentnine.springsessionless.application.port.incoming.CreateUserUseCase;
import app.planentnine.springsessionless.application.port.outgoing.CreateUserPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
//TODO: Add user creation
public class UserService implements CreateUserUseCase {
    private final CreateUserPort createUserPort;
    
    @Autowired
    public UserService(CreateUserPort createUserPort){
        this.createUserPort = createUserPort;
    }
    
    @Override
    public User createUser(User user) {
        return createUserPort.createUser(user);
    }
}
