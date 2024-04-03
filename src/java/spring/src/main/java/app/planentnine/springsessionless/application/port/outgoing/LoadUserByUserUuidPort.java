package app.planentnine.springsessionless.application.port.outgoing;

import app.planentnine.springsessionless.application.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface LoadUserByUserUuidPort {
    Optional<User> loadByUserUuid(UUID userUuid);
}
