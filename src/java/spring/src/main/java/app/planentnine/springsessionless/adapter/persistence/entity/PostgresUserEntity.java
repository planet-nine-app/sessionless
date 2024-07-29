package app.planentnine.springsessionless.adapter.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostgresUserEntity {
    private UUID id;
    private UUID userUuid;
    private String publicKey;
}
