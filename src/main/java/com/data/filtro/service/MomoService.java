package com.data.filtro.service;

import com.data.filtro.model.Cart;
import com.data.filtro.model.CartItem;
import com.data.filtro.model.Order;
import com.data.filtro.model.OrderDetail;
import com.data.filtro.model.payment.OrderDetailDto;
import com.data.filtro.model.payment.OrderStatus;
import com.data.filtro.model.payment.momo.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.utils.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomoService {
//    private final String MOMO_API = "https://test-payment.momo.vn/v2/gateway/api";
//    private final String RETURN_URL = "https://shoeselling-fourleavesshoes.onrender.com/user/billing";
//    private final String IPN_API = "https://shoeselling-fourleavesshoes.onrender.com";

    private final String MOMO_API = "https://test-payment.momo.vn/v2/gateway/api";
//    private final String RETRN_URL = env.getProperty("spring.data.payment.serveo_link") + "/user/billing/reset_login";
    @Value("${spring.data.payment.serveo_link}")
    private String IPN_API;

    private final Environment env;
    private final CartService cartService;
    private final OrderService orderService;
    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(MomoService.class);

    @Autowired
    CartItemService cartItemService;

    public MomoResponse createMomoOrder(Order order){
        System.out.println("truy cap vao create Momo order");
        String endpoint = MOMO_API + "/create";
        logger.info(endpoint);
        MomoRequest momoRequest = momoRequest(order);
        return processMomoOrder(endpoint, momoRequest);
    }


    private MomoResponse processMomoOrder(String endpoint, MomoRequest momoRequest) {
        System.out.println("truy cap vao process momo order");
        try{
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(momoRequest);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentLength(json.length());
            HttpEntity<MomoRequest> request = new HttpEntity<>(momoRequest, headers);
            return restTemplate.postForObject(endpoint, request, MomoResponse.class);

        }catch (JsonProcessingException e){
            logger.error("Error while parsing data: {}", e.getMessage());
        }
        return null;
    }


    public void processIPN(String request){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            MomoIPN momoIPN = objectMapper.readValue(request, MomoIPN.class);
            momoIPN.setAccessKey(env.getProperty("application.security.momo.access-key"));
            String signature = momoIPN.getSignature();
            String rawSignature = ipnHashedSignature(momoIPN);
            if(signature.equals(rawSignature) && (verifyOrder(momoIPN) && verifyAmount(momoIPN))){
                    updateOrderStatus(momoIPN);
                    logger.info("IPN verified and updated order status {}, {}", momoIPN.getOrderId(), momoIPN.getResultCode());
            }
        }catch (JsonProcessingException e){
            logger.error("Error: {}", e.getMessage());
        }
    }


    private boolean verifyOrder(MomoIPN momoIPN){
        Order order = orderService.getOrderByOrderCode(momoIPN.getOrderId());
        return order!=null;
    }

    private boolean verifyAmount(MomoIPN momoIPN){
        Order order = orderService.getOrderByOrderCode(momoIPN.getOrderId());
        return String.valueOf(order.getTotal()).equals(String.valueOf(momoIPN.getAmount()));
    }

    private void updateOrderStatus(MomoIPN momoIPN){
        Order order = orderService.getOrderByOrderCode(momoIPN.getOrderId());
        Cart cart = cartService.getCartByAccountName(order.getUser().getAccountName());
        List<CartItem> cartItems = cart.getCartItemList();
        switch (momoIPN.getResultCode().toString()) {
            case "0":
                for (CartItem cartItem : cartItems){
                    cartItemService.deleteCartItemFromCartItemIdAndCartId(cartItem.getId(), cartItem.getCart().getId());
                }
                order.setStatusPayment(OrderStatus.PAID_MOMO);
                break;
            case "1006":
                order.setStatusPayment(OrderStatus.CANCELED);
                break;
            default:
                order.setStatusPayment(OrderStatus.FAILED);
                break;
        }
        orderService.saveOrder(order);
    }


    private String ipnHashedSignature(MomoIPN momoIPN){
        Field[] fields = momoIPN.getClass().getDeclaredFields();
        List<String> fieldsName = Arrays.stream(fields).map(Field::getName).sorted().toList();
        StringBuilder rawSignature = new StringBuilder();
        for(String fieldName : fieldsName){
            try{
                Field field = momoIPN.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                if(fieldName.equals("signature")){
                    continue;
                }
                String fieldValue = String.valueOf(field.get(momoIPN));
                if(fieldValue!=null){
                    rawSignature.append(fieldName).append("=").append(fieldValue).append("&");
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.info("Error: {}", e.getMessage());
            }
        }
        rawSignature.deleteCharAt(rawSignature.length()-1);
        return hmacSHA256(rawSignature.toString());
    }


    private String hmacSHA256(String data) {
        try {
            String secret_key = env.getProperty("application.security.momo.secret-key");
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKey key = new SecretKeySpec(secret_key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(key);
            return new String(Hex.encode(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8))));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("Error while hashing data: {}", e.getMessage());
        }
        return null;
    }

    private MomoRequest momoRequest(Order order){
        System.out.println("truy cap vao momo request");
        List<OrderDetail> orderDetail = orderService.getOrderDetailByOrderId(order.getId());
        System.out.println(orderDetail.size());
        List<OrderDetailDto> orderDetailDtos = new ArrayList<>();
        System.out.println("truoc khi chuyen dto");
        if(orderDetail!=null){
            orderDetail.forEach(od-> orderDetailDtos.add(od.convertToDto()));
        }
        System.out.println("sau khi chuyen dto");
        String orderName = order.getOrder_code() + " " + order.getUser().getAccountName();
        logger.info(env.getProperty("application.security.momo.partner-code"));
//        String fullReturnUrl = RETURN_URL + "?username=" + order.getUser().getAccountName();
        String fullReturnUrl = env.getProperty("spring.data.payment.serveo_link") + "/user/billing/reset_login" + "?username=" + order.getUser().getAccountName();
        // khi response gửi từ vnpay hay momo về thì mất token, session, không vào lại web với quyền truy cập được
        MomoRequest momoRequest = MomoRequest.builder()
                .partnerCode(env.getProperty("application.security.momo.partner-code")) // giống username gửi request tới momo
                .partnerName("TEST")  // lựa chọn môi trường test momo
                .storeName("FOUR-LEAVES-SHOE") // tên cửa hàng trên momo
                .requestType("captureWallet") // bạn đang yêu cầu thực hiện một giao dịch thanh toán qua ví MoMo
                .redirectUrl(fullReturnUrl) // đường dẫn trả về khi thanh toán thành công
                .ipnUrl(IPN_API + "/api/v1/momo/webhook/ipn")  // đường dẫn trả về để momo trả dữ liệu về
                .requestId(UUID.randomUUID().toString()) // tạo id cho request tới momo
                .amount(Long.valueOf(order.getTotal()))  // tổng tiền trên momo
                .lang("vi") // mã ngôn ngữ đại diện cho tiếng Việt.
                .orderId(order.getOrder_code()) // mã đơn hàng
                .orderInfo("Thanh toán đơn hàng bằng MOMO") // mô tả trên momo
                .extraData(Base64.encodeBase64String(orderName.getBytes())) //mã đơn hàng trên momo
                .build();

        String rawSignature = "accessKey=" + env.getProperty("application.security.momo.access-key")
                +"&amount="+momoRequest.getAmount()+"&extraData="+momoRequest.getExtraData()
                +"&ipnUrl="+momoRequest.getIpnUrl()+"&orderId="+momoRequest.getOrderId()
                +"&orderInfo="+momoRequest.getOrderInfo()+"&partnerCode="+momoRequest.getPartnerCode()
                +"&redirectUrl="+momoRequest.getRedirectUrl()+"&requestId="+momoRequest.getRequestId()
                +"&requestType="+momoRequest.getRequestType();
        String signature = hmacSHA256(rawSignature);
        momoRequest.setSignature(signature);

        List<MomoItems> momoItems = new ArrayList<>();
        int sumOfQuantity = 0;
        for(OrderDetailDto od : orderDetailDtos){
            MomoItems momoItem = MomoItems.builder()
                    .id(od.getIdProductDetail().toString()) // id sản phẩm
                    .name(od.getProductName()) // tên sản phẩm
                    .imageUrl(od.getUrlImage()) // đường dẫn hình ảnh sản phẩm
                    .price(Long.valueOf(od.getPrice())) //  // giá từng sản phẩm
                    .currency("VND") // đơn vị tiền
                    .quantity(od.getQuantity()) // số lượng
                    .totalPrice(Long.valueOf(od.getTotal())) // tổng giá tiền của sản phẩm
                    .build();
            sumOfQuantity += od.getQuantity();
            momoItems.add(momoItem);
        }
        momoRequest.setItems(momoItems);
        MomoUserInfo momoUserInfo = MomoUserInfo.builder()
                .name(orderName)  //mã đơn hàng
                .email(order.getEmail()) // email ng dùng
                .phoneNumber(order.getPhoneNumber()) // số điện thoại
                .build();
        momoRequest.setUserInfo(momoUserInfo);

        MomoDeliveryInfo momoDeliveryInfo = MomoDeliveryInfo.builder()
                .deliveryAddress(order.getAddress())  // địa chỉ đơn hàng
                .deliveryFee(String.valueOf(0)) // phí vận chuyển 0đ
                .quantity(String.valueOf(sumOfQuantity)) // số lượng loại sản phẩm
                .build();
        momoRequest.setDeliveryInfo(momoDeliveryInfo);
        return momoRequest;
    }


}
