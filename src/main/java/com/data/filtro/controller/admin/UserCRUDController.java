package com.data.filtro.controller.admin;

import com.data.filtro.model.Account;
import com.data.filtro.model.DTO.UserDTO;
import com.data.filtro.model.User;
import com.data.filtro.service.AccountService;
import com.data.filtro.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/user")
public class UserCRUDController {

    @Autowired
    UserService userService;


    public Pageable sortUser(int currentPage, int pageSize, int sortType) {
        Pageable pageable;
        switch (sortType) {
            case 5:
            case 10:
            case 25:
            case 50:
                pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("name"));
                break;
            default:
                pageSize = 5;
                pageable = PageRequest.of(currentPage - 1, pageSize);
                break;
        }
        return pageable;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_USER', 'VIEW_USER')")
    public String show(@RequestParam(defaultValue = "5") int sortType, @RequestParam("currentPage") Optional<Integer> page, Model model, HttpSession session) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        int currentPage = page.orElse(1);
        int pageSize = sortType;
        List<User> usableAccounts = userService.getAppropriateAccountForUser();
        Pageable pageable;
        Page<User> userPage;
        pageable = sortUser(currentPage, pageSize, sortType);
        userPage = userService.getAllPagingUser(pageable);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("sortType", sortType);
        model.addAttribute("totalElements", userPage.getTotalElements());
        model.addAttribute("usableAccounts", usableAccounts);
        return "admin/boot1/user";
    }

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority('FULL_ACCESS_USER', 'VIEW_USER')")
    public void exportFileCSV(HttpServletResponse response, Model model, HttpSession session) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".csv";
        response.setHeader(headerKey, headerValue);

        // Include the UTF-8 Byte Order Mark (BOM)
        Writer writer = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
        writer.write('\ufeff'); // Write the BOM to the file

        List<User> listUsers = userService.getAppropriateAccountForUser();
        ICsvBeanWriter csvWriter = new CsvBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"Name", "Date of birth", "Sex", "Address", "Zip", "City", "Email", "Phone number", "Account name", "Created date"};
        String[] nameMapping = {"name", "dob", "sex", "address", "zip", "city", "email", "phoneNumber", "accountName", "createdDate"};

        csvWriter.writeHeader(csvHeader);

        CellProcessor[] processors = new CellProcessor[] {
                new org.supercsv.cellprocessor.Optional(), // Name
                new org.supercsv.cellprocessor.Optional(new FmtDate("yyyy-MM-dd")) , // Date of birth
                new org.supercsv.cellprocessor.Optional(), // Sex
                new org.supercsv.cellprocessor.Optional(),
                new org.supercsv.cellprocessor.Optional(),
                new org.supercsv.cellprocessor.Optional(),
                new org.supercsv.cellprocessor.Optional(),
                new org.supercsv.cellprocessor.Optional(),
                new org.supercsv.cellprocessor.Optional(),
                new org.supercsv.cellprocessor.Optional(new FmtDate("yyyy-MM-dd"))
        };

        for (User user : listUsers) {
            csvWriter.write(user, nameMapping, processors);
        }

        writer.flush(); // Flush the writer to ensure all data is written to the stream
        csvWriter.close(); // Close the csvWriter
        writer.close(); // Close the writer

    }


    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority( 'FULL_ACCESS_USER')")
    public String create(@ModelAttribute UserDTO user) {
        userService.create(user);
        return "redirect:/admin/user";
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority( 'FULL_ACCESS_USER')")
    public String update(@ModelAttribute UserDTO user) {
        userService.update(user);
        return "redirect:/admin/user";
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_STAFF', 'ACCOUNTING_STAFF') and hasAnyAuthority( 'FULL_ACCESS_USER')")
    public String delete(@RequestParam("id") int id) {
        User user = userService.getByUserId(id);
        userService.deleteById(id);
        return "redirect:/admin/user";
    }
}
