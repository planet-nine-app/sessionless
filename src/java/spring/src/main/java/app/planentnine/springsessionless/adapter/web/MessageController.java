package app.planentnine.springsessionless.adapter.web;

import app.planentnine.springsessionless.adapter.web.dto.RestMessageDto;
import app.planentnine.springsessionless.adapter.web.dto.mapper.RestMessageDtoMapper;
import app.planentnine.springsessionless.application.port.incoming.VerifyMessageUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

class CoolnessResponse {
  private final Boolean doubleCool;

  public CoolnessResponse(Boolean isCool) {
    this.doubleCool = isCool;
  }
}

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
    
    @PostMapping("/cool-stuff")
    public ResponseEntity<Object> verifyMessage(@RequestBody RestMessageDto messageDto){
System.out.println("down the rabbit hole we go.");
System.out.println("Actually maybe not");
        boolean verified = verifyMessageUseCase.verifyMessage(messageDtoMapper.map(messageDto));
            Map<String, Boolean> responseMap = new HashMap<>();
        if (verified) {
            responseMap.put("doubleCool", true);
            return ResponseEntity.accepted().body(responseMap);
        } else {
            responseMap.put("doubleCool", false);
            return ResponseEntity.accepted().body(responseMap);
        }
    }
}
