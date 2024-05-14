package com.data.filtro.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "nhanvien")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Staff implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manv")
    private Integer id;

    @Column(name = "hoten")
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngaysinh")
    private Date dob;

    @Column(name = "gioitinh")
    private String sex;

    @Column(name = "sdt")
    private String phoneNumber;


    @Column(name = "tinhtrang")
    private Integer status;

    @Column(name = "tai_khoan")
    private String accountName;

    @Column(name = "mat_khau")
    private String password;

    @Column(name = "ngay_tao")
    private Date createdDate;

    @Column(name = "ma_vai_tro")
    private Integer roleNumber;

    @Column(name = "password_reset_token")
    private String passwordResetToken;
}
