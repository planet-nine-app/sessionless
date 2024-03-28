package app.planentnine.springsessionless.application.domain;

import java.util.UUID;

public record User(UUID userUuid, String publicKey) {
}
