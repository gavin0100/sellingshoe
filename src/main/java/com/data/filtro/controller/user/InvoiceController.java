package com.data.filtro.controller.user;

import com.data.filtro.Util.ExportPdf;
import com.data.filtro.exception.AuthenticationAccountException;
import com.data.filtro.model.Order;
import com.data.filtro.model.OrderDetail;
import com.data.filtro.model.User;
import com.data.filtro.service.CartService;
import com.data.filtro.service.InvoiceService;
import com.data.filtro.service.OrderService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {

    private final CartService cartService;

    private final OrderService orderService;

    private final InvoiceService invoiceService;

    public InvoiceController(CartService cartService, OrderService orderService, InvoiceService invoiceService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.invoiceService = invoiceService;
    }

    @GetMapping("/{orderId}")
    public String show(@PathVariable("orderId") int orderId, Model model) {
        try {
            User user = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
                user = (User) authentication.getPrincipal();
            }
            if (user == null) {
                throw new AuthenticationAccountException("Vui lòng đăng nhập để xem đơn hàng của bạn!");
            }
            Order order = orderService.getOrderById(orderId);
            List<OrderDetail> orderDetailList = order.getOrderDetails();
            int check = orderService.checkOrderStatusById(orderId);
            model.addAttribute("user", user);
            model.addAttribute("order", order);
            model.addAttribute("orderDetailList", orderDetailList);
            model.addAttribute("check", check);
            model.addAttribute("orderId", orderId);
        } catch (AuthenticationAccountException ex) {
            model.addAttribute("message", ex.getMessage());
        }
        return "user/boot1/invoice";
    }

    @GetMapping(value = "/{orderId}/exportpdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> showOrderPdf(@PathVariable("orderId") int orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        List<OrderDetail> orderDetailList = order.getOrderDetails();

        ByteArrayInputStream bis = ExportPdf.employeesReport(order, orderDetailList);

        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Disposition", "attachment;filename=invoice.pdf");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @PostMapping("/makeInvoice/{orderId}")
    public String makeInvoice(@PathVariable("orderId") int orderId,
                              @RequestParam("totalPrice") int totalPrice,
                              Model model) {
        try {
            User user = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
                user = (User) authentication.getPrincipal();
            }
            if (user == null) {
                throw new AuthenticationAccountException("Vui lòng đăng nhập để xem đơn hàng của bạn!");
            }
            Order order = orderService.getOrderById(orderId);
            invoiceService.makeInvoice(order, totalPrice);
            orderService.updateOrderStatus(orderId);
            orderService.updateSoldByOrderStatus(order);
        } catch (AuthenticationAccountException ex) {
            model.addAttribute("message", ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "redirect:/";
    }

}
