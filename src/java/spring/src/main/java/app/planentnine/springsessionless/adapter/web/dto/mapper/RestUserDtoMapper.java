package app.planentnine.springsessionless.adapter.web.dto.mapper;

import app.planentnine.springsessionless.adapter.web.dto.RestUserDto;
import app.planentnine.springsessionless.application.domain.User;
import org.springframework.stereotype.Component;

@Component
public class RestUserDtoMapper {
    public RestUserDto map(User user){
        return new RestUserDto(
                user.userUUID(),
                user.publicKey()
        );
    }
    
    public User map(RestUserDto restUserDto){
        return new User(
                null,
                null,
                restUserDto.pubKey()
        );
    }
}
