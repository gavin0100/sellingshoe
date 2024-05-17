package com.data.filtro.model.payment.momo;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MomoDeliveryInfo {
    private String deliveryAddress;
    private String deliveryFee;
    private String quantity;

}
