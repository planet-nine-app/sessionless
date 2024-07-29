package app.planentnine.springsessionless.application.port.incoming;

import app.planentnine.springsessionless.application.domain.Message;
import app.planentnine.springsessionless.application.domain.User;

public interface CreateUserUseCase {
    User createUser(Message message, User user);
}
