package com.data.filtro.authentication;

import com.data.filtro.authentication.exception.MyServletException;
import com.data.filtro.service.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
//@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public JwtFilter(UserDetailsService userDetailsService, @Lazy JwtService jwtService, @Lazy AuthenticationService authenticationService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String jwt = "";
        if (request.getCookies() != null){
            for (int i =0; i < request.getCookies().length; i++){
                if (request.getCookies()[i].getName().equals("fourleavesshoestoken")){
                    jwt = request.getCookies()[i].getValue();
                    break;
                }
            }
        }
        String accountName = "";
        if (path.startsWith("/css/") ||
                path.startsWith("/javascript/") ||
                path.startsWith("/image/") ||
                path.equals("/login") ||
                path.startsWith("/img/") ||
                path.startsWith("/access-denied") ||
                path.startsWith("/product/img") ||
                path.startsWith("/product/") ||
                path.startsWith("/cart") ||
                path.startsWith("/category/") ||
                path.equals("/register") ||
                path.startsWith("/api/") ||
                path.startsWith("/test") ||
                path.startsWith("/app-minio") ||
                path.equals("/contact") ||
                path.startsWith("/admin/login") ||
                path.equals("/forgot-password") ||
                path.startsWith("/favicon.ico") ||
                path.equals("/") ||
                path.startsWith("/logout_to_login") ||
                path.startsWith("/user/billing/reset_login") ||
                path.startsWith("/search") ||
                path.equals("/404")){
            if (path.startsWith("/product/") ||
                    path.startsWith("/cart") ||
                    path.startsWith("/category/") ||
                    path.startsWith("/register") ||
                    path.startsWith("/contact") ||
                    path.startsWith("/admin/login") ||
                    path.startsWith("/forgot-password") ||
                    path.equals("/") ||
                    path.startsWith("/login") ||
                    path.startsWith("/search")
            ){

                if (!jwt.equals("") && jwt != null && accountName.equals("")){
                    try{
                        accountName = jwtService.extractUsername(jwt);
                        setSecurityContextHolder(jwt, accountName, request);
                    } catch (Exception ex){
                        throw new MyServletException("Can't get accountName from JWT", null, false, false);
                    }
                }
            }
            filterChain.doFilter(request, response);
            return;
        }

        if (jwt.equals("") || jwt== null){
            throw new AccessDeniedException("token khong ton tai");
        }

        if (accountName.equals("")){
            try{
                accountName = jwtService.extractUsername(jwt);
                if (isTokenExpired(jwt)){
                    throw new MyServletException("Token is Expired ", null, false, false);
                }
            } catch (Exception ex){
                throw new MyServletException("Can't get accountName from JWT", null, false, false);
            }
        }

        if(accountName!=null && SecurityContextHolder.getContext().getAuthentication() == null){
            setSecurityContextHolder(jwt, accountName, request);
        }
        filterChain.doFilter(request, response);
    }

    private boolean isTokenExpired (String jwt){
        return jwtService.isTokenExpired(jwt);
    }

    private void setSecurityContextHolder(String jwt, String accountName, HttpServletRequest request) throws MyServletException {
        if (isTokenExpired(jwt)){
            throw new MyServletException("Token is Expired ", null, false, false);
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(accountName);
        if(jwtService.isValidToken(jwt, userDetails)){
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
            throw new MyServletException("Token is not valid", null, false, false);
        }
    }
}
