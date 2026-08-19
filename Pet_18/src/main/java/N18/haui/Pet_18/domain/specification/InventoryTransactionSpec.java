package N18.haui.Pet_18.domain.specification;


import N18.haui.Pet_18.domain.entity.InventoryTransaction;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class InventoryTransactionSpec {

    // Nếu người dùng truyền vào ngày from, hệ thống
    // sẽ lấy ra tất cả các giao dịch kho được tạo từ 00:00:00 của ngày đó trở về sau.
    public static Specification<InventoryTransaction> fromDate(LocalDate from){
        return (root, query, criteria) ->
                from == null ? null :
                        //Hãy tìm tất cả các bản ghi có ngày tạo (createdDate) lớn hơn hoặc
                        // bằng (greaterThanOrEqualTo) thời điểm bắt đầu của ngày from (from.atStartOfDay())."
                        criteria.greaterThanOrEqualTo(root.get("createdDate"), from.atStartOfDay());

    }

    public static Specification<InventoryTransaction> toDate(LocalDate to){
        return (root, query, criteria) ->{
            return to == null ? null :
                    criteria.lessThanOrEqualTo(root.get("createdDate"), to.atTime(23, 59, 59));
        };
    }
}
