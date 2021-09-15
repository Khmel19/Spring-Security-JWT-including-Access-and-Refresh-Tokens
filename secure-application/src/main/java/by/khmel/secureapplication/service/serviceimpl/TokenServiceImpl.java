package by.khmel.secureapplication.service.serviceimpl;

import by.khmel.secureapplication.domain.Role;
import by.khmel.secureapplication.security.securityutil.AlgorithmGenerator;
import by.khmel.secureapplication.service.TokenService;
import by.khmel.secureapplication.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TokenServiceImpl implements TokenService {
    private final AlgorithmGenerator algorithmGenerator;
    private final UserService userService;

    @Autowired
    public TokenServiceImpl(AlgorithmGenerator algorithmGenerator, UserService userService) {
        this.algorithmGenerator = algorithmGenerator;
        this.userService = userService;
    }

    @Override
    public String createAccessToken(String username, Collection<GrantedAuthority> authorities, HttpServletRequest request) {
        Algorithm algorithm = algorithmGenerator.getAlgorithm();
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", authorities
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .sign(algorithm);
    }

    @Override
    public String createRefreshToken(User user, HttpServletRequest request) {
        Algorithm algorithm = algorithmGenerator.getAlgorithm();
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
    }

    @Override
    public void updateAccessToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = algorithmGenerator.getAlgorithm();
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                by.khmel.secureapplication.domain.User user = userService.getUser(username);
                Collection<GrantedAuthority> authorities = user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .map(roleName -> (GrantedAuthority) () -> roleName).collect(Collectors.toList());
                String access_token = createAccessToken(username, authorities, request);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception exception) {

                response.setHeader("error", exception.getMessage());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("refresh_token is missing");
        }
    }

}
