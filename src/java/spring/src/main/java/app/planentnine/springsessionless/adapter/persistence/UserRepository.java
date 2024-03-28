package app.planentnine.springsessionless.adapter.persistence;

import app.planentnine.springsessionless.application.domain.Message;
import app.planentnine.springsessionless.application.domain.User;
import app.planentnine.springsessionless.application.port.outgoing.CreateUserPort;
import app.planentnine.springsessionless.application.port.outgoing.VerifyMessagePort;
import org.springframework.stereotype.Repository;

@Repository
//TODO: complete
public class UserRepository implements CreateUserPort, VerifyMessagePort {
    @Override
    public User createUser(User user) {
        return null;
    }
    
    @Override
    public boolean verifyMessage(Message message) {
        return false;
    }
}
