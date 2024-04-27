package app.planentnine.springsessionless.application.port.incoming;

import app.planentnine.springsessionless.application.domain.User;

public interface CreateUserUseCase {
    User createUser(User user);
}
