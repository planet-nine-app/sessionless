package app.planentnine.springsessionless.application.domain;

import java.util.UUID;

public record Message(UUID userUUID, String coolness, String payload, Long timestamp, String signature) {
}
