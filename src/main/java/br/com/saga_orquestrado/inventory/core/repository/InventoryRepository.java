package br.com.saga_orquestrado.inventory.core.repository;

import br.com.saga_orquestrado.inventory.core.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    Optional<Inventory> findByProductCode(String productCode);
}
