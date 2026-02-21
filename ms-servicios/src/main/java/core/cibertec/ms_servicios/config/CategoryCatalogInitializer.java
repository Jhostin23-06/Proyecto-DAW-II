package core.cibertec.ms_servicios.config;

import core.cibertec.ms_servicios.infrastructure.persistence.entity.ShipmentCategoryEntity;
import core.cibertec.ms_servicios.infrastructure.persistence.repository.ShipmentCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CategoryCatalogInitializer {

    private final ShipmentCategoryRepository shipmentCategoryRepository;

    @Bean
    CommandLineRunner initCategoryCatalog() {
        return args -> {
            List<ShipmentCategoryEntity> defaults = List.of(
                    new ShipmentCategoryEntity(1L, "DOCUMENTS"),
                    new ShipmentCategoryEntity(2L, "PACKAGES"),
                    new ShipmentCategoryEntity(3L, "HEAVY_CARGO"),
                    new ShipmentCategoryEntity(4L, "FRAGILE")
            );

            for (ShipmentCategoryEntity category : defaults) {
                shipmentCategoryRepository.findById(category.getCategoryId())
                        .or(() -> shipmentCategoryRepository.findByCategoryNameIgnoreCase(category.getCategoryName()))
                        .orElseGet(() -> shipmentCategoryRepository.save(category));
            }
        };
    }
}
