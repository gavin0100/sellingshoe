package com.data.filtro.model;

import com.data.filtro.model.payment.ApiOrderDTO;
import com.data.filtro.model.payment.OrderStatus;
import com.data.filtro.model.payment.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "dathang")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "madathang")
    private Integer id;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "makh", referencedColumnName = "makh")
    @JsonManagedReference
    private User user;

    @Column(name = "ngaydathang")
    private Date orderDate;

    @Column(name = "diachi")
    private String address;

    @Column(name = "zip")
    private Integer zip;

    @Column(name = "thanhpho")
    private String city;


    @Column(name = "SDT")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "tong")
    private Integer total;


    @Column(name = "tinhtrang")
    private Integer status;

    @Column(name = "trang_thai")
    @Enumerated(EnumType.STRING)
    private OrderStatus statusPayment;
    @Column(name = "phuongthucthanhtoan")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "order_code")
    private String order_code;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "orderId")
//    @JsonIgnore
    @JsonManagedReference
    private List<OrderDetail> orderDetails;

    public ApiOrderDTO convertToApiDTO(){
        return ApiOrderDTO.builder().
                id(this.id)
                .userId(this.user.getId())
                .orderDate(this.orderDate)
                .email(this.email)
                .phoneNumber(this.getPhoneNumber())
                .address(this.address)
                .city(this.city)
                .zip(this.zip)
                .paymentMethod(this.paymentMethod)
                .total(this.total)
                .statusPayment(this.statusPayment)
                .build();

    }

}
