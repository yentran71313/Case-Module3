package com.example.casem3.service.mysql;



import com.example.casem3.model.Customer;
import com.example.casem3.model.Order;
import com.example.casem3.model.OrderDTO;
import com.example.casem3.model.OrderItem;
import com.example.casem3.service.IOrderService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MSOrderService extends DBContext implements IOrderService {
    private static final String ADD_ORDER_ITEM = "INSERT INTO `order_item` (`id_order`, `id_product`,`quantity`) VALUES (?, ?,?)";
    private static final String SELLECT_ALL_ORDER = "SELECT * FROM `order`";
    private static final String ADD_ORDER = "INSERT INTO `order` (`address`, `phone`, `name`, `total`) VALUES (?, ?, ?, ?)";
    private static final String FIND_ORDER_BY_ID = "SELECT * FROM `ORDER` WHERE ID = ?";
    private static final String FIND_ORDERITEM_BY_ID = "SELECT * FROM order_item where id_order = ?;";

    @Override
    public List<Order> getAllOrder() {
        List<Order> orders = new ArrayList<>();
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SELLECT_ALL_ORDER);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                Order order = getOrderForm(rs);
                orders.add(order);
            }
            connection.close();
        } catch (SQLException e) {
            printSQLException(e);
        }
        return orders;
    }

    private Order getOrderForm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String address = rs.getString("address");
        String phone = rs.getString("phone");
        String name = rs.getString("name");
        Double total = rs.getDouble("total");
        return new Order(id,name,phone,address,total);
    }

    @Override
    public Order getOrderById(long id) {
        Connection connection = getConnection();
        Order order = null;
        List<OrderItem> orderItems = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ORDER_BY_ID);
            preparedStatement.setLong(1, id);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                order = getOrderForm(rs);
            }

            PreparedStatement preparedStatement1 = connection.prepareStatement(FIND_ORDERITEM_BY_ID);
            preparedStatement1.setLong(1,id);
            ResultSet resultSet = preparedStatement1.executeQuery();
            while (resultSet.next()){
                OrderItem orderItem = new OrderItem(resultSet.getLong("id_order"),
                        resultSet.getLong("id_product"),
                        resultSet.getInt("quantity"));
                orderItems.add(orderItem);
            }
            order.setOrderItems(orderItems);

            connection.close();
        } catch (SQLException e) {
            printSQLException(e);
        }
        return order;
    }

    @Override
    public void saveOrder(Order order) {
        Connection connection = getConnection();
//        String ADD_ORDER = "INSERT INTO `c10_qlykhachhang`.`order` (`address`, `phone`, `name`, `total`)
//        VALUES ('28 NTP', '3456789', 'Quang', '12');\n";
        try {
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(ADD_ORDER);
            preparedStatement.setString(1, order.getAddress());
            preparedStatement.setString(2, order.getPhone());
            preparedStatement.setString(3, order.getName());
            preparedStatement.setDouble(4, order.total());
            preparedStatement.executeUpdate();


            ResultSet rs = preparedStatement.executeQuery("SELECT LAST_INSERT_ID()");
            while (rs.next()) {
                order.setId(rs.getLong(1));
            }
            for (int i = 0; i < order.getOrderItems().size(); i++) {
                //INSERT INTO `c10_qlykhachhang`.`order_item` (`id_order`, `id_product`) VALUES (?, ?)";
                PreparedStatement ps = connection.prepareStatement(ADD_ORDER_ITEM);
                ps.setLong(1, order.getId());
                ps.setLong(2, order.getOrderItems().get(i).getIdProduct());
                ps.setInt(3,order.getOrderItems().get(i).getQuantiy());
                ps.executeUpdate();
            }
            connection.commit();
        } catch (SQLException sqlException) {
            printSQLException(sqlException);
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }



    }
}
