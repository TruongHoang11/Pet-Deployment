package N18.haui.Pet_18.validator;

import N18.haui.Pet_18.validator.annotation.EnumValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Stream;

public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {
    private List<String> acceptedValues;
    @Override
    public void initialize(EnumValue enumValue) {
        acceptedValues = Stream.of(enumValue.enumClass().getEnumConstants())
                .map(Enum::name)
                .toList();
    }


@Override
public boolean isValid(Object value, ConstraintValidatorContext context) {
    if (value == null) return true;

    boolean isValid = acceptedValues.contains(value.toString().toUpperCase().trim());

    if (!isValid) {
        // Tắt thông báo mặc định
        context.disableDefaultConstraintViolation();
        // Tạo thông báo mới kèm danh sách giá trị hợp lệ
        context.buildConstraintViolationWithTemplate(
                        "must be any of enum " + acceptedValues.toString())
                .addConstraintViolation();
    }

    return isValid;
}
}
