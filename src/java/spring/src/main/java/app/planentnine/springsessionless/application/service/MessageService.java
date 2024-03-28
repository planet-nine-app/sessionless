package app.planentnine.springsessionless.application.service;

import app.planentnine.springsessionless.application.domain.Message;
import app.planentnine.springsessionless.application.port.incoming.VerifyMessageUseCase;
import app.planentnine.springsessionless.application.port.outgoing.VerifyMessagePort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
//TODO add and implement verification protocols
public class MessageService implements VerifyMessageUseCase {
    private final VerifyMessagePort verifyMessagePort;
    
    @Autowired
    public MessageService(VerifyMessagePort verifyMessagePort) {
        this.verifyMessagePort = verifyMessagePort;
    }
    
    @Override
    public boolean verifyMessage(Message message) {
        return verifyMessagePort.verifyMessage(message);
    }
}
