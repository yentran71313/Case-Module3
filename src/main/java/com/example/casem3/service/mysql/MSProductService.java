package com.example.casem3.service.mysql;


import com.example.casem3.model.Product;
import com.example.casem3.service.IProductService;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MSProductService extends DBContext implements IProductService {
    private static final String SELLECT_ALL_PRODUCT = "SELECT * FROM product;";
    private static final String FIND_PRODUCT_BY_ID = "SELECT * FROM product where id = ?;";
    private static final String EDIT_PRODUCT = "UPDATE `product` SET `name` = ?, `price` = ?, `description` = ?, `image` = ?, `create_at` = ?, `category_id` = ? WHERE (`id` = ?);";
    private static final String SELLECT_ALL_PRODUCT_BY_ID = "SELECT * FROM product where cateID = ?";
    private static final String CHECK_IMAGE_EXIST = "SELECT * product where image = ?;";
    private static final String SEARCHING_PAGGING_PRODUCT = "SELECT SQL_CALC_FOUND_ROWS  * FROM product where `name` like ? and cateID = ?  limit ?, ?";

    private static final String INSERT_PRODUCT = "INSERT INTO `product` (`name`, `image`, `price`, `title`, `description`, `cateID`, `date`) VALUES (?, ?, ?, ?, ?, ?, ?);";
    private static final String SEARCHING_PAGGING_PRODUCT_ALL = "SELECT SQL_CALC_FOUND_ROWS * FROM PRODUCT WHERE `NAME` LIKE ? LIMIT ?,?";
    private int noOfRecords;

    public int getNoOfRecords() {
        return noOfRecords;
    }

    public void setNoOfRecords(int noOfRecords) {
        this.noOfRecords = noOfRecords;
    }

    @Override
    public List<Product> getAllProduct() {
        List<Product> products = new ArrayList<>();
        Connection connection = getConnection();
        try {
//            PreparedStatement preparedStatement = connection.prepareStatement(SELLECT_ALL_CUSTOMER);

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SELLECT_ALL_PRODUCT);
            while (rs.next()) {
                Product c = getProductFromRs(rs);
                products.add(c);
            }
            connection.close();
        } catch (SQLException e) {
            printSQLException(e);
        }
        return products;
    }

    @Override
    public List<Product> getAllProductSearchingPagging(String kw, int idCategory, int offset, int limit) {
        List<Product> products = new ArrayList<>();
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = null;
            if(idCategory == -1){
                preparedStatement = connection.prepareStatement(SEARCHING_PAGGING_PRODUCT_ALL);
                preparedStatement.setString(1,"%" + kw + "%");
                preparedStatement.setInt(2,offset);
                preparedStatement.setInt(3,limit);
            }else {
                preparedStatement = connection.prepareStatement(SEARCHING_PAGGING_PRODUCT);
                preparedStatement.setString(1,"%" + kw + "%");
                preparedStatement.setInt(2, idCategory);
                preparedStatement.setInt(3, offset);
                preparedStatement.setInt(4, limit);
            }
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                Product product = getProductFromRs(rs);
                products.add(product);
            }
            rs = preparedStatement.executeQuery("Select found_rows()");
            while (rs.next()){
                noOfRecords = rs.getInt(1);
            }
            connection.close();
        }catch (Exception e){

        }
        return products;
    }


    private Product getProductFromRs(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        double price = rs.getDouble("price");
        java.sql.Date sqlCreatedAt = rs.getDate("date");
        Date uCreatedAt = new Date(sqlCreatedAt.getTime());
        String image = rs.getString("image");
        int idCategory = rs.getInt("cateID");
        String title = rs.getString("title");

        //long id, String name, double price, String description, String image, Date createAt, int idCategory
        Product product = new Product(id, name, price, description, image, uCreatedAt, idCategory,title);
        return product;

    }

    @Override
    public Product findProductById(Long id) {
        Connection connection = getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_PRODUCT_BY_ID);
            preparedStatement.setLong(1, id);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Product product = getProductFromRs(rs);
                return product;
            }

            connection.close();
        } catch (SQLException e) {
            printSQLException(e);
        }
        return null;
    }

    @Override
    public void editProduct(Product Product) {
//        Connection connection = getConnection();
//
//        try {
//            //"UPDATE `customer` SET `name` = ?, `createdat` = ?,
//            // `address` = ?, `image` =?, `idcustomertype` = ? WHERE (`id` = ?)";
//            PreparedStatement preparedStatement = connection.prepareStatement(EDIT_PRODUCT);
//            preparedStatement.setString(1, customer.getName());
//            preparedStatement.setDate(2, new java.sql.Date(customer.getCreatedAt().getTime()));
//            preparedStatement.setString(3, customer.getAddress());
//            preparedStatement.setString(4, customer.getImage());
//            preparedStatement.setInt(5, customer.getIdType());
//            preparedStatement.setLong(6, customer.getId());
//
//            preparedStatement.executeUpdate();
//
//            connection.close();
//        } catch (SQLException sqlException) {
//            printSQLException(sqlException);
//        }
    }

    @Override
    public void deleteProductById(Long id) {

    }

    @Override
    public void createProduct(Product product) {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PRODUCT);
//            "INSERT INTO `customer` ( `name`, `createdat`, `address`, `image`, `idcustomertype`) VALUES (?, ?,?, ?, ? )";
            preparedStatement.setString(1,product.getName());
            preparedStatement.setString(2,product.getImage());
            preparedStatement.setString(4, product.getTitle());
            preparedStatement.setDouble(3,product.getPrice());
            preparedStatement.setString(5, product.getDescription());
            preparedStatement.setInt(6, product.getIdCategory());
            preparedStatement.setDate(7, new java.sql.Date(product.getCreateAt().getTime()));
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException sqlException) {
            printSQLException(sqlException);
        }
    }

    @Override
    public List<Product> getAllProductByCategoryId(int idCategory) {
        List<Product> products = new ArrayList<>();
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SELLECT_ALL_PRODUCT_BY_ID);
            preparedStatement.setInt(1, idCategory);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Product c = getProductFromRs(rs);
                products.add(c);
            }
            connection.close();
        } catch (SQLException e) {
            printSQLException(e);
        }
        return products;
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
}
