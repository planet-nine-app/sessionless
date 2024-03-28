package app.planentnine.springsessionless.application.port.incoming;

import app.planentnine.springsessionless.application.domain.Message;

public interface VerifyMessageUseCase {
    boolean verifyMessage(Message message);
}
