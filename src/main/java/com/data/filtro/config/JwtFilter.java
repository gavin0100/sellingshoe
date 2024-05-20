package com.data.filtro.config;

import com.data.filtro.authentication.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final UserDetailsService userDetailsService;
    private  final JwtService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
//        System.out.println(request.getRequestURL());
        if (path.startsWith("/css/") || path.startsWith("/javascript/") || path.startsWith("/image/") || path.startsWith("/login") || path.startsWith("/img/") || path.startsWith("/access-denied")) {
            // Nếu đúng là tài nguyên tĩnh, cho phép yêu cầu đi qua mà không xử lý thêm
            filterChain.doFilter(request, response);
            return;
        }

//        System.out.println(request.getHeader("Host"));
//        System.out.println("doFilterInternal trong jwtFilter");

//        final String authHeader = request.getHeader(AUTHORIZATION);
        String jwt = "";
        String accountName = "";
        if (request.getCookies() != null){
            for (int i =0; i < request.getCookies().length; i++){
//                System.out.println(request.getCookies()[i].getName() + " " + request.getCookies()[i].getValue()  );
                if (request.getCookies()[i].getName().equals("token")){
                    jwt = request.getCookies()[i].getValue();
                    accountName = jwtService.extractUsername(jwt);
                    break;
                }
            }
        }
//        System.out.println(authHeader);
        if(jwt.equals("")){
//            logger.error("JWT Token does not begin with Bearer String");
            filterChain.doFilter(request, response);
            return;
        }


        if(accountName!=null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(accountName);

            if(jwtService.isValidToken(jwt, userDetails)){
//                System.out.println(userDetails.getUsername());
//                System.out.println(userDetails.getPassword());
//                System.out.println(userDetails.getAuthorities().toString());
//                System.out.println("===");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
