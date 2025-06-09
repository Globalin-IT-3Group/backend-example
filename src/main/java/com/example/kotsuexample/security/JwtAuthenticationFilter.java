package com.example.kotsuexample.security;

import com.example.kotsuexample.config.redis.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;

    private static final Set<String> WHITELIST = Set.of(
            "/kakao/user/auth",
            "/user/login", "/favicon.ico",
            "/user/join",
            "/user/check-email",
            "/user/find-email",
            "/user/find-password"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        System.out.println("uri = " + uri);
        
        if (WHITELIST.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractTokenFromCookie(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String userId = jwtTokenProvider.getUserIdFromToken(token);

            String savedToken = redisUtil.getAccessToken("LOGIN_" + userId);
            if (savedToken != null && savedToken.equals(token)) {
                request.setAttribute("userId", Integer.valueOf(userId));
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 인증 실패
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\": \"Unauthorized\"}");
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("AUTH_TOKEN".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
