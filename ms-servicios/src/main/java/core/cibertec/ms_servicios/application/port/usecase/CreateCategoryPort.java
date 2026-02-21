package core.cibertec.ms_servicios.application.port.usecase;

import core.cibertec.ms_servicios.domain.bean.Category;

public interface CreateCategoryPort {
    Category createCategory(String categoryName);
}
