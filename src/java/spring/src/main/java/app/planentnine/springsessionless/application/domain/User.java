package app.planentnine.springsessionless.application.domain;

import java.util.UUID;

public record User(UUID id, UUID userUUID, String publicKey) {
}
