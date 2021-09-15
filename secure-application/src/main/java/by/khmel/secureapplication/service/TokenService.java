package by.khmel.secureapplication.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

public interface TokenService {
    String createAccessToken(String username, Collection<GrantedAuthority> authorities, HttpServletRequest request);

    String createRefreshToken(User user, HttpServletRequest request);

    void updateAccessToken(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
