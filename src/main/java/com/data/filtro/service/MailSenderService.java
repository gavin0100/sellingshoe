package com.data.filtro.service;

import com.data.filtro.model.Order;
import com.data.filtro.model.OrderDetail;
import com.data.filtro.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

@Service
public class MailSenderService {
    @Autowired
    UserService userService;

    public void sendEmailGetPassword(String to, String from, String host, String subject, String matKhauMoi) {
        // Get system properties
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);


        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                return new  PasswordAuthentication("voduc0100@gmail.com", "arozojkhspxuuxeg");
            }
        });

        try {
            // Create a default MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message
//            message.setText(text);

//            String htmlMessage = buildHtmlBill(customerName, items);
            String htmlMessage = buildHtmlBill4(matKhauMoi);
//            message.setContent(htmlMessage, "text/html");
            message.setContent(htmlMessage, "text/html; charset=UTF-8");
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public String buildHtmlBill4(String matKhauMoi) {
        // Use StringBuilder to create the HTML content
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset='UTF-8'></head><body style='font-family: Arial, sans-serif; background-color: #f7f7f7; color: #333;'>");
        sb.append("<div style='max-width: 600px; margin: auto; background-color: #fff; padding: 20px; border: 1px solid #ddd;'>");
        sb.append("<h2 style='text-align: center; color: #4A90E2;'>Shop bán giày Four Leave Shoes</h2>");
        sb.append("<p style='text-align: center;'>Thời gian in:" + String.valueOf(localDateParseMethod(LocalDateTime.now())) +"</p>");
        sb.append("<h1 style='background-color: #4A90E2; color: #fff; padding: 10px; text-align: center;'>MẬT KHẨU MỚI</h1>");
        sb.append("<p>Mật khẩu:   " + String.valueOf(matKhauMoi) + "</p>");

        return sb.toString();
    }

    public String localDateParseMethod(LocalDateTime ngayLam){
        String formattedNgayLam = ngayLam.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return formattedNgayLam;
    }


    public void sendHoaDon(String to, String from, String host, String subject, Order hoaDon, List<OrderDetail> danhSachHoaDonChiTiet ) {
        // Get system properties
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);


        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                return new  PasswordAuthentication("voduc0100@gmail.com", "arozojkhspxuuxeg");
            }
        });

        try {
            // Create a default MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message
//            message.setText(text);

//            String htmlMessage = buildHtmlBill(customerName, items);
            String htmlMessage = buildHtmlBillHoaDon(hoaDon, danhSachHoaDonChiTiet);
//            message.setContent(htmlMessage, "text/html");
            message.setContent(htmlMessage, "text/html; charset=UTF-8");
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public String buildHtmlBillHoaDon(Order hoaDon, List<OrderDetail> danhSachHoaDonChiTiet) {
        // Use StringBuilder to create the HTML content
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset='UTF-8'></head><body style='font-family: Arial, sans-serif; background-color: #f7f7f7; color: #333;'>");
        sb.append("<div style='max-width: 600px; margin: auto; background-color: #fff; padding: 20px; border: 1px solid #ddd;'>");
        sb.append("<h2 style='text-align: center; color: #4A90E2;'>Shop bán giày Four Leave Shoes</h2>");
        sb.append("<p style='text-align: center;'>Thời gian in phiếu:" + String.valueOf(localDateParseMethod(LocalDateTime.now())) +"</p>");
        sb.append("<h1 style='background-color: #4A90E2; color: #fff; padding: 10px; text-align: center;'>HÓA ĐƠN</h1>");
        sb.append("<p>Mã phiếu:" + String.valueOf(hoaDon.getOrder_code()) + "</p>");
        User khachHang = khachHang = userService.getByUserId(hoaDon.getUser().getId());

        sb.append("<p>Khách hàng: " + khachHang.getName() + " - " + khachHang.getAddress()+ "</p>");
        sb.append("<p>Tổng thành tiền: <strong>" + String.valueOf(hoaDon.getTotal()) + "đ</strong></p>");
        sb.append("<br>");

        sb.append("<table style='width: 100%; border-collapse: collapse; border: 1px solid #ddd; background-color: #fff;'>");


        sb.append("<tr style='background-color: #4A90E2; color: #fff;'>");
        sb.append("<th style='padding: 8px; border: 1px solid #ddd;'>Mã hàng hóa</th>");
        sb.append("<th style='padding: 8px; border: 1px solid #ddd;'>Tên hàng hóa</th>");
        sb.append("<th style='padding: 8px; border: 1px solid #ddd;'>Giá</th>");
        sb.append("<th style='padding: 8px; border: 1px solid #ddd;'>Số lượng</th>");
        sb.append("<th style='padding: 8px; border: 1px solid #ddd;'>Giảm giá</th>");
        sb.append("<th style='padding: 8px; border: 1px solid #ddd;'>Tổng</th>");
        sb.append("</tr>");

        for(int i =0; i< danhSachHoaDonChiTiet.size(); i++){
            sb.append("<tr>");
            sb.append("<td style='padding: 8px; border: 1px solid #ddd;'>" + danhSachHoaDonChiTiet.get(i).getProduct().getId() + "</td>");
            sb.append("<td style='padding: 8px; border: 1px solid #ddd;'>" + danhSachHoaDonChiTiet.get(i).getProduct().getProductName()+ "</td>");
            sb.append("<td style='padding: 8px; border: 1px solid #ddd;'>" + String.valueOf(danhSachHoaDonChiTiet.get(i).getPrice()) + "đ</td>");
            sb.append("<td style='padding: 8px; border: 1px solid #ddd;'>" + String.valueOf(danhSachHoaDonChiTiet.get(i).getTotal()) + "</td>");
            sb.append("<td style='padding: 8px; border: 1px solid #ddd;'>" + String.valueOf(0) + "</td>");
            sb.append("<td style='padding: 8px; border: 1px solid #ddd;'>" + String.valueOf(danhSachHoaDonChiTiet.get(i).getTotal()) + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        sb.append("<br>");

        sb.append("<div style='width: 100%; overflow: auto; margin-bottom: 50px;'>"); // Container for the three signature blocks


        sb.append("</div>");
        sb.append("</body></html>");

        return sb.toString();
    }
}
