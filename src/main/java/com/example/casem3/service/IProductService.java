package com.example.casem3.service;




import com.example.casem3.model.Product;

import java.util.List;

public interface IProductService {
    List<Product> getAllProduct();
    List<Product> getAllProductSearchingPagging(String kw, int idCategory, int offset, int limit);
    int getNoOfRecords();
    void setNoOfRecords(int noOfRecords);
    Product findProductById(Long id);
    void editProduct(Product Product);
    void deleteProductById(Long id);

    void createProduct(Product Product);


    List<Product> getAllProductByCategoryId(int idCategory);

    boolean checkImageExists(String image);
}
