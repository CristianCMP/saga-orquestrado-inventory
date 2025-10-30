package br.com.saga_orquestrado.inventory.core.repository;

import br.com.saga_orquestrado.inventory.core.model.OrderInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderInventoryRepository extends JpaRepository<OrderInventory, Integer> {
    boolean existsByOrderIdAndTransactionId(String orderId, String transactionId);
    List<OrderInventory> findAllByOrderIdAndTransactionId(String orderId, String transactionId);
}
