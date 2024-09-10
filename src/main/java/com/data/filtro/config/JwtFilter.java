package com.data.filtro.config;

import com.data.filtro.authentication.JwtService;
import com.data.filtro.model.User;
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
        // TH1: xử lý cho trang home, khi đã đăng nhập xong nhưng lại mất token,
        // load lại trang home nhưng dữ liệu trong session ở controller vẫn chưa bị xóa
        // TH2: nếu token mất mà session vẫn còn, thì user bị trả về đây
        // trong lần chuyển tiếp và xóa đi session, ng dùng cần truy cập lại url lần 2 mới vào được trang không cần quyền
        if (!path.equals("/logout_to_login/fromJwtEmptyOrNullException")
                && !path.equals("/logout")
                && (jwt.equals("") || jwt== null)
                && request.getSession().getAttribute("user") != null){
            throw new JwtNullOrEmptyException("token khong ton tai", null, false, false);
        }

        // đôi khi token tồn tại nhưng chưa xác thực mà ng dùng muốn truy cập các trang không cần xác thực và cần đăng nhập la
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
                System.out.println("đôi khi token tồn tại nhưng chưa xác thực mà ng dùng muốn truy cập các trang không cần xác thực và cần đăng nhập lại");
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
                System.out.println("đôi khi token tồn tại nhưng chưa xác thực mà ng dùng muốn truy cập các trang không cần xác thực và cần đăng nhập lại");
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
            // Nếu đúng là tài nguyên tĩnh, cho phép yêu cầu đi qua mà không xử lý thêm
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
                // token bị đổi
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
        System.out.println(path);
        // khi respone gửi momo, vnpay về thông qua ipn thì token fourleavesshoestoken của mình đã bị xóa,
        // tuy nhiên các yêu cầu khi nhận respone thông qua ipn vẫn làm đủ. nhưng khi trả về trang user-billing thì không có token,
        // nên giờ phải thêm lại
//        System.out.println("SecurityContextHolder chứa thông tin: " +
//                SecurityContextHolder.getContext().getAuthentication());

//        if (path.startsWith("/user/billing") && SecurityContextHolder.getContext().getAuthentication() != null){
//            AuthenticateResponse authenticateResponse = authenticationService.authenticate(SecurityContextHolder.getContext().getAuthentication());
//            Cookie cookie = new Cookie("fourleavesshoestoken", authenticateResponse.getAccessToken());
//            cookie.setHttpOnly(true);
//            cookie.setPath("/"); // This makes the cookie valid for all routes on your domain
//            response.addCookie(cookie);
//            response.sendRedirect("/user/billing");
//            filterChain.doFilter(request, response);
//            return;
//        }
//        System.out.println(path);

        // chưa có token thì xóa hết dữ liệu, quay lại trạng thái không đăng nhập
        if (jwt.equals("") || jwt== null){
            throw new AccessDeniedException("token khong tai");
            // cách nào cũng được
//            response.sendRedirect("/logout_to_login/fromJwtEmptyOrNullException");  // bí quá thì trả về response.sendRedirect("/");
//            return;
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
                // lưu những thông tin chi tiết vào SecurityContextHolder như thông tin địa chỉ IP, session ID của request gửi yêu cầu có token mà context holder không chứa thông tin
                SecurityContextHolder.getContext().setAuthentication(authToken);
//                System.out.println("SecurityContextHolder chứa thông tin: " + temp +
//                        SecurityContextHolder.getContext().getAuthentication().getCredentials() +
//                        SecurityContextHolder.getContext().getAuthentication().getPrincipal() +
//                        SecurityContextHolder.getContext().getAuthentication().getAuthorities() );

            } else {
                throw new MyServletException("Token is not valid", null, false, false);
            }
        }
//        System.out.println("SecurityContextHolder chứa thông tin: " +
//                        SecurityContextHolder.getContext().getAuthentication());
        // có token mà không có session


        temp = temp + 1;
        filterChain.doFilter(request, response);
    }
}
