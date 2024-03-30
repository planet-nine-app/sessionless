package app.planentnine.springsessionless.adapter.web;

import app.planentnine.springsessionless.adapter.web.dto.RestUserDto;
import app.planentnine.springsessionless.adapter.web.dto.mapper.RestUserDtoMapper;
import app.planentnine.springsessionless.application.domain.User;
import app.planentnine.springsessionless.application.port.incoming.CreateUserUseCase;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;


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
    public ResponseEntity<Object> createUser(@RequestBody RestUserDto restUserDto) {
        User user = createUserUseCase.createUser(userDtoMapper.map(restUserDto));

        if (isValidPublicKey(user.publicKey())) {
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("userUuid", user.userUuid().toString());
            return ResponseEntity.accepted().body(responseMap);
        } else {
            return ResponseEntity.badRequest().body("Invalid request parameters provided");
        }
    }
    
    private boolean isValidPublicKey(String publicKey) {
        try {
            BigInteger publicKeyFormatted = new BigInteger(publicKey, 16);
            ECNamedCurveParameterSpec ecNamedCurveParameterSpec =
                    ECNamedCurveTable.getParameterSpec("secp256k1");
            
            org.bouncycastle.math.ec.ECPoint publicKeyPoint =
                    ecNamedCurveParameterSpec.getCurve().decodePoint(publicKeyFormatted.toByteArray());
            
            if (publicKeyPoint != null && publicKeyPoint.isValid()) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Invalid Key format"); //TODO throw to log
        }
        return false;
    }
    
}
