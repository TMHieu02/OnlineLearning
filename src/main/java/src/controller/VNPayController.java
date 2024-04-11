package src.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.config.VNPayConfig;
import src.Dto.TransactionStatusDTO;
import src.config.annotation.ApiPrefixController;

@CrossOrigin(origins = "https://boss-breath-production.up.railway.app")
@RestController
@ApiPrefixController(value = "/vnpay")
public class VNPayController {

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;

    public String createPayment(String orderInfor, String baseUrl, long amount, String infor) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String orderType = "order-type";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", infor);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = "vn";
        vnp_Params.put("vnp_Locale", locate);

        // Sử dụng URL của front end từ cấu hình
        String urlReturn = frontendBaseUrl + VNPayConfig.vnp_ReturnUrl;
        vnp_Params.put("vnp_ReturnUrl", urlReturn);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        String hashData = VNPayConfig.hashAllFields(vnp_Params);

        try {
            baseUrl = URLEncoder.encode(baseUrl, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String queryUrl = "vnp_PayUrl=" + VNPayConfig.vnp_PayUrl + "&" + "vnp_Version=" + vnp_Version + "&" +
                "vnp_Command=" + vnp_Command + "&" + "vnp_TmnCode=" + vnp_TmnCode + "&" + "vnp_Amount=" + amount + "&" +
                "vnp_CurrCode=VND&" + "vnp_TxnRef=" + vnp_TxnRef + "&" + "vnp_OrderInfo=" + infor + "&" +
                "vnp_OrderType=" + orderType + "&" + "vnp_Locale=" + locate + "&" + "vnp_ReturnUrl=" + urlReturn + "&" +
                "vnp_IpAddr=" + vnp_IpAddr + "&" + "vnp_CreateDate=" + vnp_CreateDate + "&" +
                "vnp_ExpireDate=" + vnp_ExpireDate + "&" + "vnp_SecureHash=" + hashData + "&vnp_Url=" + baseUrl;

        return VNPayConfig.vnp_PayUrl + "?" + queryUrl;
    }

    @PostMapping("/create_payment")
    public String createPayment(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        String amountStr = requestBody.get("amount"); // Lấy giá trị 'amount' từ request body
        String infor = requestBody.get("infor");
        long amount = Long.parseLong(amountStr);

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        // Gọi hàm createPayment với thông tin cần thiết
        String vnpay_url = createPayment("pay", baseUrl, amount, infor);

        return vnpay_url;
    }

    @GetMapping("/test")
    public String send() {
        return "vnpay";
    }

    @GetMapping("payment_infor")
    public ResponseEntity<?> transaction(
            @RequestParam(value = "vnp_Amount") String amount,
            @RequestParam(value = "vnp_BankCode") String bankCode,
            @RequestParam(value = "vnp_OrderInfo") String order,
            @RequestParam(value = "vnp_ResponseCode") String responseCode
    ) {
        TransactionStatusDTO transactionStatusDTO = new TransactionStatusDTO();
        if (responseCode.equals("00")) {
            transactionStatusDTO.setStatus("ok");
            transactionStatusDTO.setMessage("Successfully");
            transactionStatusDTO.setData("");
        } else {
            transactionStatusDTO.setStatus("No");
            transactionStatusDTO.setMessage("Failed");
            transactionStatusDTO.setData("");

        }
        return ResponseEntity.status(HttpStatus.OK).body(transactionStatusDTO);
    }
}
