package br.com.saga_orquestrado.inventory.core.service;

import br.com.saga_orquestrado.inventory.config.exception.ValidationException;
import br.com.saga_orquestrado.inventory.core.dto.Event;
import br.com.saga_orquestrado.inventory.core.dto.OrderProducts;
import br.com.saga_orquestrado.inventory.core.model.Inventory;
import br.com.saga_orquestrado.inventory.core.model.OrderInventory;
import br.com.saga_orquestrado.inventory.core.producer.KafkaProducer;
import br.com.saga_orquestrado.inventory.core.repository.InventoryRepository;
import br.com.saga_orquestrado.inventory.core.repository.OrderInventoryRepository;
import br.com.saga_orquestrado.inventory.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class InventoryService {

    private static final String CURRENT_SOURCE = "INVENTORY_SERVICE";

    private final JsonUtil jsonUtil;
    private final KafkaProducer producer;
    private final InventoryRepository inventoryRepository;
    private final OrderInventoryRepository orderInventoryRepository;

    public void updateInventory(Event event) {
        try {
            checkCurrentValidation(event);
            createOrderInventory(event);
//            var payment = findByOrderIdAndTransactionId(event);
//            validateAmount(payment.getTotalAmount());
//            changePaymentToSuccess(payment);
//            handleSuccess(event);
        } catch (Exception ex) {
            log.error("Error trying to update inventory: ", ex);
//            handleFailCurrentNotExecuted(event, ex.getMessage());
        }

        producer.sendEvent(jsonUtil.toJson(event));
    }

    private void checkCurrentValidation(Event event) {
        if (orderInventoryRepository.existsByOrderIdAndTransactionId(event.getPayload().getId(), event.getPayload().getTransactionId())) {
            throw new ValidationException("There's another transactionId for this validation.");
        }
    }

    private void createOrderInventory(Event event) {
        event
                .getPayload()
                .getProducts()
                .forEach(product -> {
                    var inventory = findInventoryByProductCode(product.getProduct().getCode());
                    var orderInventory = createOrderInventory(event, product, inventory);
                    orderInventoryRepository.save(orderInventory);
                });
    }

    private OrderInventory createOrderInventory(Event event, OrderProducts product, Inventory inventory) {
        return OrderInventory
                .builder()
                .inventory(inventory)
                .oldQuantity(inventory.getAvailable())
                .orderQuantity(product.getQuantity())
                .newQuantity(inventory.getAvailable() - product.getQuantity())
                .orderId(event.getPayload().getId())
                .transactionId(event.getTransactionId())
                .build();
    }

    private Inventory findInventoryByProductCode(String productCode) {
        return inventoryRepository
                .findByProductCode(productCode)
                .orElseThrow(() -> new ValidationException("Inventory not found by informed product."));
    }
}
