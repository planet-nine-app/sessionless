package app.planentnine.springsessionless.adapter.web.dto.mapper;

import app.planentnine.springsessionless.adapter.web.dto.RestMessageDto;
import app.planentnine.springsessionless.application.domain.Message;
import org.springframework.stereotype.Component;

@Component
public class RestMessageDtoMapper {
    public Message map(RestMessageDto restMessageDto){
        String content = String.format("{\"uuid\":\"%s\",\"coolness\":\"%s\",\"timestamp\":\"%s\"}", restMessageDto.uuid(), restMessageDto.coolness(), restMessageDto.timestamp());
        return new Message(
                restMessageDto.uuid(),
                content,
                restMessageDto.signature(),
                restMessageDto.timestamp()
        );
    }
}
