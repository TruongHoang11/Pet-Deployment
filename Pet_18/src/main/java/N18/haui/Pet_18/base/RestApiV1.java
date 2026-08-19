package N18.haui.Pet_18.base;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

//vi tri co the dat annotation
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
//Đảm bảo annotation này tồn tại trong lúc ứng dụng đang chạy để Spring có thể quét và nhận diện được.
@Documented
@RestController
@RequestMapping("/api/v1")
public @interface RestApiV1 {
}
