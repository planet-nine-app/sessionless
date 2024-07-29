package app.planentnine.springsessionless.application.service;

import app.planentnine.springsessionless.application.domain.Message;
import app.planentnine.springsessionless.application.domain.User;
import app.planentnine.springsessionless.application.domain.exception.ValidationException;
import app.planentnine.springsessionless.application.port.incoming.CreateUserUseCase;
import app.planentnine.springsessionless.application.port.incoming.VerifyMessageUseCase;
import app.planentnine.springsessionless.application.port.outgoing.CreateUserIfNotExistsPort;
import app.planentnine.springsessionless.application.port.outgoing.LoadUserByUserUuidPort;
import app.planentnine.springsessionless.application.validation.CreateUserValidator;
import app.planentnine.springsessionless.application.validation.MessageFormatValidator;
import com.allthing.libs.sessionless.Sessionless;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements CreateUserUseCase, VerifyMessageUseCase {
    private final CreateUserIfNotExistsPort createUserIfNotExistsPort;
    private final LoadUserByUserUuidPort loadUserByUserUuidPort;
    private final CreateUserValidator createUserValidator;
    private final MessageFormatValidator messageFormatValidator;
    
    
    @Autowired
    public UserService(
            CreateUserIfNotExistsPort createUserIfNotExistsPort,
            LoadUserByUserUuidPort loadUserByUserUuidPort,
            CreateUserValidator createUserValidator,
            MessageFormatValidator messageFormatValidator){
        this.createUserIfNotExistsPort = createUserIfNotExistsPort;
        this.loadUserByUserUuidPort = loadUserByUserUuidPort;
        this.createUserValidator = createUserValidator;
        this.messageFormatValidator = messageFormatValidator;
    }
    
    @Override
    public User createUser(Message message, User user) {
        Optional<ValidationException> validationResult = createUserValidator.validate(user.publicKey());
        if (validationResult.isPresent()) {
            throw validationResult.get();
        }
        
        if (verifyMessagePayload(message, user)) {
            User createdUser = new User(
                    UUID.randomUUID(),
                    Sessionless.generateUuid(),
                    user.publicKey()
            );
            
            return createUserIfNotExistsPort.createUserIfNotExists(createdUser);
        } else {
            return null;
        }
    }
    
    @Override
    public boolean verifyMessage(Message message) {
        return loadUserKeyAndValidateMessage(message);
    }
    
    private boolean loadUserKeyAndValidateMessage(Message message) {
        User user = loadUserByUserUuidPort.loadByUserUuid(message.userUUID())
                .orElseThrow(() -> new RuntimeException("User with id: " + message.userUUID() + "could not be found from message"));
        
        return verifyMessagePayload(message, user);
    }
    
    private boolean verifyMessagePayload(Message message, User user) {
        
        Optional<ValidationException> validationResult =
                messageFormatValidator.validate(user.publicKey(), message);
        if (validationResult.isPresent()) {
            throw validationResult.get();
        } else {
            return true;
        }
    }
}
