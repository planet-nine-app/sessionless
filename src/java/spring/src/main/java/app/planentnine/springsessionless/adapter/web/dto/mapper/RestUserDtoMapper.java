package app.planentnine.springsessionless.adapter.web.dto.mapper;

import app.planentnine.springsessionless.adapter.web.dto.RestUserDto;
import app.planentnine.springsessionless.application.domain.User;
import org.springframework.stereotype.Component;

@Component
public class RestUserDtoMapper {
    public User map(RestUserDto restUserDto){
        return new User(
                null,
                null, //TODO move
                restUserDto.publicKey(),
                null
        );
    }
}
