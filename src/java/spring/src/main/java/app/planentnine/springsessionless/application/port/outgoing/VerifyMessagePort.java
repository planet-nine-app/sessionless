package app.planentnine.springsessionless.application.port.outgoing;

import app.planentnine.springsessionless.application.domain.Message;

public interface VerifyMessagePort {
    boolean verifyMessage(Message message);
}
