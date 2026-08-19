package N18.haui.Pet_18.security.jwt;


import N18.haui.Pet_18.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Slf4j
@Component
public class JwtPreFilter extends OncePerRequestFilter {

  private final CustomUserDetailsService customUserDetailsService;

  private final HandlerExceptionResolver resolver; // Cầu nối quan trọng nhất
  private final JwtTokenProvider tokenProvider;


  public JwtPreFilter(
          CustomUserDetailsService customUserDetailsService,
          @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver, // @Qualifier phải đặt ở đây
          JwtTokenProvider tokenProvider
  ) {
    this.customUserDetailsService = customUserDetailsService;
    this.resolver = resolver;
    this.tokenProvider = tokenProvider;
  }

  @SneakyThrows
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
    try {
      String jwt = getJwtFromRequest(request);
      if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

          String id = tokenProvider.extractSubjectFromJwt(jwt);
          UserDetails userDetails = customUserDetailsService.loadUserById(id);
          UsernamePasswordAuthenticationToken authenticationToken =
              new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
          authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
    } catch (Exception ex) {
      log.error("Could not set user authentication in security context", ex);
    }
    filterChain.doFilter(request, response);
  }

  public static String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7, bearerToken.length());
    }
    return null;
  }

}
