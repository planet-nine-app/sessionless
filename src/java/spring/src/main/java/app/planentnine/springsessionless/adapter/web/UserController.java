package app.planentnine.springsessionless.adapter.web;

import app.planentnine.springsessionless.adapter.web.dto.RestCreateUserDto;
import app.planentnine.springsessionless.adapter.web.dto.RestUserDto;
import app.planentnine.springsessionless.adapter.web.dto.mapper.RestMessageDtoMapper;
import app.planentnine.springsessionless.adapter.web.dto.mapper.RestUserDtoMapper;
import app.planentnine.springsessionless.application.domain.User;
import app.planentnine.springsessionless.application.domain.exception.ValidationException;
import app.planentnine.springsessionless.application.port.incoming.CreateUserUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;


@Controller
public class UserController {
    
    private final CreateUserUseCase createUserUseCase;
    private final RestUserDtoMapper userDtoMapper;
    private final RestMessageDtoMapper messageDtoMapper;
    
    @Autowired
    public UserController(CreateUserUseCase createUserUseCase,
                          RestUserDtoMapper restUserDtoMapper,
                          RestMessageDtoMapper restMessageDtoMapper) {
        this.createUserUseCase = createUserUseCase;
        this.userDtoMapper = restUserDtoMapper;
        this.messageDtoMapper = restMessageDtoMapper;
    }
    
    @PostMapping("/register")
    public ResponseEntity<Object> createUser(@RequestBody RestCreateUserDto createUserDto) {
        try {
            RestUserDto userDto = RestUserDto.builder()
                    .uuid(null)
                    .pubKey(createUserDto.pubKey())
                    .build();
            
            
            User user = createUserUseCase.createUser(messageDtoMapper.maptToMessage(createUserDto), userDtoMapper.map(userDto));
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("uuid", user.userUUID().toString());
            return ResponseEntity.accepted().body(responseMap);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getErrors());
        }
    }
    
}
