package app.planentnine.springsessionless.application.port.outgoing;

import app.planentnine.springsessionless.application.domain.User;

public interface CreateUserPort {
    User createUser(User user);
}
