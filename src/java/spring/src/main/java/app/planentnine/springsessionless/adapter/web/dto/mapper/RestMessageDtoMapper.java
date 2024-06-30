package app.planentnine.springsessionless.adapter.web.dto.mapper;

import app.planentnine.springsessionless.adapter.web.dto.RestMessageDto;
import app.planentnine.springsessionless.application.domain.Message;
import org.springframework.stereotype.Component;

@Component
public class RestMessageDtoMapper {
    public Message map(RestMessageDto restMessageDto){
        return new Message(
                restMessageDto.userUuid(),
                restMessageDto.content(),
                restMessageDto.signature(),
                null //TODO: implementation on uuid/timestamp
        );
    }
}
