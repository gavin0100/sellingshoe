package com.data.filtro.controller.user;

import com.data.filtro.exception.AuthenticationAccountException;
import com.data.filtro.model.*;
import com.data.filtro.model.payment.momo.MomoResponse;
import com.data.filtro.model.payment.vnpay.VNPResponse;
import com.data.filtro.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    CartItemService cartItemService;

    @Autowired
    CartService cartService;

    @Autowired
    PaymentMethodService paymentMethodService;

    @Autowired
    MomoService momoService;
    @Autowired
    VNPayService vnpayService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('FULL_ACCESS_PLACE_ORDER')")
    public String show(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            Cart cart = cartService.getCurrentCartByUserId(user.getId());
            if (cart != null) {
                List<CartItem> cartItemList = cart.getCartItemList();
                if (user.getAddress() != null && user.getCity() != null && user.getZip() != null && user.getPhoneNumber() != null) {
                    model.addAttribute("address", user.getAddress());
                    model.addAttribute("city", user.getCity());
                    model.addAttribute("zip", user.getZip());
                    model.addAttribute("phone", user.getPhoneNumber());
                }
                model.addAttribute("cartItemList", cartItemList);
            }

        } else {
            model.addAttribute("message", "LOGIN TO PLACE AN ORDER!");
        }
        return "user/boot1/order";
    }

    @PostMapping("/placeOrder")
    @PreAuthorize("hasAnyAuthority('FULL_ACCESS_PLACE_ORDER')")
    public String placeOrder(
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("city") String city,
            @RequestParam("zip") Integer zip,
            @RequestParam("paymentMethod") String paymentMethod,
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("Please login before checkout");
        }
        Cart cart = cartService.getCurrentCartByUserId(user.getId());
        List<CartItem> cartItemList = cart.getCartItemList();
        cartService.removeCartByCartId(cart.getId());
        com.data.filtro.model.payment.PaymentMethod paymentMethod1 = com.data.filtro.model.payment.PaymentMethod.COD;
        if (paymentMethod.equals("COD")){
            paymentMethod1 = com.data.filtro.model.payment.PaymentMethod.COD;
        }
        if (paymentMethod.equals("MOMO")){
            paymentMethod1 = com.data.filtro.model.payment.PaymentMethod.MOMO;
        }
        if (paymentMethod.equals("VNPAY")){
            paymentMethod1 = com.data.filtro.model.payment.PaymentMethod.VNPAY;
        }
        Order order = orderService.placeOrder(user, phone, email, address, city, zip, paymentMethod1, cartItemList);
        int orderId = order.getId();
        if (paymentMethod.equals("COD")){
            return "redirect:/invoice/" + orderId;
        }
        if (paymentMethod.equals("MOMO")){
            MomoResponse momoResponse = placeMomoOrder(orderId);
        }
        if (paymentMethod.equals("VNPAY")){
            VNPResponse vnpResponse = placeVNPayOrder(orderId, request);
        }
        return "redirect:/invoice/" + orderId;
    }

    public MomoResponse placeMomoOrder(int orderId){
        Order order = orderService.getOrderById(orderId);
        return momoService.createMomoOrder(order);
    }


    public VNPResponse placeVNPayOrder(int orderId,  HttpServletRequest request){
        Order order = orderService.getOrderById(orderId);
        return vnpayService.createVNPayOrder(order, request);
    }


    @PostMapping("/cancel")
    @PreAuthorize("hasAnyAuthority('FULL_ACCESS_PLACE_ORDER')")
    public String cancel(@RequestParam int id) {
        orderService.updateCancelOrder(id);
        return "redirect:/user/billing";
    }

    @ModelAttribute("sum")
    public int sumOfProducts(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            Cart cart = cartService.getCurrentCartByUserId(user.getId());
            if (cart != null) {
                return cartService.totalOfCartItem(user);
            }
        }
        return 0;
    }

    @ModelAttribute("paymentMethods")
    public List<PaymentMethod> paymentMethods() {
        return paymentMethodService.getAllPaymentMethods();
    }

}
