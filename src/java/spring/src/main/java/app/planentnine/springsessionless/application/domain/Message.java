package app.planentnine.springsessionless.application.domain;

import java.util.List;
import java.util.UUID;

//TODO convert signature to appropriate type
public record Message(UUID userUuid, String message, List<String> signature) {
}
