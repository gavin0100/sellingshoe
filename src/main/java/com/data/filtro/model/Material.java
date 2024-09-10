package com.data.filtro.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Entity
@Table(name = "vatlieu")
@Data
@AllArgsConstructor
@Component
@NoArgsConstructor
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mavatlieu")
    private Integer id;

    @Column(name = "tenvatlieu")
    private String materialName;

    @Column(name = "mota")
    private String description;

    @Column(name = "tinhtrang")
    private Integer status;

    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "material-product")
    private List<Product> products;

}
