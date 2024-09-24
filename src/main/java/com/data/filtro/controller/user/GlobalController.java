package com.data.filtro.controller.user;

import com.data.filtro.model.*;
import com.data.filtro.service.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@ControllerAdvice
@RequestMapping({"/", "/product", "/category", "/search"})
public class GlobalController {

    private final ProductService productService;

    private final CategoryService categoryService;

    private final MaterialService flavorService;

    private final UserService userService;

    private final CartService cartService;

    public GlobalController(ProductService productService, @Lazy CategoryService categoryService, @Lazy MaterialService flavorService, UserService userService, CartService cartService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.flavorService = flavorService;
        this.userService = userService;
        this.cartService = cartService;
    }


    @ModelAttribute("categories")
    public List<Category> getCategories() {
        List<Category> categories = categoryService.getAll();
        return categories;
    }

    @ModelAttribute("products")
    public List<Product> getProducts() {
        List<Product> productList = productService.getAll();
        return productList;
    }

    @ModelAttribute("flavors")
    public List<Material> getFlavors() {
        List<Material> flavors = flavorService.getAll();
        return flavors;
    }

    @ModelAttribute("cartItemList")
    public List<CartItem> cartItemList() {
        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = (User) authentication.getPrincipal();
        }
        if (user != null) {
            Cart cart = cartService.getCurrentCartByUserId(user.getId());
            if (cart != null) {
                List<CartItem> cartItemList = cart.getCartItemList();
                return cartItemList;
            }
        }
        return null;
    }

}
