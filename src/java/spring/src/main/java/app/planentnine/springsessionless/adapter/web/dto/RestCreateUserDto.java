package app.planentnine.springsessionless.adapter.web.dto;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record RestCreateUserDto(String pubKey, String enteredText, Long timestamp, String signature) {
}
