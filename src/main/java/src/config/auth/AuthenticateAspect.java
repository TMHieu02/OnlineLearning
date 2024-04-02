package src.config.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import src.config.exception.UnauthorizedException;
import src.service.User.UserService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

@Aspect
@Component
public class AuthenticateAspect {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userDetailsService;

    public AuthenticateAspect(UserService userDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Before("@annotation(src.config.annotation.Authenticate)")
    public void authenticate() throws UnauthorizedException, NoSuchAlgorithmException, InvalidKeySpecException {
        String token = getTokenFromRequest();
        if (token == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        String username = jwtTokenUtil.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtTokenUtil.validateToken(token, userDetails)) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            request.setAttribute("user", userDetails);
        } else
            throw new UnauthorizedException("Unauthorized");
    }

    private String getTokenFromRequest() {
        // Get token from request headers
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        final String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            return requestTokenHeader.substring(7);
        }
        return null;
    }
}
