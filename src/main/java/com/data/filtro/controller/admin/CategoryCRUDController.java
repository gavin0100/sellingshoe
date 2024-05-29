package com.data.filtro.controller.admin;

import com.data.filtro.model.Account;
import com.data.filtro.model.Category;
import com.data.filtro.model.User;
import com.data.filtro.service.CategoryService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/category")
public class CategoryCRUDController {

    @Autowired
    CategoryService categoryService;

    public Pageable sortCategory(int currentPage, int pageSize, int sortType) {
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
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_CATEGORY', 'VIEW_CATEGORY')")
    public String show(@RequestParam(defaultValue = "5") int sortType, @RequestParam("currentPage") Optional<Integer> page, Model model, HttpSession session) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        List<Category> activeCategories = categoryService.getActiveCategory(1);
        int numberActiveCategory = activeCategories.size();
        int currentPage = page.orElse(1);
        int pageSize = sortType;
        Page<Category> categoryPage;
        Pageable pageable;
        pageable = sortCategory(currentPage, pageSize, sortType);
        categoryPage = categoryService.getAllPaging(pageable);
        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalElements", categoryPage.getTotalElements());
        model.addAttribute("sortType", sortType);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("numberActiveCategory", numberActiveCategory);
        return "admin/boot1/category";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_CATEGORY')")
    public String create(@ModelAttribute Category category) {
        categoryService.create(category);
        return "redirect:/admin/category";
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_CATEGORY')")
    public String update(@ModelAttribute Category category) {
        categoryService.update(category);
        return "redirect:/admin/category";
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_CATEGORY')")
    public String delete(@RequestParam int id) {
        categoryService.delete(id);
        return "redirect:/admin/category";
    }

    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_CATEGORY')")
    public String importFile(@RequestParam("file") MultipartFile file,
                             Model model){
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getSize());
        try{
            boolean result = categoryService.importCategory(file);
            if (result == true){
                model.addAttribute("message", "Successfully import!");
            } else {
                model.addAttribute("message", "Import failed!");
            }

        } catch (Exception ex){
            model.addAttribute("message", "Import failed!");
        }

        return "redirect:/admin/category";

    }
}
