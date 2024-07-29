package app.planentnine.springsessionless.application.validation;

import app.planentnine.springsessionless.application.domain.exception.ValidationException;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CreateUserValidator {
   
    public Optional<ValidationException> validate(String publicKey) {
        List<String> errors = new ArrayList<>();
        
        try {
            BigInteger publicKeyFormatted = new BigInteger(publicKey, 16);
            ECNamedCurveParameterSpec ecNamedCurveParameterSpec =
                    ECNamedCurveTable.getParameterSpec("secp256k1");
            
            org.bouncycastle.math.ec.ECPoint publicKeyPoint =
                    ecNamedCurveParameterSpec.getCurve().decodePoint(publicKeyFormatted.toByteArray());
            
            if (publicKeyPoint == null || !publicKeyPoint.isValid()) {
                errors.add("Invalid key format");
            }
        } catch (Exception e) {
            errors.add("Invalid Key format");
        }
        
        if (errors.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new ValidationException(errors));
        }
    }
}
