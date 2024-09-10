package com.data.filtro.service;


import com.data.filtro.authentication.JwtService;
import com.data.filtro.exception.AuthenticationAccountException;
import com.data.filtro.model.AuthenticateResponse;
import com.data.filtro.model.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
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
            System.out.println("token trong authenticationService: " + token);
            System.out.println("token Credentials trong authenticationService: " + token.getCredentials());
            System.out.println("token principal trong authenticationService: " + token.getPrincipal());

//            token trong authenticationService: UsernamePasswordAuthenticationToken [Principal=doananh0100, Credentials=[PROTECTED], Authenticated=false, Details=null, Granted Authorities=[]]
//            token Credentials trong authenticationService: Duc2112002@
//            token principal trong authenticationService: doananh0100

            Authentication authentication = manager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
//            System.out.println();
//            System.out.println("token trong authenticationService: " + token);
//            System.out.println("token Credentials trong authenticationService: " + token.getCredentials());
//            System.out.println("token principal trong authenticationService: " + token.getPrincipal());
//            System.out.println();
            System.out.println("SecurityContextHolder chứa thông tin: " +
                    SecurityContextHolder.getContext().getAuthentication().getCredentials() +
                    SecurityContextHolder.getContext().getAuthentication().getPrincipal() +
                    SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        }catch (AuthenticationException e){
//            System.out.println(e.getMessage());
            throw new AuthenticationAccountException("Tên tài khoản, mật khẩu không đúng");
        }
        User user = userService.getUserByAccountName(accountName);
        String token = jwtService.generateToken(user);
        AuthenticateResponse authenticateResponse = new AuthenticateResponse();
        authenticateResponse.setAccessToken(token);
        authenticateResponse.setUser(user);
        return authenticateResponse;
    }

    public AuthenticateResponse authenticate(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        AuthenticateResponse authenticateResponse = new AuthenticateResponse();
        authenticateResponse.setAccessToken(token);
        authenticateResponse.setUser(user);
        return authenticateResponse;
    }

    public AuthenticateResponse authenticateWithOnlyAccountName(String accountName){
        User user = userService.getUserByAccountName(accountName);
        String token = jwtService.generateToken(user);
        AuthenticateResponse authenticateResponse = new AuthenticateResponse();
        authenticateResponse.setAccessToken(token);
        authenticateResponse.setUser(user);
        return authenticateResponse;
    }

}
