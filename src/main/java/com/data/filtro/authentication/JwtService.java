package com.data.filtro.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long expirationTime;
    // 1 giờ = 3600000 mili giây

    public String extractUsername(String token){
//        System.out.println("extractClaim(token, Claims::getSubject): " + extractClaim(token, Claims::getSubject));
        return extractClaim(token, Claims::getSubject);
        // Khi bạn gọi Claims::getSubject, bạn đang yêu cầu lấy giá trị của thuộc tính “subject” từ các claims đã được giải mã từ token.
        // extractClaim(token, Claims::getSubject) trả về giá trị của claim sub: doananh0100
    }

    public Date extractExpiration(String token){
//        System.out.println("extractClaim(token, Claims::getExpiration): " + extractClaim(token, Claims::getExpiration));
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver){
        final Claims claims = extractAllClaims(token);
//        System.out.println("resolver.apply(claims): " + resolver.apply(claims));
        return resolver.apply(claims);
        // sử dụng Function getSubject trong lớp Claims trong thư viện JWT để lấy giá trị claim sub từ các claims
    }


    public Claims extractAllClaims(String token){
//        System.out.println("Claims: " + Jwts
//                .parserBuilder()
//                .setSigningKey(getSigninKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody());
      return Jwts
              .parserBuilder()
              .setSigningKey(getSigninKey())
              .build()
              .parseClaimsJws(token)
              .getBody();
    }
    // trả về danh sách các claims từ token



    public String buildToken(Map<String, Object> extraInfo, UserDetails userDetails, long expirationTime){
//        System.out.println(Jwts
//                .builder()
//                .setClaims(extraInfo)
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
//                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
//                .compact());
        return Jwts
                .builder()
                .setClaims(extraInfo)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public boolean isValidToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private String create(Map<String, Object> extra, UserDetails userDetails){
         return buildToken(extra, userDetails, expirationTime);
    }

    public String generateToken(UserDetails userDetails){
//        System.out.println(Map.of("role",
//                userDetails.getAuthorities().stream()
//                        .map(Object::toString)
//                        .toList()));
        // {role=[ROLE_ADMIN, FULL_ACCESS_CATEGORY, FULL_ACCESS_ORDER, FULL_ACCESS_PRODUCT, FULL_ACCESS_USER, FULL_ACCESS_STAFF, ACCESS_DENIED_MATERIAL, FULL_ACCESS_PLACE_ORDER]}
        return create(Map.of("role",
                userDetails.getAuthorities().stream()
                        .map(Object::toString)
                        .toList()), userDetails);
    }


    private Key getSigninKey(){
//        System.out.println(secretKey);
        byte[] bytes = Decoders.BASE64.decode(secretKey);
//        System.out.println(bytes);
//        System.out.println(Keys.hmacShaKeyFor(bytes).getAlgorithm());
        return Keys.hmacShaKeyFor(bytes);
    }

    public String generatePasswordResetToken(String email){
        long expiration = System.currentTimeMillis() + 1000 * 60 * 60;
        Date expirationDate = new Date(expiration);
        return Jwts.builder()
                .setExpiration(expirationDate)
                .setSubject(email)
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isValidPasswordResetToken(String token){
        return !isTokenExpired(token);
    }


}
