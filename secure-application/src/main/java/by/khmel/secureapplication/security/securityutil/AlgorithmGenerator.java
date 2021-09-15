package by.khmel.secureapplication.security.securityutil;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AlgorithmGenerator {
    @Value("${jwt.secret}")
    private String secret;

    public Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret.getBytes());
    }
}
