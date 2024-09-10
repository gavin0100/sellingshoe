package com.data.filtro.model;

import com.data.filtro.model.payment.OrderDetailDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "dathang_chitiet")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "madathang", referencedColumnName = "madathang")
    @JsonIgnore
    private Order orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "masp", referencedColumnName = "masp")
    private Product product;

    @Column(name = "soluong")
    private Integer quantity;

    @Column(name = "giatien")
    private Integer price;

    @Column(name = "tong")
    private Integer total;

    public OrderDetailDto convertToDto(){
        return OrderDetailDto.builder()
                .idProductDetail(this.id)
                .productName(this.getProduct().getProductName())
                .urlImage(this.getProduct().getImage())
                .quantity(this.quantity)
                .price(this.price)
                .total(this.total)
                .build();
    }

}
