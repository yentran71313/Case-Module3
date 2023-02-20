package com.example.casem3.service.mysql;


import com.example.casem3.model.Customer;
import com.example.casem3.service.ICustomerService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MSCustomerService extends DBContext implements ICustomerService {


    private static final String SELLECT_ALL_CUSTOMER = "SELECT * FROM customer";
    private static final String INSERT_CUSTOMER = "INSERT INTO `customer` (`username`, `password`, `name`, `email`, `phone`, `address`, `image`, `idrole`,`dob`) VALUES (?, ?,?, ?, ?,?,?,?,? )";
    private static final String DELETE_CUSTOMER_BY_ID = "DELETE FROM `customer` WHERE (`id` = ?);";
    private static final String FIND_CUSTOMER_BY_ID = "SELECT * FROM customer where id = ?;";
    private static final String EDIT_CUSTOMER = "UPDATE `customer` SET `username` = ?, `password` = ?, `name` = ?, `email` = ?, `phone` = ?, `address` = ?, `image` = ?, `idrole` = ?, `dob` = ? WHERE (`id` = ?);";
    private static final String CHECK_IMAGE_EXIST = "SELECT * customer where image = ?;";
    private static final String FIND_CUSTOMER_BY_USERNAME_AND_PASSWORD = "SELECT * from customer where username = ? and password = ?";
    private static final String SEARCHING_PAGGING_CUSTOMER = "SELECT SQL_CALC_FOUND_ROWS  * FROM customer where `name` like ? and idrole = ?  limit ?, ?";
    private static final String SEARCHING_PAGGING_CUSTOMER_ALL = "SELECT SQL_CALC_FOUND_ROWS  * FROM customer where `name` like ? limit ?, ?;";
    private int noOfRecords;


    @Override
    public List<Customer> selectCustomerPagging(int offset, int noOfRecords) {
        String query = "select SQL_CALC_FOUND_ROWS * from users limit "
                + offset + ", " + noOfRecords;
        List<Customer> list = new ArrayList<>();
        Customer customer = null;
        Connection connection = null;
        Statement stmt = null;
        try {
            connection  = getConnection();
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                customer = new Customer();
                customer.setId(rs.getLong("id"));
                customer.setUsername(rs.getString("username"));
                customer.setPassword(rs.getString("password"));
                customer.setName(rs.getString("name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setAddress(rs.getString("address"));
                customer.setImage(rs.getString("image"));
                customer.setIdRole(rs.getInt("idrole"));
                Date sqlCreatedAt = rs.getDate("date");
                java.util.Date uCreatedAt = new java.util.Date(sqlCreatedAt.getTime());
                customer.setCreatedAt(uCreatedAt);
                list.add(customer);
            }
            rs.close();

            rs = stmt.executeQuery("SELECT FOUND_ROWS()");
            if(rs.next())
                this.noOfRecords = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally
        {
            try {
                if(stmt != null)
                    stmt.close();
                if(connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
    public int getNoOfRecords(){
        return noOfRecords;
    }

    @Override
    public List<Customer> getAllCustomer() {

        List<Customer> customers = new ArrayList<>();
        Connection connection = getConnection();
        try {
//            PreparedStatement preparedStatement = connection.prepareStatement(SELLECT_ALL_CUSTOMER);

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SELLECT_ALL_CUSTOMER);
            while (rs.next()) {
                Customer c = getCustomerFromRs(rs);
                customers.add(c);
            }


            System.out.println("getAllCustomer: " + statement);
        } catch (SQLException e) {
            printSQLException(e);
        }
        return customers;
    }

    private Customer getCustomerFromRs(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        Date sqlCreatedAt = rs.getDate("dob");
        java.util.Date uCreatedAt = new java.util.Date(sqlCreatedAt.getTime());
        String address = rs.getString("address");
        String image = rs.getString("image");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String phone = rs.getString("phone");
        String email = rs.getString("email");

        int idRole = rs.getInt("idrole");
//        (Long id, String name, java.util.Date createdAt, String address, String email, String phone,
//                String username, String password, String image, int idRole)
        Customer customer = new Customer(id, name, uCreatedAt, address,email,phone,username,password, image,idRole);
        return customer;
    }


    @Override
    public Customer findCustomerById(Long id) {

        Connection connection = getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_CUSTOMER_BY_ID);
            preparedStatement.setLong(1, id);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Customer customer = getCustomerFromRs(rs);
                return customer;
            }

            connection.close();
        } catch (SQLException e) {
            printSQLException(e);
        }
        return null;
    }

    @Override
    public void editCustomer(Customer customer) {
        Connection connection = getConnection();

        try {
            //"UPDATE `customer` SET `name` = ?, `createdat` = ?,
            // `address` = ?, `image` =?, `idcustomertype` = ? WHERE (`id` = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(EDIT_CUSTOMER);
            preparedStatement.setString(1,customer.getUsername());
            preparedStatement.setString(2,customer.getPassword());
            preparedStatement.setString(3, customer.getName());
            preparedStatement.setString(4, customer.getEmail());
            preparedStatement.setString(5, customer.getPhone());
            preparedStatement.setDate(9, new Date(customer.getCreatedAt().getTime()));
            preparedStatement.setString(6, customer.getAddress());
            preparedStatement.setString(7, customer.getImage());
            preparedStatement.setInt(8, customer.getIdRole());
            preparedStatement.setLong(10, customer.getId());

            preparedStatement.executeUpdate();

            connection.close();
        } catch (SQLException sqlException) {
            printSQLException(sqlException);
        }
    }

    @Override
    public void deleteCustomerById(Long id) {

        Connection connection = getConnection();
        //DELETE FROM customer` WHERE (`id` = ?)
        try {
            PreparedStatement ps = connection.prepareStatement(DELETE_CUSTOMER_BY_ID);
            ps.setLong(1, id);

            ps.executeUpdate();
            // DELETE FROM customer` WHERE (`id` = 3)
            System.out.println("deleteCustomerById: " + ps);
        } catch (SQLException sqlException) {
            printSQLException(sqlException);
        }

    }

    @Override
    public void createCustomer(Customer customer) {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CUSTOMER);
//            "INSERT INTO `customer` ( `name`, `createdat`, `address`, `image`, `idcustomertype`) VALUES (?, ?,?, ?, ? )";
            preparedStatement.setString(1,customer.getUsername());
            preparedStatement.setString(2,customer.getPassword());
            preparedStatement.setString(3, customer.getName());
            preparedStatement.setString(4, customer.getEmail());
            preparedStatement.setString(5, customer.getPhone());
            preparedStatement.setDate(9, new Date(customer.getCreatedAt().getTime()));
            preparedStatement.setString(6, customer.getAddress());
            preparedStatement.setString(7, customer.getImage());
            preparedStatement.setInt(8, customer.getIdRole());
            preparedStatement.executeUpdate();
            System.out.println("createCustomer: " + preparedStatement);

        } catch (SQLException sqlException) {
            printSQLException(sqlException);
        }


    }

    @Override
    public boolean checkImageExists(String image) {
        Connection connection = getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(CHECK_IMAGE_EXIST);
            preparedStatement.setString(1, image);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                // khỏi cần đọc dòng này ra vì chỉ cần trả ra true/false
                return true;
            }

            connection.close();
        } catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }

    @Override
    public int getTotalCustomer() {
        String query = "SELECT COUNT(*) FROM customer";
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                return rs.getInt(1);
            }
        }catch (Exception e){

        }
        return 0;
    }

    @Override
    public Customer findCustomerByUsernamePassword(String username, String password) {
        Connection connection = getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_CUSTOMER_BY_USERNAME_AND_PASSWORD);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2,password);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Customer customer = getCustomerFromRs(rs);
                return customer;
            }
            connection.close();
        } catch (SQLException e) {
            printSQLException(e);
        }
        return null;
    }

    @Override
    public List<Customer> getAllCustomerSearchingPagging(String kw, int idCustomerType, int offset, int limit) {
        List<Customer> customers = new ArrayList<>();
        Connection connection = getConnection();

        try {
            PreparedStatement preparedStatement = null;
            if (idCustomerType == -1) {
                preparedStatement = connection.prepareStatement(SEARCHING_PAGGING_CUSTOMER_ALL);
                preparedStatement.setString(1, "%" + kw + "%");
                preparedStatement.setInt(2, offset);
                preparedStatement.setInt(3, limit);
            }else{
                preparedStatement = connection.prepareStatement(SEARCHING_PAGGING_CUSTOMER);
                preparedStatement.setString(1, "%" + kw + "%");
                preparedStatement.setInt(2, idCustomerType);
                preparedStatement.setInt(3, offset);
                preparedStatement.setInt(4, limit);
            }
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Customer c = getCustomerFromRs(rs);
                customers.add(c);
            }

            rs = preparedStatement.executeQuery("SELECT FOUND_ROWS()");
            while (rs.next()) {
                noOfRecords = rs.getInt(1);
            }
            connection.close();
        } catch (SQLException sqlException) {
            printSQLException(sqlException);
        }
        return customers;
    }

    @Override
    public boolean checkUsername(String username) {
        List<Customer> customers = getAllCustomer();
        for (Customer c: customers
             ) {
            if (c.getUsername()==username){
                return true;
            }
        }
        return false;
    }
}
