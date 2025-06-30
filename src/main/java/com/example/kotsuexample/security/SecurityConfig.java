package com.example.kotsuexample.security;

import com.example.kotsuexample.config.redis.RedisUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RedisUtil redisUtil;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisUtil), UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(logout -> logout
                        .logoutUrl("/logout") // 프론트에서 POST 요청
                        .logoutSuccessHandler((request, response, authentication) -> {
                            String token = null;
                            if (request.getCookies() != null) {
                                for (var cookie : request.getCookies()) {
                                    if ("AUTH_TOKEN".equals(cookie.getName())) {
                                        token = cookie.getValue();
                                        break;
                                    }
                                }
                            }

                            // 토큰이 있으면 userId 추출해서 Redis에서 삭제
                            if (token != null && jwtTokenProvider.validateToken(token)) {
                                String userId = jwtTokenProvider.getUserIdFromToken(token);
                                redisUtil.deleteAccessToken("LOGIN_" + userId);
                            }

                            // 쿠키 제거
                            ResponseCookie clearCookie = ResponseCookie.from("AUTH_TOKEN", "")
                                    .httpOnly(true)
                                    .secure(false)
                                    .path("/") // 쿠키의 유효 경료 (/는 모든 경로)
                                    .maxAge(0)
                                    .build();

                            response.setHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "https://kotsu-kotsu.org")); // 프론트 주소 (포트 포함)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // 이게 있어야 withCredentials: true가 적용됨
        config.setExposedHeaders(List.of("Set-Cookie")); // 브라우저가 Set-Cookie 헤더를 읽을 수 있게 함

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
