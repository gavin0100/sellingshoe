package com.data.filtro.model.payment;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailDto {
        private Integer idProductDetail;
        private String productName;
        private String urlImage;
        private Integer quantity;
        private Integer price;
        private Integer total;

}
