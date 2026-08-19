package N18.haui.Pet_18.domain.dto.pagination;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPaginationDto {
    private Meta meta;
    private Object result;

    @Getter
    @Setter
    public static class Meta {
        private int page;
        private int pageSize;
        private int pages;
        private long total;
    }

}

//        return {
//                meta: {
//            current: page, //trang hiện tại
//                    pageSize: limit, //số lượng bản ghi đã lấy
//                    pages: totalPages, //tổng số trang với điều kiện query
//                    total: totalItems // tổng số phần tử (số bản ghi)
//        },
//        result: array-data //kết quả query