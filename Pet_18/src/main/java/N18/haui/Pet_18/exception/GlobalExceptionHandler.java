package N18.haui.Pet_18.exception;

import N18.haui.Pet_18.base.RestData;
import N18.haui.Pet_18.base.VsResponseUtil;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 1. Lỗi Validate cho các Parameter (ô điền trên @RequestParam, @PathVariable)
  @ExceptionHandler({ConstraintViolationException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<RestData<?>> handleConstraintViolationException(ConstraintViolationException ex) {
    Map<String, String> result = new LinkedHashMap<>();
    ex.getConstraintViolations().forEach((error) -> {
      String fieldName = ((PathImpl) error.getPropertyPath()).getLeafNode().getName();
      // Lấy trực tiếp thông báo lỗi gốc từ DTO/Param mà không qua messageSource nữa
      String errorMessage = error.getMessage();
      result.put(fieldName, errorMessage);
    });
    return VsResponseUtil.error(HttpStatus.BAD_REQUEST, result);
  }

  // 2. Lỗi Validate khi gửi Body Object lỗi (Dùng cho @Valid @RequestBody)
  @ExceptionHandler({MethodArgumentNotValidException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<RestData<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    Map<String, String> result = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors().forEach((error) -> {
      String fieldName = error.getField();
      String errorMessage = error.getDefaultMessage(); // Trả về luôn message viết ở @NotBlank, @NotNull,...
      result.put(fieldName, errorMessage);
    });
    return VsResponseUtil.error(HttpStatus.BAD_REQUEST, result);
  }

  // 3. Lỗi định dạng JSON gửi lên bị sai (Thiếu dấu ngoặc, sai kiểu dữ liệu body,...)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<RestData<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    log.error("JSON parse error: ", ex);
    return VsResponseUtil.error(HttpStatus.BAD_REQUEST, "Required request body is missing or invalid JSON format");
  }

  // 4. Lỗi binding dữ liệu form thông thường
  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<RestData<?>> handleValidException(BindException ex) {
    Map<String, String> result = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      result.put(fieldName, errorMessage);
    });
    return VsResponseUtil.error(HttpStatus.BAD_REQUEST, result);
  }

  // 5. Lỗi Custom: NotFoundException (Ví dụ: "Role Not Found")
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<RestData<?>> handlerNotFoundException(NotFoundException ex) {
    log.error("NotFoundException: {}", ex.getMessage(), ex);
    return VsResponseUtil.error(ex.getStatus(), ex.getMessage()); // Trả trực tiếp string "Role Not Found"
  }

  // 6. Lỗi Custom: InvalidException
  @ExceptionHandler(InvalidException.class)
  public ResponseEntity<RestData<?>> handlerInvalidException(InvalidException ex) {
    log.error("InvalidException: {}", ex.getMessage(), ex);
    return VsResponseUtil.error(ex.getStatus(), ex.getMessage());
  }

  // 7. Lỗi Custom: BadRequestException
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<RestData<?>> handleBadRequest(BadRequestException ex) {
    log.error("BadRequestException: {}", ex.getMessage(), ex);
    return VsResponseUtil.error(ex.getStatus(), ex.getMessage());
  }

  // 8. Lỗi Custom: InternalServerException
  @ExceptionHandler(InternalServerException.class)
  public ResponseEntity<RestData<?>> handlerInternalServerException(InternalServerException ex) {
    log.error("InternalServerException: {}", ex.getMessage(), ex);
    return VsResponseUtil.error(ex.getStatus(), ex.getMessage());
  }

  // 9. Lỗi Custom: UploadFileException
  @ExceptionHandler(UploadFileException.class)
  public ResponseEntity<RestData<?>> handleUploadImageException(UploadFileException ex) {
    log.error("UploadFileException: {}", ex.getMessage(), ex);
    return VsResponseUtil.error(ex.getStatus(), ex.getMessage());
  }

  // 10. Lỗi Custom: UnauthorizedException
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<RestData<?>> handleUnauthorizedException(UnauthorizedException ex) {
    log.error("UnauthorizedException: {}", ex.getMessage(), ex);
    return VsResponseUtil.error(ex.getStatus(), ex.getMessage());
  }

  // 11. Lỗi Custom: ForbiddenException
  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<RestData<?>> handleAccessDeniedException(ForbiddenException ex) {
    log.error("ForbiddenException: {}", ex.getMessage(), ex);
    return VsResponseUtil.error(ex.getStatus(), ex.getMessage());
  }

  // 12. Lỗi Custom: ConflictException
  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<RestData<?>> handleConflictException(ConflictException ex) {
    log.warn("ConflictException: {}", ex.getMessage());
    return VsResponseUtil.error(ex.getStatus(), ex.getMessage());
  }

  // 13. Lỗi Custom: VsException chung
  @ExceptionHandler(VsException.class)
  public ResponseEntity<RestData<?>> handleVsException(VsException ex) {
    log.error("VsException: {}", ex.getErrMessage(), ex);
    return VsResponseUtil.error(ex.getStatus(), ex.getErrMessage());
  }

  // 14. Lỗi sai URL / không tìm thấy API endpoint vật lý
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<RestData<?>> handleNoResourceFoundException(NoResourceFoundException ex) {
    log.warn("NoResourceFoundException: {}", ex.getMessage());
    return VsResponseUtil.error(HttpStatus.NOT_FOUND, "Resource not found");
  }

  // 15. Lỗi System chung (Lỗi logic không kiểm soát được - Bug ngầm hệ thống)
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<RestData<?>> handlerInternalServerError(Exception ex) {
    log.error("Critical System Error: {}", ex.getMessage(), ex);
    return VsResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
  }
}