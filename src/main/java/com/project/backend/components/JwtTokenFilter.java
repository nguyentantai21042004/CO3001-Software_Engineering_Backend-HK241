package com.project.backend.components;

import com.project.backend.models.SPSO;
import com.project.backend.models.Student;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    @Value("${api.prefix}")
    private String apiPrefix;

    private final JwtTokenUtils jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (isBypassToken(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
                return;
            }

            final String token = authHeader.substring(7);
            final String email = jwtTokenUtil.getSubject(token);
            final String role = jwtTokenUtil.getRole(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails;

                switch (role) {
                    case "STUDENT":
                        userDetails = (Student) userDetailsService.loadUserByUsername(email);
                        System.out.println("UserDetails: " + userDetails);
                        break;
                    case "SPSO":
                        userDetails = (SPSO) userDetailsService.loadUserByUsername(email);
                        System.out.println("UserDetails: " + userDetails);
                        break;
                    case "ADMIN":
                        userDetails = (SPSO) userDetailsService.loadUserByUsername(email);
                        System.out.println("UserDetails: " + userDetails);
                        break;
                    default:
                        throw new UsernameNotFoundException("User type not recognized");
                }

                if (jwtTokenUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
        }
    }

    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(String.format("%s/users/custom-oauth-login", apiPrefix), "GET"),
                Pair.of(String.format("%s/users/custom-oauth-callback", apiPrefix), "GET"),
                Pair.of(String.format("%s/internal/admin/login", apiPrefix), "POST"),
                Pair.of(String.format("%s/favicon.ico", apiPrefix), "GET"));

        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();

        // Debug log
        System.out.println("Request Path: " + requestPath);
        System.out.println("Request Method: " + requestMethod);
        System.out.println("API Prefix: " + apiPrefix);

        for (Pair<String, String> token : bypassTokens) {
            String path = token.getFirst();
            String method = token.getSecond();
            if (requestPath.matches(path.replace("**", ".*"))
                    && requestMethod.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }
}
