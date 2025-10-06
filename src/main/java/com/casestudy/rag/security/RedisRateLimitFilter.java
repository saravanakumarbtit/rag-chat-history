package com.casestudy.rag.security;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RedisRateLimitFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redis;
    private int rateLimit = 120; // default per minute

    public RedisRateLimitFilter(StringRedisTemplate redis){
        this.redis = redis;
    }

    @PostConstruct
    public void init(){
        try {
            String v = System.getenv("RATE_LIMIT"); 
            if (v != null && !v.isBlank()) rateLimit = Integer.parseInt(v);
        } catch (Exception ignored){}
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String apiKey = request.getHeader("X-API-KEY");
        if (apiKey == null) apiKey = "anon";
        String key = "ratelimit:" + apiKey;
        // Use Redis INCR with expiry to count requests per minute
        Long count = redis.opsForValue().increment(key);
        if (count == 1) {
            redis.expire(key, Duration.ofMinutes(1));
        }
        if (count != null && count > rateLimit) {
            response.setStatus(429);
            response.getWriter().write("Too many requests");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
