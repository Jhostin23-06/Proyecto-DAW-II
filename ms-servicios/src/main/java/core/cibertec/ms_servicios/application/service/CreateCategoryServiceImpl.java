package core.cibertec.ms_servicios.application.service;

import core.cibertec.ms_servicios.application.port.outservice.CategoryPersistencePort;
import core.cibertec.ms_servicios.application.port.usecase.CreateCategoryPort;
import core.cibertec.ms_servicios.domain.bean.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCategoryServiceImpl implements CreateCategoryPort {

    private final CategoryPersistencePort categoryPersistencePort;

    @Override
    public Category createCategory(String categoryName) {
        return categoryPersistencePort.createCategory(categoryName);
    }
}
