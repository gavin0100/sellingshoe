package com.data.filtro.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Entity
@Table(name = "danhmuc")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "madanhmuc")
    private Integer id;

    @Column(name = "tendanhmuc")
    private String categoryName;

    @Column(name = "tinhtrang")
    private Integer status;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @JsonIgnore
    @JsonManagedReference(value = "category-product")
    private List<Product> productList;

}
