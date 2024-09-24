package com.data.filtro.controller.user;

import com.data.filtro.model.*;
import com.data.filtro.model.payment.OrderStatus;
import com.data.filtro.model.payment.momo.MomoResponse;
import com.data.filtro.model.payment.vnpay.VNPResponse;
import com.data.filtro.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    private final CartItemService cartItemService;

    private final CartService cartService;

    private final PaymentMethodService paymentMethodService;

    private final MomoService momoService;
    private final VNPayService vnpayService;

    private final MailSenderService mailSender;

    public OrderController(OrderService orderService, CartItemService cartItemService, CartService cartService, PaymentMethodService paymentMethodService, MomoService momoService, VNPayService vnpayService, MailSenderService mailSender) {
        this.orderService = orderService;
        this.cartItemService = cartItemService;
        this.cartService = cartService;
        this.paymentMethodService = paymentMethodService;
        this.momoService = momoService;
        this.vnpayService = vnpayService;
        this.mailSender = mailSender;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('FULL_ACCESS_PLACE_ORDER')")
    public String show(Model model) {
        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = (User) authentication.getPrincipal();
        }
        if (user != null) {
            Cart cart = cartService.getCurrentCartByUserId(user.getId());
            if (cart != null) {
                List<CartItem> cartItemList = cart.getCartItemList();
                if (user.getAddress() != null && user.getCity() != null && user.getZip() != null && user.getPhoneNumber() != null) {
                    model.addAttribute("user", user);
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
            HttpServletRequest request,
            Model model
    ) {

        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = (User) authentication.getPrincipal();
        }
        if (user == null) {
            throw new RuntimeException("Please login before checkout");
        }
        Cart cart = cartService.getCurrentCartByUserId(user.getId());
        List<CartItem> cartItemList = cart.getCartItemList();
        com.data.filtro.model.payment.PaymentMethod paymentMethod1 = com.data.filtro.model.payment.PaymentMethod.COD;
        paymentMethod1 = com.data.filtro.model.payment.PaymentMethod.COD;
        Order order = orderService.placeOrder(user, phone, email, address, city, zip, paymentMethod1, cartItemList);
        List<CartItem> cartItems = cart.getCartItemList();
        for (CartItem cartItem : cartItems){
            cartItemService.deleteCartItemFromCartItemIdAndCartId(cartItem.getId(), cartItem.getCart().getId());
        }
        orderService.updateStatusOrder(OrderStatus.PENDING, order);
        String to = user.getEmail();

        // Sender's email ID needs to be mentioned
        String from = "voduc0100@gmail.com";

        // Assuming you are sending email from localhost
        String host = "smtp.gmail.com";

        // Subject
        String subject = "SHOP BÁN GIÀY FOUR LEAVES SHOE - HÓA ĐƠN MUA HÀNG!";

        mailSender.sendHoaDon(to, from, host, subject, order, order.getOrderDetails());
        int orderId = order.getId();
        return "redirect:/invoice/" + orderId;
    }

    @PostMapping("/placeOrderMomo")
    @PreAuthorize("hasAnyAuthority('FULL_ACCESS_PLACE_ORDER')")
    public void placeOrderMomo(
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("city") String city,
            @RequestParam("zip") Integer zip,
            @RequestParam("paymentMethod") String paymentMethod,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            RedirectAttributes redirectAttributes
    ) throws IOException {
        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = (User) authentication.getPrincipal();
        }
        if (user == null) {
            throw new RuntimeException("Please login before checkout");
        }
        Cart cart = cartService.getCurrentCartByUserId(user.getId());
        List<CartItem> cartItemList = cart.getCartItemList();
        com.data.filtro.model.payment.PaymentMethod paymentMethod1 = com.data.filtro.model.payment.PaymentMethod.COD;
        paymentMethod1 = com.data.filtro.model.payment.PaymentMethod.MOMO;
        Order order = orderService.placeOrder(user, phone, email, address, city, zip, paymentMethod1, cartItemList);
        int orderId = order.getId();
        MomoResponse momoResponse = placeMomoOrder(orderId);
        System.out.println("momoResponse.getPayUrl(): " + momoResponse.getPayUrl());
        response.sendRedirect(momoResponse.getPayUrl());
    }

    @PostMapping("/placeOrderVnpay")
    @PreAuthorize("hasAnyAuthority('FULL_ACCESS_PLACE_ORDER')")
    public void placeOrderVnpay(
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("city") String city,
            @RequestParam("zip") Integer zip,
            @RequestParam("paymentMethod") String paymentMethod,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            RedirectAttributes redirectAttributes
    ) throws IOException {
        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = (User) authentication.getPrincipal();
        }
        if (user == null) {
            throw new RuntimeException("Please login before checkout");
        }
        Cart cart = cartService.getCurrentCartByUserId(user.getId());
        List<CartItem> cartItemList = cart.getCartItemList();
        com.data.filtro.model.payment.PaymentMethod paymentMethod1 = com.data.filtro.model.payment.PaymentMethod.COD;
        paymentMethod1 = com.data.filtro.model.payment.PaymentMethod.VNPAY;
        Order order = orderService.placeOrder(user, phone, email, address, city, zip, paymentMethod1, cartItemList);
        int orderId = order.getId();
        String url = "";
        VNPResponse vnpResponse = placeVNPayOrder(orderId, request);

        response.sendRedirect(vnpResponse.getPaymentUrl());

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
    public int sumOfProducts() {
        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = (User) authentication.getPrincipal();
        }
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
