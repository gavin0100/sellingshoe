package com.data.filtro.controller.user;

import com.data.filtro.model.Contact;
import com.data.filtro.model.User;
import com.data.filtro.service.ContactService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public String loadContactPage(Model model){

        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = (User) authentication.getPrincipal();
        }
        model.addAttribute("user", user);
        return "user/boot1/contact";
    }

    @PostMapping
    public String addContact(@RequestParam("name") String name,
                             @RequestParam("email") String email,
                             @RequestParam("subject") String subject,
                             @RequestParam("message") String message,
                             Model model) {

        Contact contact = new Contact();
        contact.setName(name);
        contact.setEmail(email);
        contact.setSubject(subject);
        contact.setMessage(message);
        contactService.createContact(contact);
        return "redirect:/contact";
    }
}
