package core.cibertec.ms_servicios.application.service;

import core.cibertec.ms_servicios.application.port.outservice.CategoryPersistencePort;
import core.cibertec.ms_servicios.application.port.usecase.GetCategoriesPort;
import core.cibertec.ms_servicios.domain.bean.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetCategoriesServiceImpl implements GetCategoriesPort {

    private final CategoryPersistencePort categoryPersistencePort;

    @Override
    public List<Category> getAllCategories() {
        return categoryPersistencePort.findAllCategories();
    }
}
