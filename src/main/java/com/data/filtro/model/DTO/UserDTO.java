package com.data.filtro.model.DTO;

import com.data.filtro.model.Cart;
import com.data.filtro.model.Invoice;
import com.data.filtro.model.Order;
import com.data.filtro.model.UserPermission;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO implements Serializable {
    private Integer id;
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dob;

    private String sex;

    private String address;

    private Integer zip;

    private String city;

    private String email;

    private String phoneNumber;

    private Integer status;


    private int userPermissionId;

    private String accountName;
    private String password;

}
