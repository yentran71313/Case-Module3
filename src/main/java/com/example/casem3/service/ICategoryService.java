package com.example.casem3.service;




import com.example.casem3.model.Category;

import java.util.List;

public interface ICategoryService {
    List<Category> getAllCategorys();

    Category getCategoryById(int id);
}
