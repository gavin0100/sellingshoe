package com.data.filtro.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedController {
    @GetMapping("/access-denied")
    public String getAccessDenied(HttpServletResponse response, Model model){
        model.addAttribute("message1", "Thông tin xác thực");
        model.addAttribute("message2", "đã bị thay đổi");
        model.addAttribute("HTTPstatus", "401");
        model.addAttribute("contentStatus", "Unauthorized");
        return "error/accessDenied";
    }
    @GetMapping("/404")
    public String getNotFound(HttpServletResponse response, Model model) {
        model.addAttribute("message1", "Trang cần tìm");
        model.addAttribute("message2", "không tồn tại");
        model.addAttribute("HTTPstatus", "404");
        model.addAttribute("contentStatus", "Not Found");
        model.addAttribute("errorNotFound", "404");
        return "error/accessDenied";
    }
}
