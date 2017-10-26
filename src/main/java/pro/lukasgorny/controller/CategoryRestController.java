package pro.lukasgorny.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.lukasgorny.model.Category;
import pro.lukasgorny.service.category.CategoryService;

import java.util.List;

/**
 * Created by Łukasz on 26.10.2017.
 */

@RestController
@RequestMapping("/category-rest")
public class CategoryRestController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryRestController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<Category>> getAll() {
        List<Category> list = categoryService.findByParentIsNull();
        ResponseEntity<List<Category>> responseEntity = new ResponseEntity<>(list, HttpStatus.OK);
        return responseEntity;
    }

}
