package com.data.filtro.service;


import com.data.filtro.authentication.JwtService;
import com.data.filtro.exception.AuthenticationAccountException;
import com.data.filtro.model.AuthenticateResponse;
import com.data.filtro.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager manager;


    public AuthenticateResponse authenticate(String accountName, String password, HttpSession session){
        try{
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    accountName,
                    password
            );
//            System.out.println("token trong authenticationService: " + token);
//            System.out.println("token trong authenticationService: " + token.getCredentials());
            manager.authenticate(token);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
//            System.out.println(SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        }catch (AuthenticationException e){
//            System.out.println(e.getMessage());
            throw new AuthenticationAccountException("Invalid username/password supplied: " + e.getMessage());
        }
        User user = userService.getUserByAccountName(accountName);
        String token = jwtService.generateToken(user);
        AuthenticateResponse authenticateResponse = new AuthenticateResponse();
        authenticateResponse.setAccessToken(token);
        authenticateResponse.setUser(user);
        return authenticateResponse;
    }


}
