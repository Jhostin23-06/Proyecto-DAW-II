package core.cibertec.ms_servicios.application.service;

import core.cibertec.ms_servicios.application.port.outservice.CategoryPersistencePort;
import core.cibertec.ms_servicios.application.port.usecase.DeleteCategoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteCategoryServiceImpl implements DeleteCategoryPort {

    private final CategoryPersistencePort categoryPersistencePort;

    @Override
    public boolean deleteCategory(Long categoryId) {
        return categoryPersistencePort.deleteById(categoryId);
    }
}
