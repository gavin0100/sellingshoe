package com.data.filtro.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "emailcode")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailValidCode {
    @Id
    @Column(name = "email")
    private String email;

    @Column(name = "maxacthuc")
    private String validCode;
}
