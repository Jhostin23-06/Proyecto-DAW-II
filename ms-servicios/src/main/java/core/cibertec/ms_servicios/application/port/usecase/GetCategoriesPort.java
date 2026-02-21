package core.cibertec.ms_servicios.application.port.usecase;

import core.cibertec.ms_servicios.domain.bean.Category;

import java.util.List;

public interface GetCategoriesPort {
    List<Category> getAllCategories();
}
