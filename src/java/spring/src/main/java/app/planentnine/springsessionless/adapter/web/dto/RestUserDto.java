package app.planentnine.springsessionless.adapter.web.dto;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record RestUserDto(String pubKey, String enteredText, String timestamp) {
}
