package com.example.casem3.service.mysql;



import com.example.casem3.model.Role;
import com.example.casem3.service.IRoleService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MSRoleService extends DBContext implements IRoleService {
    private static final String SELLECT_ALL_CUSTOMER_TYPE = "SELECT * FROM role;";
    private static final String SELECT_ROLE_BY_ID = "SELECT * FROM role where id = ?";
    @Override
    public List<Role> getAllRole() {
        List<Role> roles = new ArrayList<>();
        Connection connection = getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SELLECT_ALL_CUSTOMER_TYPE);
            while (rs.next()) {
                Role c = getRoleFromRs(rs);
                roles.add(c);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return roles;
    }

    private Role getRoleFromRs(ResultSet rs) throws SQLException {
        Role role = new Role();
        int id = rs.getInt("id");
        String name = rs.getString("name");

        role.setId(id);
        role.setName(name);
        return role;
    }

    @Override
    public Role getRoleById(int id) {
        Connection connection = getConnection();
        try {
//            PreparedStatement preparedStatement = connection.prepareStatement(SELLECT_ALL_CUSTOMER);

            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ROLE_BY_ID);
            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Role c = getRoleFromRs(rs);
                return c;
            }

            System.out.println("getCustomerTypeById: " + preparedStatement);
        } catch (SQLException e) {
            printSQLException(e);
        }
        return null;
    }
}
