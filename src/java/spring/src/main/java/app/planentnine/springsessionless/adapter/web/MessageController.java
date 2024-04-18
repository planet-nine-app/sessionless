package app.planentnine.springsessionless.adapter.web;

import app.planentnine.springsessionless.adapter.web.dto.RestMessageDto;
import app.planentnine.springsessionless.adapter.web.dto.mapper.RestMessageDtoMapper;
import app.planentnine.springsessionless.application.port.incoming.VerifyMessageUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MessageController {
    
    private final VerifyMessageUseCase verifyMessageUseCase;
    private final RestMessageDtoMapper messageDtoMapper;

    @Autowired
    public MessageController(VerifyMessageUseCase verifyMessageUseCase,
                             RestMessageDtoMapper messageDtoMapper){
        this.verifyMessageUseCase = verifyMessageUseCase;
        this.messageDtoMapper = messageDtoMapper;
    }
    
    @PostMapping("/do-cool-stuff")
    public ResponseEntity<String> verifyMessage(@RequestBody RestMessageDto messageDto){
        boolean verified = verifyMessageUseCase.verifyMessage(messageDtoMapper.map(messageDto));
        if (verified) {
            return ResponseEntity.accepted().body("The message content was verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid request parameters provided");
        }
    }
}
