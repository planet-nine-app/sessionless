package app.planentnine.springsessionless.adapter.web.dto.mapper;
import app.planentnine.springsessionless.adapter.web.dto.RestCreateUserDto;
import app.planentnine.springsessionless.adapter.web.dto.RestMessageDto;
import app.planentnine.springsessionless.application.domain.Message;
import org.springframework.stereotype.Component;

@Component
public class RestMessageDtoMapper {
    public Message map(RestMessageDto restMessageDto){
        return new Message(
                restMessageDto.uuid(),
                restMessageDto.coolness(),
                String.format("{\"uuid\":\"%s\",\"coolness\":\"%s\",\"timestamp\":\"%s\"}",
                        restMessageDto.uuid(), restMessageDto.coolness(), restMessageDto.timestamp()),
                restMessageDto.timestamp(),
                restMessageDto.signature()
        );
    }
    
    public Message maptToMessage(RestCreateUserDto restCreateUserDto){
        return new Message(
                null,
                restCreateUserDto.enteredText(),
                String.format("{\"pubKey\":\"%s\",\"enteredText\":\"%s\",\"timestamp\":\"%s\"}",
                        restCreateUserDto.pubKey(), restCreateUserDto.enteredText(), restCreateUserDto.timestamp()),
                restCreateUserDto.timestamp(),
                restCreateUserDto.signature()
        );
    }
}
