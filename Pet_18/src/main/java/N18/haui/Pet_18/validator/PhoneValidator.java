package N18.haui.Pet_18.validator;

import N18.haui.Pet_18.validator.annotation.ValidPhone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {
    // Biên dịch trước Regex để tối ưu hiệu năng (chỉ chạy 1 lần khi load class)
    // Hỗ trợ: 03x, 05x, 07x, 08x, 09x (mã VN) và cả dạng quốc tế +84x

    //^ (Dấu mũ): Đánh dấu bắt đầu của chuỗi. Nó đảm bảo không có ký tự lạ nào đứng trước số điện thoại.
    //(0|\\+84) (Cụm bắt đầu):
    // Dấu ngoặc đơn () tạo thành một nhóm lựa chọn.
    // Dấu gạch thẳng | nghĩa là "HOẶC".0 là số 0 thông thường.\\+84 là mã vùng Việt Nam.
    // Lý do phải viết hai dấu gạch chéo \\ trước dấu cộng là vì trong Regex,
    // dấu + là một ký tự đặc biệt (nghĩa là lặp lại 1 hoặc nhiều lần).
    // Muốn check chính xác ký tự + bằng xương bằng thịt, ta
    // phải thêm \\ phía trước để "vô hiệu hóa" nó (escape character).=>
    // Ý nghĩa: Số điện thoại phải bắt đầu bằng 0 HOẶC +84.

    //[3|5|7|8|9] (Đầu số nhà mạng):Dấu ngoặc vuông [] nghĩa là
    // "chỉ chọn 1 trong các ký tự bên trong".=> Ý nghĩa: Ký tự tiếp theo bắt buộc phải là một
    // trong các số: 3, 5, 7, 8, hoặc 9 (đây là các đầu số di động hợp pháp tại Việt Nam hiện nay như
    // Viettel 03x, Mobi 07x, Vina 08x...).
    // Lưu ý nhỏ: Bạn có thể viết gọn lại thành [35789], không cần dấu | bên trong ngoặc vuông.

    //[0-9]{8} (Phần số còn lại):[0-9] nghĩa là bất kỳ chữ số nào từ 0 đến 9.{8}
    // nghĩa là lặp lại chính xác 8 lần.=> Ý nghĩa: Đoạn đuôi phải có đúng 8 chữ số.
    // $ (Dấu đô la): Đánh dấu kết thúc của chuỗi. Nó đảm bảo không có ký tự thừa thãi nào đuôi sau số điện thoại.
    private static final Pattern VIETNAM_PHONE_PATTERN =
            Pattern.compile("^(0|\\+84)[3|5|7|8|9][0-9]{8}$");
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null || value.trim().isEmpty()){
            return true;
        }

        return VIETNAM_PHONE_PATTERN.matcher(value).matches() ;
    }

    @Override
    public void initialize(ValidPhone constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
