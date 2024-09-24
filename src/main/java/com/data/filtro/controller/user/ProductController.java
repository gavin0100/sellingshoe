package com.data.filtro.controller.user;

import com.data.filtro.model.Feedback;
import com.data.filtro.model.Product;
import com.data.filtro.model.User;
import com.data.filtro.service.FeedbackService;
import com.data.filtro.service.InputService;
import com.data.filtro.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/product")
@Slf4j
public class ProductController {

    private final ProductService productService;

    private final FeedbackService feedbackService;

    private final InputService inputService;

    private final String PREFIX_DETAILED_PRODUCT = "detailed_product:";

    private String errorMessage;
    private String csrfToken;

    public ProductController(ProductService productService, FeedbackService feedbackService, InputService inputService) {
        this.productService = productService;
        this.feedbackService = feedbackService;
        this.inputService = inputService;
    }

    @GetMapping
    public String product() {
        return "redirect:/category/all";
    }

    @GetMapping("/{id}")
    public String product(@PathVariable Integer id, Model model) {
        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = (User) authentication.getPrincipal();
        }
        model.addAttribute("user", user);
        String _csrfToken = generateRandomString();
        csrfToken = _csrfToken;
        model.addAttribute("_csrfToken", _csrfToken);
        int currentProductId = id;
        long maxProductId = productService.countAll();
        int t1 = 13;
        long t2 = 24;
        Product product = new Product();

        product = productService.getProductById(id);

        List<Feedback> feedbackList = feedbackService.getAllFeedBackByProductId(id);
        int numberOfFeedback = feedbackList.size();
        List<Product> productList = productService.getTop4ProductsByMaterial(product.getMaterial().getId(), currentProductId);
        model.addAttribute("product", product);
        model.addAttribute("numberOfFeedback", numberOfFeedback);
        model.addAttribute("products", productList);
        model.addAttribute("currentProductId", currentProductId);
        model.addAttribute("maxProductId", maxProductId);
        model.addAttribute("feedbackList", feedbackList);
        if (errorMessage != null){
            model.addAttribute("errorMessage", errorMessage);
            log.error(errorMessage);
        }
        errorMessage = null;
        return "user/boot1/detail";
    }

    @PostMapping("/{id}/feedback")
    public String feedback(@RequestParam String content, @RequestParam("numberOfStars") int numberOfStars, @RequestParam("_csrfParameterName") String csrfTokenForm, @PathVariable Integer id, Model model) {
        if (!csrfTokenForm.equals(csrfToken)) {
            String message = "Incorrect Anti-CSRF token code!";
            errorMessage = message;
            model.addAttribute("errorMessage", message);
            return "redirect:/product/" + id;
        }
        if (!inputService.isValidComment(content)){
            String message = "The comment content should only consist of lowercase letters, numbers, '@' symbol, parentheses, commas, periods, exclamation marks, and spaces.";
            errorMessage = message;
            model.addAttribute("errorMessage", message);
            return "redirect:/product/" + id;
        }
        Feedback feedback = new Feedback();
        feedback.setContent(content);
        feedback.setUser(null);
        feedback.setStars(numberOfStars);
        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = (User) authentication.getPrincipal();
        }
        model.addAttribute("user", user);
        feedback.setUser(user);
        feedback.setProduct(productService.getProductById(id));
        feedbackService.addFeedback(feedback);
        return "redirect:/product/" + id;
    }
    public String generateRandomString() {
        return UUID.randomUUID().toString();
    }
}