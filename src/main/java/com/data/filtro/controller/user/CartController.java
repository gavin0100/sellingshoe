package com.data.filtro.controller.user;

import com.data.filtro.model.Cart;
import com.data.filtro.model.CartItem;
import com.data.filtro.model.Product;
import com.data.filtro.model.User;
import com.data.filtro.repository.CartRepository;
import com.data.filtro.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final UserService userService;
    private final ProductService productService;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final GuestCartService guestCartService;
    private final CartItemService cartItemService;

    private String[] productIdArray;
    private String[] quantityArray;

    private Product tempProduct = new Product();

    public CartController(UserService userService, ProductService productService, CartRepository cartRepository, CartService cartService, GuestCartService guestCartService, CartItemService cartItemService) {
        this.userService = userService;
        this.productService = productService;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
        this.guestCartService = guestCartService;
        this.cartItemService = cartItemService;
    }


    @GetMapping
    public String showCart(Model model) {
        User userSession = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            userSession = (User) authentication.getPrincipal();
        }
        model.addAttribute("userSession", userSession);
        if (userSession != null) {
            Cart cart = cartService.getCurrentCartByUserId(userSession.getId());
            if (cart != null) {
                List<CartItem> cartItemList = cart.getCartItemList();
                model.addAttribute("cartItemList", cartItemList);
                model.addAttribute("cart", cart);
            }
        } else {
            model.addAttribute("cartItemList", new ArrayList<CartItem>());
            model.addAttribute("message", "Bạn cần đăng nhập để sử dụng giỏ hàng!");
        }
        return "user/boot1/cart";
    }

    @PostMapping("/add")
    public String addCart(@RequestParam("productId") int productId, @RequestParam("quantity") int quantity) {
        User userSession = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            userSession = (User) authentication.getPrincipal();
        }
        Cart cart = null;
        if (userSession != null) {
            cart = cartService.getCurrentCartByUserId(userSession.getId());
            cartService.addProductToCart(cart, productId, quantity);
        }
        return "redirect:/cart";
    }


    @PostMapping("/remove/{productId}")
    public String removeCartItem(@PathVariable("productId") int productId) {
        User userSession = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            userSession = (User) authentication.getPrincipal();
        }
        if (userSession != null) {
            Cart cart = cartService.getCurrentCartByUserId(userSession.getId());
            cartItemService.removeCartItemByCartIdAndProductId(cart.getId(), productId);
        }
        return "redirect:/cart";
    }


    @PostMapping("/update")
    public String updateCartBeforePlaceOrder(@RequestParam("productIds") String productIds,
                                             @RequestParam("quantities") String quantities,
                                             Model model){
        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = (User) authentication.getPrincipal();
        }
        if (user == null){
            return "user/boot1/cart";
        }
        if (productIds != null && quantities != null) {
            productIdArray = productIds.split(",");
            quantityArray = quantities.split(",");
        }
        Cart cart = cartService.getCurrentCartByUserId(user.getId());

        int totalPriceItem = 0;
        int latestPrice = 0;
        for (int i = 0; i < productIdArray.length; i++) {
            String productId = productIdArray[i].trim();
            String quantity = quantityArray[i].trim();
            tempProduct = productService.getProductById(Integer.parseInt(productId));
            totalPriceItem = (tempProduct.getPrice() - tempProduct.getPrice()*tempProduct.getDiscount()/100) * Integer.parseInt(quantity);
            latestPrice = tempProduct.getPrice() - tempProduct.getPrice()*tempProduct.getDiscount()/100;
            cartItemService.updateQuantityByProductId(cart.getId(),Integer.parseInt(productId), Integer.parseInt(quantity), totalPriceItem, latestPrice);
        }
        return "redirect:/order";
    }

    @ModelAttribute("sum")
    public int sumOfProducts(HttpSession session) {
        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = (User) authentication.getPrincipal();
        }
        if (user != null) {
            Cart cart = cartService.getCurrentCartByUserId(user.getId());
            if (cart != null) {
                return cartService.totalOfCartItem(user);
            } else {
                return 0;
            }
        }
        return 0;
    }

}
