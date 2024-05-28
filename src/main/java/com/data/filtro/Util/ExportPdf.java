package com.data.filtro.Util;

import com.data.filtro.model.Order;
import com.data.filtro.model.OrderDetail;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Cell;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
public class ExportPdf {
    public static ByteArrayInputStream employeesReport(Order order, List<OrderDetail> orderDetailList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Thiết lập font
            FontProgram fontProgram1 = FontProgramFactory.createFont("src/main/resources/fonts/SVN-Times New Roman.ttf");
            FontProgram fontProgram2 = FontProgramFactory.createFont("src/main/resources/fonts/SVN-Times New Roman Bold.ttf");
            PdfFont font = PdfFontFactory.createFont(fontProgram1, "Identity-H", true);
            PdfFont boldFont = PdfFontFactory.createFont(fontProgram2, "Identity-H", true);

            // Thêm tiêu đề
            Paragraph title = new Paragraph("Shop bán giày Four Leave Shoes")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Thêm ngày in phiếu
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            Paragraph dateParagraph = new Paragraph("Thời gian in phiếu: " + now.format(formatter))
                    .setFont(font).setTextAlignment(TextAlignment.CENTER);
            document.add(dateParagraph);

            // Thêm thông tin hóa đơn
            Paragraph orderInfo = new Paragraph("Mã phiếu: " + order.getOrder_code() +
                    "\nKhách hàng: " + order.getUser().getName() + " - " + order.getUser().getAddress() +
                    "\nTổng thành tiền: " + order.getTotal() + "đ")
                    .setFont(font)
                    .setFontSize(12);
            document.add(orderInfo);

            // Tạo bảng
            float[] columnWidths = {4, 4, 4, 4, 4, 4};
            Table table = new Table(columnWidths);
            table.addCell(new Cell().add(new Paragraph("Mã hàng hóa").setFont(boldFont)));
            table.addCell(new Cell().add(new Paragraph("Tên hàng hóa").setFont(boldFont)));
            table.addCell(new Cell().add(new Paragraph("Giá").setFont(boldFont)));
            table.addCell(new Cell().add(new Paragraph("Số lượng").setFont(boldFont)));
            table.addCell(new Cell().add(new Paragraph("Giảm giá").setFont(boldFont)));
            table.addCell(new Cell().add(new Paragraph("Tổng").setFont(boldFont)));

            // Thêm dữ liệu vào bảng
            for (OrderDetail orderDetail : orderDetailList) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(orderDetail.getProduct().getId())).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(orderDetail.getProduct().getProductName()).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(orderDetail.getPrice()) + "đ").setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(orderDetail.getQuantity())).setFont(font)));
                table.addCell(new Cell().add(new Paragraph("0").setFont(font))); // Giả sử giảm giá là 0
                table.addCell(new Cell().add(new Paragraph(String.valueOf(orderDetail.getTotal()) + "đ").setFont(font)));
            }

            document.add(table);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
