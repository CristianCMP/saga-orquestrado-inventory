package br.com.saga_orquestrado.inventory.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProducts {
    private Product product;
    private int quantity;
}
