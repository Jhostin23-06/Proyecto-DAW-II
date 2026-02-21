package core.cibertec.ms_servicios.infrastructure.controller;

import core.cibertec.ms_servicios.application.port.usecase.GetStatusesPort;
import core.cibertec.ms_servicios.application.port.usecase.CreateStatusPort;
import core.cibertec.ms_servicios.application.port.usecase.DeleteStatusPort;
import core.cibertec.ms_servicios.application.port.usecase.GetCategoriesPort;
import core.cibertec.ms_servicios.application.port.usecase.CreateCategoryPort;
import core.cibertec.ms_servicios.application.port.usecase.DeleteCategoryPort;
import core.cibertec.ms_servicios.domain.bean.Category;
import core.cibertec.ms_servicios.domain.bean.CreateCategoryRequest;
import core.cibertec.ms_servicios.domain.bean.CreateStatusRequest;
import core.cibertec.ms_servicios.domain.bean.Status;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CatalogController {

    private final GetStatusesPort getStatusesPort;
    private final CreateStatusPort createStatusPort;
    private final DeleteStatusPort deleteStatusPort;
    private final GetCategoriesPort getCategoriesPort;
    private final CreateCategoryPort createCategoryPort;
    private final DeleteCategoryPort deleteCategoryPort;

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(getCategoriesPort.getAllCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        Category created = createCategoryPort.createCategory(request.getCategoryName());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        boolean deleted = deleteCategoryPort.deleteCategory(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<Status>> getStatuses() {
        return ResponseEntity.ok(getStatusesPort.getAllStatuses());
    }

    @PostMapping("/statuses")
    public ResponseEntity<Status> createStatus(@Valid @RequestBody CreateStatusRequest request) {
        Status created = createStatusPort.createStatus(request.getStatusName());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/statuses/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        boolean deleted = deleteStatusPort.deleteStatus(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
