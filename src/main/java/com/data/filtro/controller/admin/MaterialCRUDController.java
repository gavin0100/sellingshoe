package com.data.filtro.controller.admin;

import com.data.filtro.model.Material;
import com.data.filtro.service.MaterialService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/material")
public class MaterialCRUDController {
    private String errorMessage = "";
    private String message="";
    @Autowired
    MaterialService materialService;

    public Pageable sortFlavor(int currentPage, int pageSize, int sortType) {
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
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_MATERIAL', 'VIEW_MATERIAL')")
    public String show(@RequestParam(defaultValue = "5") int sortType, @RequestParam("currentPage") Optional<Integer> page, Model model, HttpSession session) {
        if (!errorMessage.equals("")){
            model.addAttribute("errorMessage", errorMessage);
            errorMessage="";
        }
        if (!message.equals("")){
            model.addAttribute("message", message);
            message="";
        }
        List<Material> activeMaterials = materialService.getActiveMaterial(1);
        int numberActiveMaterials = activeMaterials.size();
        int currentPage = page.orElse(1);
        int pageSize = sortType;
        Page<Material> materialPage;
        Pageable pageable;
        pageable = sortFlavor(currentPage, pageSize, sortType);
        materialPage = materialService.getAllPaging(pageable);
        model.addAttribute("materials", materialPage.getContent());
        model.addAttribute("totalPages", materialPage.getTotalPages());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalElements", materialPage.getTotalElements());
        model.addAttribute("sortType", sortType);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("numberActiveMaterials", numberActiveMaterials);
        return "admin/boot1/material";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_MATERIAL')")
    public String create(@ModelAttribute Material material, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            errorMessage = "Nhập sai định dạng dữ liệu";
            return "redirect:/admin/material";
        }
        materialService.create(material);
        message="Tạo material thành công";
        return "redirect:/admin/material";
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_MATERIAL')")
    public String update(@ModelAttribute Material material, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            errorMessage = "Nhập sai định dạng dữ liệu";
            return "redirect:/admin/material";
        }
        materialService.update(material);
        message="Cập nhật material thành công";
        return "redirect:/admin/material";
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_MATERIAL')")
    public String delete(@RequestParam int id) {
        materialService.delete(id);
        message="Xóa material thành công";
        return "redirect:/admin/material";
    }

}
