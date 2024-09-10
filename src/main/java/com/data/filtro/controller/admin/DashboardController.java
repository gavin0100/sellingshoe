package com.data.filtro.controller.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class DashboardController {

//    @GetMapping
//    public String show(HttpSession session) {
//        User admin = (User) session.getAttribute("admin");
//        if (admin == null) {
//            return "redirect:/admin/login";
//        }
//
//        return "redirect:/admin/dashboard";
//    }


    @GetMapping({"/dashboard", ""})
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_CATEGORY', 'FULL_ACCESS_PLACE_ORDER')")
    public String showDashboard(){
        try{
            return "admin/boot1/dashboard";
        } catch (Exception ex ){
            return "redirect:/admin/product";
        }

    }


}
