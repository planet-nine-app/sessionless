package app.planentnine.springsessionless.adapter.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Schema(name = "UserDto")
@Builder
@Jacksonized
public record RestUserDto(String publicKey) {
}
