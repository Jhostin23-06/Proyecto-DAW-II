package core.cibertec.ms_servicios.infrastructure.persistence.adapter;

import core.cibertec.ms_servicios.application.port.outservice.CategoryPersistencePort;
import core.cibertec.ms_servicios.domain.bean.Category;
import core.cibertec.ms_servicios.infrastructure.persistence.entity.ShipmentCategoryEntity;
import core.cibertec.ms_servicios.infrastructure.persistence.repository.ShipmentRepository;
import core.cibertec.ms_servicios.infrastructure.persistence.repository.ShipmentCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryPersistencePort {

    private final ShipmentCategoryRepository shipmentCategoryRepository;
    private final ShipmentRepository shipmentRepository;

    @Override
    public List<Category> findAllCategories() {
        return shipmentCategoryRepository.findAll().stream()
                .sorted(Comparator.comparing(ShipmentCategoryEntity::getCategoryId))
                .map(e -> new Category(e.getCategoryId(), e.getCategoryName()))
                .toList();
    }

    @Override
    public Category createCategory(String categoryName) {
        shipmentCategoryRepository.findByCategoryNameIgnoreCase(categoryName.trim())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Category already exists: " + existing.getCategoryName());
                });

        long nextId = shipmentCategoryRepository.findAll().stream()
                .mapToLong(ShipmentCategoryEntity::getCategoryId)
                .max()
                .orElse(0L) + 1L;

        ShipmentCategoryEntity saved = shipmentCategoryRepository.save(
                new ShipmentCategoryEntity(nextId, categoryName.trim().toUpperCase())
        );
        return new Category(saved.getCategoryId(), saved.getCategoryName());
    }

    @Override
    public boolean existsById(Long categoryId) {
        return shipmentCategoryRepository.existsById(categoryId);
    }

    @Override
    public boolean deleteById(Long categoryId) {
        if (!shipmentCategoryRepository.existsById(categoryId)) {
            return false;
        }
        if (shipmentRepository.existsByCategoryId(categoryId)) {
            throw new IllegalStateException("Category is in use by one or more shipments");
        }
        shipmentCategoryRepository.deleteById(categoryId);
        return true;
    }
}
