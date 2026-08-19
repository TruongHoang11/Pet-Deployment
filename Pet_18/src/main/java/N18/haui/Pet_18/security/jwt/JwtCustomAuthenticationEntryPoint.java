package N18.haui.Pet_18.security.jwt;


import N18.haui.Pet_18.base.RestData;
import N18.haui.Pet_18.util.BeanUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class JwtCustomAuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {

  @SneakyThrows
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
    System.out.println(">>> EntryPoint blocked request: " + request.getRequestURI());
    System.out.println(">>> Exception detail: " + authException.getMessage());

    MessageSource messageSource = BeanUtil.getBean(MessageSource.class);
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    String message = "You don't have permission to access this resource";
    //String message = messageSource.getMessage("You don't have permission to access this resource", null, LocaleContextHolder.getLocale());
    response.getOutputStream().write(new ObjectMapper().writeValueAsBytes(RestData.error(message)));
  }

}
