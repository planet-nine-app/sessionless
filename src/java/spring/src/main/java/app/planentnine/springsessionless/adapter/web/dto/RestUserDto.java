package app.planentnine.springsessionless.adapter.web.dto;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Builder
@Jacksonized
public record RestUserDto(UUID uuid, String pubKey) {
}
