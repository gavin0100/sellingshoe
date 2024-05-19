package com.data.filtro.controller.admin;

import com.data.filtro.model.Material;
import com.data.filtro.model.User;
import com.data.filtro.model.UserPermission;
import com.data.filtro.service.MaterialService;
import com.data.filtro.service.UserPermissionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/user-permission")
public class UserPermissionCRUDController {

    @Autowired
    UserPermissionService userPermissionService;

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String show( Model model, HttpSession session) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        List<UserPermission> userPermissions = userPermissionService.getAll();
        model.addAttribute("userPermissions", userPermissions);
        return "admin/boot1/user-permission";
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String update(@ModelAttribute UserPermission userPermission) {
        System.out.println(userPermission.getPermissionId() + " " + userPermission.getUserManagement() + " " + userPermission.getMaterialManagement());
        userPermissionService.update(userPermission);
        return "redirect:/admin/user-permission";
    }


}
