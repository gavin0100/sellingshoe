package com.data.filtro.service;

import com.data.filtro.model.Cart;
import com.data.filtro.model.CartItem;
import com.data.filtro.model.Order;
import com.data.filtro.model.payment.OrderStatus;
import com.data.filtro.model.payment.vnpay.VNPIPN;
import com.data.filtro.model.payment.vnpay.VNPIPNResponse;
import com.data.filtro.model.payment.vnpay.VNPRequest;
import com.data.filtro.model.payment.vnpay.VNPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VNPayService {
    private final Logger logger = LoggerFactory.getLogger(VNPayService.class);
    private final RestTemplate restTemplate;
    private final Environment env;
    private final OrderService orderService;
    private final CartService cartService;
//    private final String RETURN_URL = "https://shoeselling-fourleavesshoes.onrender.com/user/billing";
//    private final String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

//    private final String RETURN_URL = "http://localhost:8080/user/billing/reset_login";
    private final String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    @Autowired
    CartItemService cartItemService;

    public VNPResponse createVNPayOrder(Order order, HttpServletRequest req){
        String data = vnpRequest(order, req);
        return VNPResponse.builder()
                .status("OK")
                .message("success")
                .paymentUrl(data)
                .build();
    }


    public VNPIPNResponse processIpn(Map<String, String> params){
        try{
            ObjectMapper mapper = new  ObjectMapper();
            VNPIPN vnpipn = mapper.convertValue(params, VNPIPN.class); // map sang đối tượng VNPIPN
            VNPIPNResponse response;
            if(validateSignature(vnpipn)){   // kiểm tra chữ ký hợp lệ không
                if(verifyOrder(vnpipn)){   // kiểm tra mã đơn hàng có tồn tại trong database không
                    if(verifyAmount(vnpipn)){   // kiểm tra tổng tiền đơn hàng có khớp không với db không
                        if(verifyOrderStatus(vnpipn)){ // kiểm tra trạng thái đơn hàng có phải là trạng thái pending không
                            updateOrderStatus(vnpipn);
                            response = VNPIPNResponse.builder()
                                    .RspCode("00")
                                    .Message("Confirm Success")
                                    .build();
                            logger.info("VNPay IPN: {}", vnpipn.getVnp_ResponseCode());
                        }
                        else{
                            response = VNPIPNResponse.builder()
                                    .RspCode("02")
                                    .Message("Order already confirmed")
                                    .build();
                            logger.info("VNPay IPN Order: {}", vnpipn.getVnp_ResponseCode());
                        }
                    }else{
                        response = VNPIPNResponse.builder()
                                .RspCode("04")
                                .Message("Invalid amount")
                                .build();
                        logger.info("VNPay IPN Amount: {}", vnpipn.getVnp_ResponseCode());
                    }
                }else{
                    response = VNPIPNResponse.builder()
                            .RspCode("01")
                            .Message("Invalid order")
                            .build();
                    logger.info("VNPay IPN Check Order: {}", vnpipn.getVnp_ResponseCode());
                }
            }else{
                response = VNPIPNResponse.builder()
                        .RspCode("97")
                        .Message("Invalid signature")
                        .build();
                logger.info("VNPay IPN Signature: {}", vnpipn.getVnp_ResponseCode());
            }
            logger.info("VNPay IPN Response: {}", response);
            return response;
        }catch (Exception e) {
            logger.error("Error when processing IPN: {}", e.getMessage());
        }
        return VNPIPNResponse.builder()
                .RspCode("99")
                .Message("Unknown error")
                .build();
    }

    private boolean validateSignature(VNPIPN vnpipn){
        String vnp_SecureHash = vnpipn.getVnp_SecureHash();
        vnpipn.setVnp_SecureHash(null);   // lấy xong chữ ký, xóa luôn cho chắc
        String rawData = hashAllFields(vnpipn); // chuyển dữ liệu từ object VNPIPN sang dạng chuỗi param trong url
        String mySecureHash = hmacSHA512(env.getProperty("application.security.vnpay.vnp_HashSecret"), rawData);
        // từ chuỗi có dạng chuỗi param trong url và scretkey tạo ra một chữ ký mới. sau đó so sánh chữ ký mới này và chữ ký gửi từ VNPAY về
        return vnp_SecureHash.equals(mySecureHash);
    }

    private boolean verifyAmount(VNPIPN vnpipn){
        Order order = orderService.getOrderByOrderCode(vnpipn.getVnp_TxnRef());
        if(order != null){
                return order.getTotal() == Integer.parseInt(vnpipn.getVnp_Amount())/(100);
        }
        return false;
    }

    private boolean verifyOrder(VNPIPN vnpipn){
        Order order = orderService.getOrderByOrderCode(vnpipn.getVnp_TxnRef());
        // xem mã đơn hàng có tồn tại trong database khng
        return order != null;
    }

    private boolean verifyOrderStatus(VNPIPN vnpipn){
        System.out.println(vnpipn.getVnp_TxnRef());
        Order order = orderService.getOrderByOrderCode(vnpipn.getVnp_TxnRef());
        if(order != null){
            return order.getStatusPayment().equals(OrderStatus.PENDING);
        }
        return false;
    }

    private void updateOrderStatus(VNPIPN vnpipn){
        Order order = orderService.getOrderByOrderCode(vnpipn.getVnp_TxnRef());
        Cart cart = cartService.getCartByAccountName(order.getUser().getAccountName());
        List<CartItem> cartItems = cart.getCartItemList();
        switch (vnpipn.getVnp_ResponseCode()) {
            case "00":
                // xoa cart
                for (CartItem cartItem : cartItems){
                    cartItemService.deleteCartItemFromCartItemIdAndCartId(cartItem.getId(), cartItem.getCart().getId());
                }
                order.setStatusPayment(OrderStatus.PAID_VNPAY);
                break;
            case "24":
                order.setStatusPayment(OrderStatus.CANCELED);
                break;
            default:
                order.setStatusPayment(OrderStatus.FAILED);
                break;
        }
        orderService.saveOrder(order);
    }

//    private void updateProductStockAndSold(Order order){
//        List<OrderDetail> orderDetails = order.getOrderDetails();
//        for(OrderDetail orderDetail: orderDetails){
//            ProductDetail pd = orderDetail.getProductDetail();
//            pd.setStock(pd.getStock() - orderDetail.getQuantity());
//            productDetailService.saveProductDetail(pd);
//            Product product = orderDetail.getProductDetail().getProduct();
//            product.setSold(product.getSold() + orderDetail.getQuantity());
//            productService.save(product);
//        }
//    }

    private String vnpRequest(Order order, HttpServletRequest req){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = dateFormat.format(calendar.getTime());

//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC+7"));
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//        calendar.add(Calendar.HOUR_OF_DAY, 14);
//        String vnp_CreateDate = dateFormat.format(calendar.getTime());
//        calendar.add(Calendar.MINUTE, 15);
//        String vnp_ExpireDate = dateFormat.format(calendar.getTime());
        // phải thêm 7 tiếng vì giờ của server thuê là ở múi giờ 0, còn vnpay là +7.
//        String fullReturnUrl = RETURN_URL + "?username=" + order.getUser().getAccountName();
        String fullReturnUrl = env.getProperty("spring.data.payment.serveo_link") + "/user/billing/reset_login" + "?username=" + order.getUser().getAccountName();
        // khi response gửi từ vnpay hay momo về thì mất token, session, không vào lại web với quyền truy cập được
        VNPRequest request = VNPRequest.builder()
                .vnp_Version("2.1.0")
                .vnp_Command("pay")
                .vnp_Amount(String.valueOf(order.getTotal()*100)) // tổng giá tiền
                .vnp_TxnRef(order.getOrder_code()) // mã đơn hàng bằng order_id và account name
                .vnp_BankCode("")
                .vnp_IpAddr(getIpAddress(req)) //
                .vnp_TmnCode(env.getProperty("application.security.vnpay.vnp_TmnCode"))
                .vnp_CurrCode("VND")
                .vnp_Inv_Company("FILTRO")
                .vnp_OrderType("other")  // đơn hàng không thuộc bất cứ loại cụ thể nào được định nghĩa bởi VNPAY
                .vnp_OrderInfo("Thanh toán bằng VNPay")
                .vnp_Locale("vn")
                .vnp_ReturnUrl(fullReturnUrl)  // url trả về sau khi thanh toán thành công
                .vnp_CreateDate(vnp_CreateDate)   // thiết lập thời gian bắt đầu và kết thúc thanh toán
                .vnp_ExpireDate(vnp_ExpireDate)
                .build();

        String rawData = hashAllFields(request);
        String secureHash = hmacSHA512(env.getProperty("application.security.vnpay.vnp_HashSecret"), rawData);
        request.setVnp_SecureHash(secureHash);
        return vnp_PayUrl + "?"+ rawData + "&vnp_SecureHash=" + secureHash;
    }


    private  String getIpAddress(HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR"); // ipAddress là giá trị của header X-FORWARDED-FOR
            // nếu không có thì là null
            if (ipAdress == null) {
                ipAdress = request.getRemoteAddr();
                // nếu chạy localhost:8080 thì giá trị ip máy client là 0:0:0:0:0:0:0:1
            }
        } catch (Exception e) {
            ipAdress = "Invalid IP:" + e.getMessage();
        }
        return ipAdress;
    }


//    key: 70OXPPONL7XA9QR57L1QDWQVZPYC4L3U
//    data: email=john.doe%40example.com&name=John%20Doe&phone=1234567890

    private String hmacSHA512(String key, String data){
        // data là tham số trên url, key là scretkey lấy từ appliation.yml
        try{
            if(key == null  || data== null){
                throw new NullPointerException();
            }
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            // Khởi tạo đối tượng Mac

            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            // Chuyển đổi khóa thành mảng byte

            SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            // Tạo đối tượng SecretKeySpec

            hmac512.init(secretKey);
            // Khởi tạo Mac với khóa bí mật

            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);

            // quá trình tạo HMAC có hai bước chính: khởi tạo với khóa bí mật và tính toán HMAC cho dữ liệu
            // hmac512.init(secretKey) để thiết lập khóa bí mật, dùng khóa này để mã hóa dữ liệu, khóa bí mật có kiểu dữ liệu SecretKeySpec
            // lúc này trong đối tượng Mac hmac512 đã chứa khóa bí mật
            // dữ liệu chuyển thành byte theo chuẩn UTF_8
            // dùng đối tượng Mac hmac512 để doFinal mảng byte của data tạo ra một mảng byte result cuối cùng



            StringBuilder sb = new StringBuilder(2*result.length);
            // chuyển mảng byte thành chuỗi hex
            // 1 byte đại diện cho 2 ký tự trong chuỗi hex
            for(byte b: result){
                sb.append(String.format("%02x", b&0xff));
            }
            // String.format("%02x", b&0xff): cú pháp chuyển byte thành hex
            // Chuyển đổi mảng byte thành chuỗi hex

            // b & 0xff: Phép toán này đảm bảo rằng giá trị byte b được chuyển đổi thành một giá trị không âm trong khoảng từ 0 đến 255.
            // Điều này là cần thiết vì byte trong Java có thể là số âm (từ -128 đến 127), nhưng khi chuyển đổi sang hex, chúng ta cần giá trị dương.

            // %02x: Đây là định dạng chuỗi trong String.format:
            //%x chỉ định rằng giá trị sẽ được định dạng dưới dạng số thập lục phân.
            //02 chỉ định rằng chuỗi kết quả sẽ có ít nhất 2 ký tự, và nếu giá trị chỉ có 1 ký tự, nó sẽ được thêm số 0 ở phía trước (ví dụ: 0a thay vì a).

            return sb.toString();
            // chữ ký là kêt hợp giữa mảng byte của scretkey và mảng byte của tham số sau khi mã hóa hmac512
        }catch (NoSuchAlgorithmException  | InvalidKeyException e) {
            logger.error("Error when generating HMAC SHA512: {}", e.getMessage());
            return null;
        }
    }


    private String hashAllFields(Object request){
        // ví dụ request [{"name":"John Doe"}, {"email":"john.doe@example.com"}, {"phone":"1234567890"}]
        Field[] fields = request.getClass().getDeclaredFields();
        // Lấy tất cả các trường (fields) đã được khai báo trong lớp của đối tượng request.
        // Lấy tất cả các trường: name, email, phone.

        List<String> fieldNames = Arrays.stream(fields)
                .map(Field::getName)
                .collect(Collectors.toList());
        // Chuyển tên các trường thành một danh sách các chuỗi.
        // Chuyển tên các trường thành danh sách: ["name", "email", "phone"].
        Collections.sort(fieldNames);
        // Sắp xếp danh sách tên các trường theo thứ tự bảng chữ cái.
        // Sắp xếp lại danh sách: ["email", "name", "phone"].

        StringBuilder sb = new StringBuilder();
        // Khởi tạo một đối tượng StringBuilder để xây dựng chuỗi kết quả., có mấy phương tức như append để thêm
        for (String fieldName : fieldNames){
            try{
                Field field = request.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                String fieldValue = (String) field.get(request);
                // Lấy giá trị của trường tương ứng từ đối tượng request.
//                email: john.doe@example.com
//                name: John Doe
//                phone: 1234567890
                if(fieldValue != null && !fieldValue.isEmpty()){
                    sb.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8));
                    sb.append("=");
                    sb.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                    sb.append("&");
                }
                // Nếu giá trị trường không null và không rỗng, mã hóa tên trường và giá trị trường, sau đó thêm vào StringBuilder.
//                email=john.doe%40example.com&
//                name=John%20Doe&
//                phone=1234567890&
            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.error("Error when getting field value: {}", e.getMessage());
            }
        }
        sb.delete(sb.length()-1, sb.length());
        // Xóa ký tự ‘&’ cuối cùng trong chuỗi kết quả.
        return sb.toString();
        // email=john.doe%40example.com&name=John%20Doe&phone=1234567890
    }

}
