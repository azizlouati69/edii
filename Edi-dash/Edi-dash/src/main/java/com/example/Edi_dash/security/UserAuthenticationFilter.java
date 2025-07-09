package com.example.Edi_dash.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class UserAuthenticationFilter extends OncePerRequestFilter {
@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String username = request.getHeader("X-User-Name");
        String userIdStr = request.getHeader("X-User-Id");

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Long userId = null;
            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException ignored) {}

            // Build simple Authentication object
            UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(username)
                    .password("")  // no password here
                    .authorities(Collections.emptyList())  // set roles if forwarded by gateway
                    .build();

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
