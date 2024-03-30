package app.planentnine.springsessionless.adapter.web;

import app.planentnine.springsessionless.adapter.web.dto.RestUserDto;
import app.planentnine.springsessionless.adapter.web.dto.mapper.RestUserDtoMapper;
import app.planentnine.springsessionless.application.domain.User;
import app.planentnine.springsessionless.application.port.incoming.CreateUserUseCase;
import app.planentnine.springsessionless.application.util.Sessionless;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/api/user")
public class UserController {
    
    private final CreateUserUseCase createUserUseCase;
    private final RestUserDtoMapper userDtoMapper;
    
    @Autowired
    public UserController(CreateUserUseCase createUserUseCase,
                          RestUserDtoMapper restUserDtoMapper) {
        this.createUserUseCase = createUserUseCase;
        this.userDtoMapper = restUserDtoMapper;
    }
    
    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody RestUserDto restUserDto) {
        User user = createUserUseCase.createUser(userDtoMapper.map(restUserDto));
        if (user == null) {
            return ResponseEntity.badRequest().body("Malformed key, user could not be created");
        } else {
            return ResponseEntity.accepted().body(user.userUuid().toString());
        }
    }
}
