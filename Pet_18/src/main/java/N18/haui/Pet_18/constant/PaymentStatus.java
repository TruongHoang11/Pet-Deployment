package N18.haui.Pet_18.constant;

public enum PaymentStatus {
    PENDING,    // Đang chờ thanh toán (Khách chọn chuyển khoản nhưng chưa hoàn tất, hoặc đơn SHIP COD chưa giao)
    PROCESSING, // Đang xử lý (Giao dịch đang được hệ thống hoặc cổng thanh toán kiểm tra)
    SUCCESS,    // Thanh toán thành công (Khách đã trả tiền qua ví/thẻ, hoặc shipper đã thu tiền COD và xác nhận)
    FAILED,     // Thanh toán thất bại (Lỗi thẻ, khách hủy giao dịch giữa chừng tại cổng thanh toán)
    REFUNDED    // Đã hoàn tiền (Dùng khi đơn hàng bị hủy và Admin thực hiện hoàn tiền lại cho khách qua ví/thẻ)
}
