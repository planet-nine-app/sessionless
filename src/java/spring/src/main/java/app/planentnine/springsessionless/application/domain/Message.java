package app.planentnine.springsessionless.application.domain;

import java.time.LocalDateTime;
import java.util.UUID;

//TODO convert signature to appropriate type
public record Message(UUID userUuid, String content, String[] signature, LocalDateTime timestamp) {
}
