package com.data.filtro.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig{
    private final AuthenticationProvider authenticationProvider;
    private final JwtFilter jwtFilter;
//    private final FilterExceptionHandler filterExceptionHandler;

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


//    private final LogoutHandler logoutHandler;
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
                                        "/session"

                                ).permitAll()
                                .requestMatchers("/css/**", "/js/**", "/image/**", "/javascript/**", "/access-denied", "/img/**", "/product/img/**").permitAll()
                                .anyRequest().authenticated()
                                .and()
                                .exceptionHandling()
                                .accessDeniedPage("/");  // chuyen huong den trang access-denied khi cố gắng truy cập vào một tài nguyên mà họ không được phép khi chưa xác thực
//                                .and()
//                                .logout() // neu da dang ky ngoai nay thi may cai viet trong controler logout khong duoc thuc hien
//                                .invalidateHttpSession(true)
//                                .deleteCookies("JSESSIONID")
//                                .deleteCookies("fourleavesshoestoken")
//                                .clearAuthentication(true)
//                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                                .logoutSuccessUrl("/login")
//                                .and()
//                                .oauth2Login(oauth2 -> {
//                                    oauth2.redirectionEndpoint()
//                                            .baseUri("/login/oauth2/code/*");
//                                    oauth2.defaultSuccessUrl("/user_google_hihi", true);
//                                });
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
                                .accessDeniedPage("/")
                                .authenticationEntryPoint(accessErrorHandler());
                    } catch (Exception e) {
                        e.printStackTrace(); // Or use a logging framework
                    }
                })

                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();

        // session nếu không bị khóa thì spring security sẽ chuyển sang sài session mặc định để lưu thông tin,
        // nhưng quyền jwt thì lại được lưu trong cookie, điều này lại gây sự cố
        // nhưng mà nếu session bị khóa, thì hệ thống phân quyền hoạt động bình thường nhưng oauth lại trả về null user
        // vì spring security không thể tạo được session tạm thời để lưu thông tin user
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(ahr -> {
//                    try {
//                        ahr
//                                .anyRequest().authenticated()
//                                .and()
//                                .oauth2Login(oauth2 -> oauth2.redirectionEndpoint(redirection -> redirection.baseUri("/login/oauth2/code/google")).defaultSuccessUrl("/user_google_hihi", true));
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                })
//                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authenticationProvider(authenticationProvider)
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
    }
}
