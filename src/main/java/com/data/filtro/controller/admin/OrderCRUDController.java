package com.data.filtro.controller.admin;

import com.data.filtro.model.Category;
import com.data.filtro.model.Order;
import com.data.filtro.model.User;
import com.data.filtro.model.payment.OrderStatus;
import com.data.filtro.service.CategoryService;
import com.data.filtro.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/order")
public class OrderCRUDController {
    @Autowired
    OrderService orderService;

    public Pageable sortOrder(int currentPage, int pageSize, int sortType) {
        Pageable pageable;
        switch (sortType) {
            case 5, 10, 25, 50 -> pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("id"));
            default -> {
                pageSize = 5;
                pageable = PageRequest.of(currentPage - 1, pageSize);
            }
        }
        return pageable;
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_ORDER', 'VIEW_ORDER')")
    public String show(@RequestParam(defaultValue = "5") int sortType, @RequestParam("currentPage") Optional<Integer> page, Model model, HttpSession session) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        List<Order> deliveriedOrders = orderService.filterStatusOrder(4);
        int numberOfdeliveriedOrders = deliveriedOrders.size();
        List<Order> awaitingRefundOrders = orderService.filterStatusOrder(5);
        int numberOfawaitingRefundOrders = awaitingRefundOrders.size();
        int currentPage = page.orElse(1);
        int pageSize = sortType;
        Page<Order> orderPage;
        Pageable pageable;
        pageable = sortOrder(currentPage, pageSize, sortType);
        orderPage = orderService.getAllPaging(pageable);
        List<OrderStatus> orderStatusList = returnListOrderStatus();
        model.addAttribute("orderStatusList", orderStatusList);
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalElements", orderPage.getTotalElements());
        model.addAttribute("sortType", sortType);
        model.addAttribute("numberOfdeliveriedOrders", numberOfdeliveriedOrders);
        model.addAttribute("numberOfawaitingRefundOrders", numberOfawaitingRefundOrders);
        return "admin/boot1/order";
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_ORDER')")
    public String update(@ModelAttribute Order order) {
        orderService.update(order);
        orderService.updateSoldByOrderStatus(order);
        return "redirect:/admin/order";
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_ORDER')")
    public String delete(@RequestParam int id) {
        orderService.delete(id);
        return "redirect:/admin/order";
    }

    public List<OrderStatus> returnListOrderStatus(){
        List<OrderStatus> danhSachOrderStatus = new ArrayList<>();
        danhSachOrderStatus.add(OrderStatus.PENDING);
        danhSachOrderStatus.add(OrderStatus.PAID_MOMO);
        danhSachOrderStatus.add(OrderStatus.PAID_VNPAY);
        danhSachOrderStatus.add(OrderStatus.CONFIRMED);
        danhSachOrderStatus.add(OrderStatus.SHIPPING);
        danhSachOrderStatus.add(OrderStatus.DELIVERED);
        danhSachOrderStatus.add(OrderStatus.CANCELED);
        danhSachOrderStatus.add(OrderStatus.FAILED);
        return danhSachOrderStatus;
    }

}
