package com.data.filtro.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "lienhe")
@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ten")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "chude")
    private String subject;

    @Column(name = "tinnhan")
    private String message;
}
