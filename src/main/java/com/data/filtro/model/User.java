package com.data.filtro.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "khachhang")
@Data
@EqualsAndHashCode(exclude = {"cart"})
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "makh")
    private Integer id;

    @Column(name = "hoten")
    private String name;

    @Column(name = "ngaysinh")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dob;

    @Column(name = "gioitinh")
    private String sex;

    @Column(name = "diachi")
    private String address;

    @Column(name = "zip")
    private Integer zip;

    @Column(name = "thanhpho")
    private String city;

    @Column(name = "email")
    private String email;

    @Column(name = "sdt")
    private String phoneNumber;

    @Column(name = "tinhtrang")
    private Integer status;

    @ManyToOne
    @JoinColumn(name = "ma_vai_tro")
//    @JsonInclude(JsonInclude.Include.NON_NULL) // Include non-null fields
    private UserPermission userPermission;


    @Column(name = "tai_khoan")
    private String accountName;

    @Column(name = "mat_khau")
    private String password;

    @Column(name = "ngay_tao")
    private Date createdDate;

    @Column(name = "password_reset_token")
    private String passwordResetToken;



    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Invoice> invoices;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Order> orders;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_"+userPermission.getRole().name()),
                new SimpleGrantedAuthority(userPermission.getCategoryManagement() +"_CATEGORY"),
                new SimpleGrantedAuthority(userPermission.getOrderManagement()+ "_ORDER"),
                new SimpleGrantedAuthority(userPermission.getProductManagement()+ "_PRODUCT"),
                new SimpleGrantedAuthority(userPermission.getUserManagement()+ "_USER"),
                new SimpleGrantedAuthority(userPermission.getStaffManagement()+ "_STAFF"),
                new SimpleGrantedAuthority(userPermission.getMaterialManagement()+ "_MATERIAL"),
                new SimpleGrantedAuthority(userPermission.getPlaceOrderManagement()+ "_PLACE_ORDER")
        );
    }


    @Override
    public String getUsername() {
        return getAccountName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
