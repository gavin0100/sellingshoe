package com.data.filtro.controller;

import com.data.filtro.Util.Utility;
import com.data.filtro.exception.UserNotFoundException;
import com.data.filtro.model.User;
import com.data.filtro.service.MailSenderService;
import com.data.filtro.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordResetController {
    public static int statusAPIResetPassword;

    private final MailSenderService mailSenderService;

    private final UserService userService;

    private final JavaMailSender mailSender;

    public PasswordResetController(MailSenderService mailSenderService, UserService userService, JavaMailSender mailSender) {
        this.mailSenderService = mailSenderService;
        this.userService = userService;
        this.mailSender = mailSender;
    }

    @GetMapping("/forgot-password")
    public String showForgotPassword() {

        return "user/boot1/forgotPassword";
    }


    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, HttpServletRequest request, Model model) {
//        String token = UUID.randomUUID().toString();
        try {
//            userService.updateResetPasswordToken(token, email);
//            String resetPasswordLink = Utility.getSiteURL(request) + "/reset-password?token=" + token;
//            sendMail(email, resetPasswordLink);
            String to = email;

            // Sender's email ID needs to be mentioned
            String from = "voduc0100@gmail.com";

            // Assuming you are sending email from localhost
            String host = "smtp.gmail.com";

            // Subject
            String subject = "SHOP BÁN GIÀY FOUR LEAVES SHOE - ĐẶT LẠI MẬT KHẨU CHO TÀI KHOẢN!";
            String newPassword = Utility.getRandomString();
            User user = userService.getUserByEmail(email);
            if (user == null){
                throw new UserNotFoundException("Email chưa đăng ký tài khoản!");
            }
            userService.updatePassword(user, newPassword);
            mailSenderService.sendEmailGetPassword(to, from, host, subject, newPassword );
            model.addAttribute("successMessage", "Mật khẩu mới đã được gửi tới email của bạn.!");
        } catch (UserNotFoundException ex) {
            model.addAttribute("message", ex.getMessage());
        } catch (Exception ex) {
            model.addAttribute("message", "Không thể gửi mail!");
        }
        return "user/boot1/forgotPassword";
    }


//    public void sendMail(String recipentAddress, String link) throws MessagingException, UnsupportedEncodingException {
//        JavaMailSender mailSender = new JavaMailSenderImpl();
//        ((JavaMailSenderImpl) mailSender).setHost("smtp.gmail.com");
//        ((JavaMailSenderImpl) mailSender).setPort(587);
//        ((JavaMailSenderImpl) mailSender).setUsername("voduc0100@gmail.com");
//        ((JavaMailSenderImpl) mailSender).setPassword("tvhnwfasjnjhbcro");
//        System.out.println("tao doi tuong MimeMessage");
//        MimeMessage message = mailSender.createMimeMessage();
//        System.out.println("gan gia tri helper");
//        MimeMessageHelper helper = new MimeMessageHelper(message);
//        helper.setFrom("contact@ark.com", "Arkadian");
//        helper.setTo(recipentAddress);
//        String subject = "Reset password Four Leaves Shoes";
//        String content = "\"The link below is used to reset the password\n" + link;
//        helper.setSubject(subject);
//        helper.setText(content);
//        System.out.println("gan xong gia tri helper");
//        System.out.println("gui gmail");
//        mailSender.send(message);
//    }
//
//    @GetMapping("/reset-password")
//    public String showPasswordReset(@RequestParam(value = "token") String token, Model model) {
//        statusAPIResetPassword = 1;
//        User user = userService.getByPasswordResetToken(token);
//        model.addAttribute("token", token);
//        if (user == null) {
//            model.addAttribute("message", "Token không hợp lệ");
//            return "user/boot1/passwordReset";
//        }
//        return "user/boot1/passwordReset";
//    }
//
//
//    @PostMapping("/reset-password")
//    public String processPasswordReset(@RequestParam("token") String token,
//                                       @RequestParam("password") String password,
//                                       Model model) {
//        User user = userService.getByPasswordResetToken(token);
//        if (user == null) {
//            model.addAttribute("message", "Token không hợp lệ");
//            return "user/boot1/passwordReset";
//        } else {
//            userService.updatePassword(user, password);
//            model.addAttribute("message", "Đổi mật khẩu thành công!");
//        }
//        return "user/boot1/passwordReset";
//    }


}
