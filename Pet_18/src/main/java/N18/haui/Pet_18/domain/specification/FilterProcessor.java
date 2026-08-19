package N18.haui.Pet_18.domain.specification;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record FilterProcessor(SpecificationBuilder<?> specificationBuilder, List<String> filter) {
    public static FilterProcessor process(SpecificationBuilder<?> specificationBuilder, List<String> filter){
        if (filter == null || filter.isEmpty()) {
            return new FilterProcessor(specificationBuilder, filter);
        } else {
            // Regex bắt group chuẩn xác
//            Pattern pattern = Pattern.compile("^('?)\\s*([a-zA-Z0-9_.]+)([<:>~!])(.*)$");

            //* ^('?)        -> Group 1: Bắt dấu nháy đơn ở đầu chuỗi (Dùng làm cờ hiệu toán tử OR), có thể có hoặc không.
            //             * \\s* -> Nuốt hết các khoảng trắng dư thừa (nếu có) trước tên thuộc tính.
            //             * ([a-zA-Z0-9_.]+) -> Group 2: Bắt tên thuộc tính/trường dữ liệu cần lọc (Chấp nhận chữ, số, dấu gạch dưới và dấu chấm để join bảng).
            //             * \\s* -> Nuốt hết các khoảng trắng dư thừa nằm GIỮA tên thuộc tính và toán tử.
            //             * ([<:>~!])    -> Group 3: Bắt chính xác 1 ký tự toán tử nằm trong tập hợp cho phép (<, :, >, ~, !).
            //             * \\s* -> Nuốt hết các khoảng trắng dư thừa nằm GIỮA toán tử và giá trị tìm kiếm.
            //             * (.*)$        -> Group 4: Bắt toàn bộ phần còn lại ở cuối chuỗi làm giá trị cần tìm kiếm (Value).
            Pattern pattern = Pattern.compile("^('?)\\s*([a-zA-Z0-9_.]+)\\s*([<>]=|[<:>~!])\\s*(.*)$");
            for(String condition : filter){
                condition = condition.trim();
                Matcher matcher = pattern.matcher(condition);
                if(matcher.find()){
                    String orIndicator = matcher.group(1);
                    String key = matcher.group(2).trim();
                    String operation = matcher.group(3);
                    String valueStr = matcher.group(4).trim();

                    // Xử lý loại bỏ dấu *
                    FilterAttributeSearch att = FilterAttributeSearch.handleWildCardSearch(valueStr, orIndicator);

                    if(att.isOrPredicate()){
                        specificationBuilder.with(SEARCH_OPERATION.OR_PREDICATE_FLAG, key, operation, att.valueStr(), att.prefix(), att.suffix());
                    } else{
                        specificationBuilder.with(key, operation, att.valueStr(), att.prefix(), att.suffix());
                    }
                }
            }
            return new FilterProcessor(specificationBuilder, filter);
        }
    }
}