package com.data.filtro.model.payment.vnpay;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VNPResponse {
    private String status;
    private String message;
    private String paymentUrl;
}
