package com.example.casem3.service.mysql;



import com.example.casem3.model.Category;
import com.example.casem3.service.ICategoryService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MSCategoryService extends  DBContext implements ICategoryService {
    private static final String SELLECT_ALL_CATEGORY = "SELECT * FROM category;";
    private static final String SELLECT_CATEGORY_BY_ID = "SELECT * FROM category where cid = ?";

    @Override
    public List<Category> getAllCategorys() {
        List<Category> categories = new ArrayList<>();
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SELLECT_ALL_CATEGORY);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Category c = getCategoryFromRs(rs);
                categories.add(c);
            }
            connection.close();
        } catch (SQLException e) {
            printSQLException(e);
        }
        return categories;
    }

    private Category getCategoryFromRs(ResultSet rs) throws SQLException {
        int id = rs.getInt("cid");
        String name = rs.getString("cname");

        Category category = new Category(id, name);
        return category;

    }

    @Override
    public Category getCategoryById(int id) {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SELLECT_CATEGORY_BY_ID);
            preparedStatement.setInt(1,id);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Category c = getCategoryFromRs(rs);
                return c;
            }

        } catch (SQLException e) {
            printSQLException(e);
        }
        return null;
    }
}
