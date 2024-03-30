package app.planentnine.springsessionless.adapter.web.dto.mapper;

import app.planentnine.springsessionless.adapter.web.dto.RestUserDto;
import app.planentnine.springsessionless.application.domain.User;
import app.planentnine.springsessionless.application.util.Sessionless;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RestUserDtoMapper {
    public User map(RestUserDto restUserDto){
        return new User(
                null,
                Sessionless.generateUuid(), //TODO move
                restUserDto.publicKey(),
                LocalDateTime.now()
        );
    }
}
