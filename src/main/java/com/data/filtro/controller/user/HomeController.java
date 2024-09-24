package com.data.filtro.controller.user;

import com.data.filtro.model.Product;
import com.data.filtro.model.User;
import com.data.filtro.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String home(HttpServletRequest request, Model model) {

        List<Product> productTopSellingList = productService.getTopSellingProducts();
        List<Product> product6thList = productService.getSixthProducts();
        List<Product> productTop4Discount = productService.getTopDiscountProducts();

        model.addAttribute("productTopSellingList", productTopSellingList);
        model.addAttribute("product6thList", product6thList);
        model.addAttribute("productTop4DiscountList", productTop4Discount);
        User userSession = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            userSession = (User) authentication.getPrincipal();
        }
        model.addAttribute("userSession", userSession);

        return "user/boot1/index";
    }


}
