package com.data.filtro.model.payment;

import lombok.*;

import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiOrderDTO {
    private Integer id;
    private Integer userId;

    private Date orderDate;

    private String email;
    private String phoneNumber;

    private String address;

    private String city;
    private Integer zip;
    private PaymentMethod paymentMethod;
    private Integer total;
    private OrderStatus statusPayment;
}
