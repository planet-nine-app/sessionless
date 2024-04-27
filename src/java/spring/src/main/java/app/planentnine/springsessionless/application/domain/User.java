package app.planentnine.springsessionless.application.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record User(UUID id, UUID userUuid, String publicKey, LocalDateTime dateCreated) {
}
