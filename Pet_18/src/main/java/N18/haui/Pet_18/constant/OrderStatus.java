package N18.haui.Pet_18.constant;

public enum OrderStatus {
    PENDING, //Chờ xử lý / Đang chờ duyệt (Đơn hàng mới tạo, hệ thống hoặc nhân viên chưa xác nhận).
    PROCESSING, // Đang xử lý / Đang chuẩn bị hàng (Shop đã nhận đơn, đang đóng gói hoặc kiểm tra kho).
    SHIPPED, //Đã giao cho đơn vị vận chuyển / Đang giao hàng (Hàng đã rời kho và đang trên đường tới tay khách).
    DELIVERED,  //Giao hàng thành công / Đã giao (Khách đã nhận được hàng).
    CANCELLED //Đã hủy (Đơn hàng bị hủy bởi người mua, người bán hoặc hệ thống).
}
