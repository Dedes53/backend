package federicolepore.backend.security;

import federicolepore.backend.entities.User;
import federicolepore.backend.exceptions.UnauthorizedException;
import federicolepore.backend.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private final TokenTools tokenTools;
    private final UserService userService;

    public TokenFilter(TokenTools tokenTools, UserService userService) {
        this.tokenTools = tokenTools;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Invalid token supplied in the authorization header");
        }

        String accessToken = authorizationHeader.substring(7);

        tokenTools.verifyToken(accessToken);
        UUID userId = tokenTools.extractFromToken(accessToken);
        User authenticatedUser = userService.findById(userId);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticatedUser,
                null,
                authenticatedUser.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || new AntPathMatcher().match("/auth/**", path);
    }
}