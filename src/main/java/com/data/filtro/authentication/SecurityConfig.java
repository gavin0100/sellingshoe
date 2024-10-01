package com.data.filtro.authentication;

import com.data.filtro.authentication.exception.CustomAccessDeniedHandler;
import com.data.filtro.authentication.exception.CustomAuthenticationFailureHandler;
import com.data.filtro.authentication.exception.CustomAuthenticationSuccessHandler;
import com.data.filtro.authentication.exception.CustomErrorHandler;
import com.data.filtro.authentication.oauth.CustomOAuth2UserService;
import com.data.filtro.authentication.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import com.data.filtro.authentication.oauth.OAuth2AuthenticationFailureHandler;
import com.data.filtro.authentication.oauth.OAuth2AuthenticationSuccessHandler;
import com.data.filtro.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;
    private final JwtFilter jwtFilter;

    private final AuthenticationManager authenticationManager;

    private final UserService userService;
    private final CustomOAuth2UserService oauthUserService;

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;



    public SecurityConfig(AuthenticationProvider authenticationProvider,
                          JwtFilter jwtFilter,
                          @Lazy AuthenticationManager authenticationManager,
                          UserService userService, CustomOAuth2UserService oauthUserService, OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler, OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler, HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository){
        this.authenticationManager = authenticationManager;
        this.jwtFilter = jwtFilter;
        this.authenticationProvider = authenticationProvider;
        this.userService = userService;
        this.oauthUserService = oauthUserService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }
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
                                        "/favicon.ico",
                                        "/search",
                                        "/404",
                                        "/rest/api/**",
                                        "/oauth/**",
                                        "/auth/**",
                                        "/error_problem_detail",
                                        "/v3/**",
                                        "/swagger-ui/**"

                                ).permitAll()
                                .requestMatchers("/css/**", "/js/**", "/image/**", "/javascript/**", "/access-denied", "/img/**", "/product/img/**").permitAll()
                                .anyRequest().authenticated();
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
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login()
                .loginPage("/login")
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization")
                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .userInfoEndpoint()
                .userService(oauthUserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler);
        return http.build();
    }

}
