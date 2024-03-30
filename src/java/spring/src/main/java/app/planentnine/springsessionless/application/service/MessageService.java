package app.planentnine.springsessionless.application.service;

import app.planentnine.springsessionless.application.util.Sessionless;
import app.planentnine.springsessionless.application.domain.Message;
import app.planentnine.springsessionless.application.domain.User;
import app.planentnine.springsessionless.application.port.incoming.VerifyMessageUseCase;
import app.planentnine.springsessionless.application.port.outgoing.LoadUserByUserUuidPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService implements VerifyMessageUseCase {
    private final LoadUserByUserUuidPort loadUserByUserUuidPort;
    
    @Autowired
    public MessageService(LoadUserByUserUuidPort loadUserByUserUuidPort) {
        this.loadUserByUserUuidPort = loadUserByUserUuidPort;
    }
    
    @Override
    public boolean verifyMessage(Message message) {
        User user = loadUserByUserUuidPort.loadByUserUuid(message.userUuid())
                .orElseThrow(() -> new RuntimeException("User with id: " + message.userUuid() + "could not be found from message"));
        String publicKey = user.publicKey();
        String[] signature = message.signature();
        String messageContent = message.content();
        return Sessionless.verify(publicKey, signature, messageContent);
    }
}
