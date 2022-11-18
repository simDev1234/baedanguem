package com.example.baedanguem.security;

import com.example.baedanguem.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.h2.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final long TOKEN_EXPIRED_TIME = 1000 * 60 * 60; // 1 hour
    private static final String KEY_ROLES = "roles";

    private final MemberService memberService;

    @Value("{spring.jwt.secret}")
    public String secretKey;

    /**
     * 토큰 생성
     * @param username
     * @param roles
     * @return
     */
    public String generateToken(String username, List<String> roles){

        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles);

        var now = new Date();
        var expiredDate = new Date(now.getTime() + TOKEN_EXPIRED_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS512, this.secretKey) // 사용할 알고리즘, 비밀키
                .compact();
    }

    public Authentication getAuthentication(String jwt){
        UserDetails userDetails = this.memberService.loadUserByUsername(this.getUsername(jwt));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return this.parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        if (StringUtils.isNullOrEmpty(token)) {
            return false;
        }

        var claims = this.parseClaims(token);

        return !claims.getExpiration().before(new Date());
    }

    private Claims parseClaims(String token) {

        try {
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        } catch(ExpiredJwtException e) {
            //TODO
            return e.getClaims();
        }

    }

}
