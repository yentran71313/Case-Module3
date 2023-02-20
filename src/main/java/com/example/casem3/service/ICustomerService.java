package com.example.casem3.service;




import com.example.casem3.model.Customer;

import java.util.List;

public interface ICustomerService {
    List<Customer> selectCustomerPagging(int offset, int noOfRecords);
    int getNoOfRecords();
    List<Customer> getAllCustomer();

    Customer findCustomerById(Long id);
    void editCustomer(Customer customer);
    void deleteCustomerById(Long id);

    void createCustomer(Customer customer);

    boolean checkImageExists(String image);
    int getTotalCustomer();

    Customer findCustomerByUsernamePassword(String username,String password);
    List<Customer> getAllCustomerSearchingPagging(String kw, int idCustomerType, int offset, int limit);
    boolean checkUsername(String username);
}
