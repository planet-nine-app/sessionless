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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;


@Controller
public class UserController {
    
    private final CreateUserUseCase createUserUseCase;
    private final RestUserDtoMapper userDtoMapper;
    
    @Autowired
    public UserController(CreateUserUseCase createUserUseCase,
                          RestUserDtoMapper restUserDtoMapper) {
        this.createUserUseCase = createUserUseCase;
        this.userDtoMapper = restUserDtoMapper;
    }
    
    @PostMapping("/register")
    public ResponseEntity<Object> createUser(@RequestBody RestUserDto restUserDto) {
System.out.println("about to check pub key validity");
System.out.println(restUserDto.pubKey());
        if (isValidPublicKey(restUserDto.pubKey()) && ) {
            User user = createUserUseCase.createUser(userDtoMapper.map(restUserDto));
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("uuid", user.userUuid().toString());
            return ResponseEntity.accepted().body(responseMap);
        } else {
            return ResponseEntity.badRequest().body("Invalid request parameters provided");
        }
    }
    
    private boolean isValidPublicKey(String publicKey) {
System.out.println("Does this even get called?");
        try {
System.out.println("oof");
System.out.println(publicKey);
            BigInteger publicKeyFormatted = new BigInteger(publicKey, 16);
System.out.println("foo");
            ECNamedCurveParameterSpec ecNamedCurveParameterSpec =
                    ECNamedCurveTable.getParameterSpec("secp256k1");
            
System.out.println("bar");
            org.bouncycastle.math.ec.ECPoint publicKeyPoint =
                    ecNamedCurveParameterSpec.getCurve().decodePoint(publicKeyFormatted.toByteArray());
            
System.out.println("baz");
            if (publicKeyPoint != null && publicKeyPoint.isValid()) {
                return true;
            }
        } catch (Exception e) {
            //System.out.println("Invalid Key format"); //TODO throw to log
            System.out.println("gogogogogogo");
        }
        return false;
    }
    
}
