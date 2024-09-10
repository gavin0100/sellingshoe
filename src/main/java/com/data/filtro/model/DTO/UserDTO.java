package com.data.filtro.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

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
