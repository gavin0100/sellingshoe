package com.data.filtro.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "user_permission")
@NoArgsConstructor
@AllArgsConstructor
public class UserPermission implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private int permissionId;
    //    @NotEmpty(message = "role name is required!")
    @Column(name = "role", length = 50)
    @Enumerated(EnumType.STRING)
    private Role role;
    //    @NotEmpty(message = "syllabus permission is required!")
    @Column(name = "category", length = 50)
    @Enumerated(EnumType.STRING)
    private Permission categoryManagement;
    //    @NotEmpty(message = "training program permission is required!")
    @Column(name = "order_management", length = 50)
    @Enumerated(EnumType.STRING)
    private Permission orderManagement;
    //    @NotEmpty(message = "class permission is required!")
    @Column(name = "product", length = 50)
    @Enumerated(EnumType.STRING)
    private Permission productManagement;
    //    @NotEmpty(message = "learning material permission is required!")
    @Column(name = "user_management", length = 50)
    @Enumerated(EnumType.STRING)
    private Permission userManagement;
    //    @NotEmpty(message = "user management permission is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "staff", length = 50)
    private Permission staffManagement;

    @Enumerated(EnumType.STRING)
    @Column(name = "material", length = 50)
    private Permission materialManagement;

    @Enumerated(EnumType.STRING)
    @Column(name = "place_order", length = 50)
    private Permission placeOrderManagement;

    @JsonIgnore
    @OneToMany(mappedBy = "userPermission", cascade = CascadeType.ALL)
    private List<User> users;
}
