package com.data.filtro.controller.admin;

import com.data.filtro.model.Product;
import com.data.filtro.service.CategoryService;
import com.data.filtro.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class AdminProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;


    @GetMapping("/addProduct")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_PRODUCT', 'VIEW_PRODUCT')")
    public String showProduct(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAll());
        return "admin/product/addProduct";
    }

    @PostMapping("/addProduct")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_PRODUCT')")
    public String addProduct(@ModelAttribute("product") Product product,
                             Model model) throws Exception {

        productService.addProduct(product);

        model.addAttribute("product", product);
        return "admin/product/list";
    }

//    @GetMapping("/list")
//    public String show(Model model) {
//        Product newProduct = (Product) model.asMap().get("newProduct");
//        if (newProduct != null) {
//            model.addAttribute("products", Collections.singletonList(newProduct));
//        } else {
//            model.addAttribute("products", productService.getAll());
//        }
//        return "admin/product/list";
//    }
}
