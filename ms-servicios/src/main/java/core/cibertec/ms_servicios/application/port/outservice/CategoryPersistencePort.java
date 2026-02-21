package core.cibertec.ms_servicios.application.port.outservice;

import core.cibertec.ms_servicios.domain.bean.Category;

import java.util.List;

public interface CategoryPersistencePort {
    List<Category> findAllCategories();
    Category createCategory(String categoryName);
    boolean existsById(Long categoryId);
}
