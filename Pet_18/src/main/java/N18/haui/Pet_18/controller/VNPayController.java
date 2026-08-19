package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.base.RestApiV1;
import N18.haui.Pet_18.constant.UrlConstant;
import N18.haui.Pet_18.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@RequiredArgsConstructor
public class VNPayController {

    private final VNPayService vnPayService;

    @PostMapping(UrlConstant.Payment.CREATE_PAYMENT)
    public ResponseEntity<String> createPayment(
            @RequestParam Long orderId,
            HttpServletRequest request) {
        String paymentUrl = vnPayService.createPaymentUrl(orderId, request);
        return ResponseEntity.ok(paymentUrl);
    }

//    @RequestMapping(
//            value = UrlConstant.Payment.HANDLE_RETURN,
//            method = {RequestMethod.GET, RequestMethod.POST}
//    )
//    public void handleReturn(HttpServletRequest request, HttpServletResponse response)
//            throws IOException {
//        String result = vnPayService.handleReturn(request);
//        // Redirect sang FE
//        if ("SUCCESS".equals(result)) {
//            response.sendRedirect("http://localhost:5173/);
//        } else {
//            response.sendRedirect("http://localhost:3000/payment/failed");
//        }
//    }


@RequestMapping(
        value = UrlConstant.Payment.HANDLE_RETURN,
        method = {RequestMethod.GET, RequestMethod.POST}
)
public ResponseEntity<String> handleReturn(HttpServletRequest request) {

    String result = vnPayService.handleReturn(request);

    return ResponseEntity.ok(result);
}

//    @RequestMapping(
//            value = UrlConstant.Payment.HANDLE_RETURN,
//            method = {RequestMethod.GET, RequestMethod.POST}
//    )
//    public void handleReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String result = vnPayService.handleReturn(request);
//
//        String redirectUrl = switch (result) {
//            case "SUCCESS" -> "http://localhost:5173/payment/success";
//            case "STOCK_SHORTAGE" -> "http://localhost:5173/payment/stock-shortage";
//            default -> "http://localhost:5173/payment/failed";
//        };
//
//        response.sendRedirect(redirectUrl);
//    }

    @GetMapping(UrlConstant.Payment.GET_PAYMENT_STATUS)
    public ResponseEntity<?> getPaymentStatus(@RequestParam Long orderId) {
        return ResponseEntity.ok(vnPayService.getPaymentStatus(orderId));
    }
}