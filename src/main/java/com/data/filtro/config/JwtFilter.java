package com.data.filtro.config;

import com.data.filtro.authentication.JwtService;
import com.data.filtro.model.AuthenticateResponse;
import com.data.filtro.model.User;
import com.data.filtro.service.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
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
    private int temp = 0;
    private boolean tempbool = false;

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

        if (!path.equals("/logout_to_login/fromJwtEmptyOrNullException")
                && !path.equals("/logout")
                && (jwt.equals("") || jwt== null)
                && request.getSession().getAttribute("user") != null){
            throw new JwtNullOrEmptyException("token khong ton tai", null, false, false);
        }

        String accountName = "";
        if (!jwt.equals("") && jwt != null && request.getSession().getAttribute("user") == null && (
                path.startsWith("/product/") ||
                        path.startsWith("/cart") ||
                        path.startsWith("/category/") ||
                        path.startsWith("/register") ||
                        path.startsWith("/login") ||
                        path.startsWith("/forgot-password") ||
                        path.startsWith("/contact") ||
                        path.startsWith("/user") ||
                        path.startsWith("/admin") ||
                        path.equals("/") ||
                        path.equals("") ||
                        path.equals("/session"))){
            try{
                accountName = jwtService.extractUsername(jwt);
                if (jwtService.isTokenExpired(jwt)){
                    throw new MyServletException("Token is Expired ", null, false, false);
                }
                UserDetails userDetails = userDetailsService.loadUserByUsername(accountName);
                if(jwtService.isValidToken(jwt, userDetails)){
                    request.getSession().setAttribute("user", (User)userDetails);
                }
            } catch (Exception ex){
                throw new MyServletException("Can't get accountName from JWT or Token is not valid", null, false, false);
            }
        }

        if (!jwt.equals("") && jwt != null && accountName != null && request.getSession().getAttribute("admin") == null && (
                path.startsWith("/admin"))){
            try{
                accountName = jwtService.extractUsername(jwt);
                if (jwtService.isTokenExpired(jwt)){
                    throw new MyServletException("Token is Expired ", null, false, false);
                }
                UserDetails userDetails = userDetailsService.loadUserByUsername(accountName);
                if(jwtService.isValidToken(jwt, userDetails)){
                    request.getSession().setAttribute("admin", (User)userDetails);
                }
            } catch (Exception ex){
                throw new MyServletException("Can't get accountName from JWT or Token is not valid", null, false, false);
            }
        }



        if (path.startsWith("/css/") ||
                path.startsWith("/javascript/") ||
                path.startsWith("/image/") ||
                path.startsWith("/login") ||
                path.startsWith("/img/") ||
                path.startsWith("/access-denied") ||
                path.startsWith("/product/img") ||
                path.startsWith("/product/") ||
                path.startsWith("/cart") ||
                path.startsWith("/category/") ||
                path.startsWith("/register") ||
                path.startsWith("/api/") ||
                path.startsWith("/test") ||
                path.startsWith("/app-minio") ||
                path.startsWith("/contact") ||
                path.startsWith("/admin/login") ||
                path.startsWith("/forgot-password") ||
                path.startsWith("/favicon.ico") ||
                path.equals("/") ||
                path.startsWith("/logout_to_login") ||
                path.startsWith("/user/billing/reset_login")){
            if (path.startsWith("/product/") ||
                    path.startsWith("/cart") ||
                    path.startsWith("/category/") ||
                    path.startsWith("/register") ||
                    path.startsWith("/contact") ||
                    path.startsWith("/admin/login") ||
                    path.startsWith("/forgot-password") ||
                    path.equals("/") ||
                    path.startsWith("/login")
            ){
                if (!jwt.equals("") && jwt != null && accountName.equals("") && (
                        request.getSession().getAttribute("user") != null ||
                                request.getSession().getAttribute("admin") != null)){
                    try{
                        accountName = jwtService.extractUsername(jwt);
                        if (jwtService.isTokenExpired(jwt)){
                            throw new MyServletException("Token is Expired ", null, false, false);
                        }
                    } catch (Exception ex){
                        throw new MyServletException("Can't get accountName from JWT", null, false, false);
                    }
                }
            }
            filterChain.doFilter(request, response);
            return;
        }

        if (jwt.equals("") || jwt== null){
            throw new AccessDeniedException("token khong tai");
        }

        if (accountName.equals("")){
            try{
                accountName = jwtService.extractUsername(jwt);
                if (jwtService.isTokenExpired(jwt)){
                    throw new MyServletException("Token is Expired ", null, false, false);
                }
            } catch (Exception ex){
                throw new MyServletException("Can't get accountName from JWT", null, false, false);
            }
        }

        if(accountName!=null && SecurityContextHolder.getContext().getAuthentication() == null){
            if (jwtService.isTokenExpired(jwt)){
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
        filterChain.doFilter(request, response);
    }
}
