package com.data.filtro.config;

import com.data.filtro.handler.FilterExceptionHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig{
    private final AuthenticationProvider authenticationProvider;
    private final JwtFilter jwtFilter;

    @Autowired
    @Lazy
    private  AuthenticationManager authenticationManager;

    @Bean
    public CustomAccessDeniedHandler accessDeniedHandler(){
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public CustomErrorHandler accessErrorHandler(){
        return new CustomErrorHandler();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        CustomUsernamePasswordAuthenticationFilter customFilter = new CustomUsernamePasswordAuthenticationFilter(authenticationManager);
        customFilter.setAuthenticationSuccessHandler(new CustomAuthenticationSuccessHandler());
        customFilter.setAuthenticationFailureHandler(new CustomAuthenticationFailureHandler());
        CustomExceptionFilter customExceptionFilter = new CustomExceptionFilter();
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(ahr -> {
                    try {
                        ahr
                                .requestMatchers("/login/**",
                                        "/register",
                                        "/",
                                        "/admin/login",
                                        "/api/v1/momo/**",
                                        "/api/v1/vnpay/**",
                                        "/access-denied",
                                        "/oauth2user",
                                        "/forgot-password",
                                        "/category",
                                        "/contact",
                                        "/api/**",
                                        "/category/**",
                                        "/cart/**",
                                        "/product/**",
                                        "/user_google_hihi",
                                        "/otp/**",
                                        "/test/**",
                                        "/app-minio/**",
                                        "/logout_to_login/**",
                                        "/user/billing/reset_login",
                                        "/session",
                                        "/favicon.ico"

                                ).permitAll()
                                .requestMatchers("/css/**", "/js/**", "/image/**", "/javascript/**", "/access-denied", "/img/**", "/product/img/**").permitAll()
                                .anyRequest().authenticated()
                                .and()
                                .exceptionHandling()
                                .accessDeniedPage("/");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .formLogin().disable()
                .addFilter(customFilter)
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(customExceptionFilter, LogoutFilter.class)
                .logout(ahr -> {
                    ahr.invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                            .deleteCookies("fourleavesshoestoken")
                            .clearAuthentication(true)
                            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                            .logoutSuccessUrl("/login");
                })
                .exceptionHandling(ahr -> {
                    try {
                        ahr
                                .authenticationEntryPoint(accessErrorHandler());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })

                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
