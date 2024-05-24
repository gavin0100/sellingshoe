package com.data.filtro.config;

import com.data.filtro.handler.FilterExceptionHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig{
    private final AuthenticationProvider authenticationProvider;
    private final JwtFilter jwtFilter;
    private final FilterExceptionHandler filterExceptionHandler;

    private final JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public CustomAccessDeniedHandler accessDeniedHandler(){
        return new CustomAccessDeniedHandler();
    }

//    private final LogoutHandler logoutHandler;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
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
                                        "/api/**"

                                        ).permitAll()
                                .requestMatchers("/css/**", "/js/**", "/image/**", "/javascript/**").permitAll()
                                .anyRequest().authenticated()
                                .and()
                                .exceptionHandling()
                                .authenticationEntryPoint(accessDeniedHandler())  // chuyen huong den trang access-denied khi cố gắng truy cập vào một tài nguyên mà họ không được phép khi chưa xác thực
//                                .anyRequest().authenticated()
//                                .and()
//                                .exceptionHandling()
//                                .accessDeniedHandler(accessDeniedHandler())
//                                .accessDeniedPage("/access-denied")
//                                .authenticationEntryPoint(accessDeniedHandler())
                                .and()
//                                .formLogin(form ->  form.failureForwardUrl("/login/login-failure"))
//                                .loginPage("/login")
//                                .loginProcessingUrl("/sign-in")
//                                .defaultSuccessUrl("/", true)
//                                .permitAll()
//                                .and()
                                .logout() // neu da dang ky ngoai nay thi may cai viet trong controler logout khong duoc thuc hien
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID")
                                .deleteCookies("token")
                                .clearAuthentication(true)
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .logoutSuccessUrl("/login");
//                                .and()
//                                .exceptionHandling()
//                                .accessDeniedPage("/access-denied");
//                                .and()
//                                .oauth2Login()
//                                .loginPage("/login")
//                                .defaultSuccessUrl("/oauth2user", true);
                    } catch (Exception e) {
                        System.out.println("Lỗi truy cập xử lý html");
                        throw new RuntimeException(e);
                    }
                })
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
//                .exceptionHandling(eh-> eh.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//                .exceptionHandling()
//                .accessDeniedPage("/access-denied");
//                .addFilterBefore(filterExceptionHandler, JwtFilter.class);


        return http.build();
    }
}
