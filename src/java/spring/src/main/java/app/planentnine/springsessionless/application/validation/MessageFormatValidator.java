package app.planentnine.springsessionless.application.validation;

import app.planentnine.springsessionless.application.domain.Message;
import app.planentnine.springsessionless.application.domain.exception.ValidationException;
import com.allthing.libs.sessionless.Sessionless;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MessageFormatValidator {
    
    @Value("${message.valid-window-in-seconds}")
    private long VALID_WINDOW_IN_SECONDS;
    
    public Optional<ValidationException> validate(String publicKey, Message message){
        List<String> errors = new ArrayList<>();
        
        if (message.timestamp() < Instant.now().toEpochMilli() - VALID_WINDOW_IN_SECONDS * 1000) {
            
            errors.add("Timestamp only valid within " + VALID_WINDOW_IN_SECONDS + " seconds");
            errors.add("Now: " + LocalDateTime.now());
            errors.add("Provided: " + message.timestamp());
        }
        
        if (!Sessionless.verifySignature(publicKey, message.signature(), message.payload())){
            errors.add("Signature verification failed");
        }
        
        if (errors.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(new ValidationException(errors));
        }
        
    }
}
