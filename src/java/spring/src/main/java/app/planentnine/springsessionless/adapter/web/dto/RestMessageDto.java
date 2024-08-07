package app.planentnine.springsessionless.adapter.web.dto;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Builder
@Jacksonized
public record RestMessageDto(UUID uuid, String coolness, Long timestamp, String signature) {
}
