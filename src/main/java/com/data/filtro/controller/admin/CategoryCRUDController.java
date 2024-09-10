package com.data.filtro.controller.admin;

import com.data.filtro.model.Category;
import com.data.filtro.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/category")
@Slf4j
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
    private String errorMessage = "";
    private String message="";

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_CATEGORY', 'VIEW_CATEGORY')")
    public String show(@RequestParam(defaultValue = "5") int sortType, @RequestParam("currentPage") Optional<Integer> page, Model model, HttpSession session) {
        if (!errorMessage.equals("")){
            model.addAttribute("errorMessage", errorMessage);
            errorMessage="";
        }
        if (!message.equals("")){
            model.addAttribute("message", message);
            message="";
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
    public String create(@ModelAttribute Category category, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            errorMessage = "Nhập sai định dạng dữ liệu";
            return "redirect:/admin/order";
        }
        categoryService.create(category);
        message="Tạo danh mục thành công";
        return "redirect:/admin/category";
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_CATEGORY')")
    public String update(@ModelAttribute Category category, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            errorMessage = "Nhập sai định dạng dữ liệu";
            return "redirect:/admin/order";
        }
        categoryService.update(category);
        message="Cập nhật danh mục thành công";
        return "redirect:/admin/category";
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_CATEGORY')")
    public String delete(@RequestParam int id) {
        categoryService.delete(id);
        message="Xóa danh mục thành công";
        return "redirect:/admin/category";
    }

    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_CATEGORY')")
    public String importFile(@RequestParam("file") MultipartFile file,
                             Model model){
        log.info(file.getOriginalFilename() + ", size: " + file.getSize());
        try{
            boolean result = categoryService.importCategory(file);
            if (result == true){
                message = "Import thành công!";
            } else {
                errorMessage = "Import thất bại!";
            }

        } catch (Exception ex){
            errorMessage = "Import thất bại!";
        }

        return "redirect:/admin/category";

    }
}
