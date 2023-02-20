package com.example.casem3.controller;


import com.example.casem3.config.ResourceConfig;
import com.example.casem3.model.*;
import com.example.casem3.service.ICategoryService;
import com.example.casem3.service.IOrderService;
import com.example.casem3.service.IProductService;
import com.example.casem3.service.mysql.MSCategoryService;
import com.example.casem3.service.mysql.MSOrderService;
import com.example.casem3.service.mysql.MSProductService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@WebServlet(name = "CartServlet", urlPatterns = "/cart")
public class CartServlet extends HttpServlet {
    private IProductService iProductService;
    private IOrderService iOrderService;
    private ICategoryService iCategoryService;
    @Override
    public void init() throws ServletException {
        iProductService = new MSProductService();
        iOrderService = new MSOrderService();
        iCategoryService = new MSCategoryService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "add":
                handleAddToCart(req, resp);
                break;
            case "edit":
                showEditCart(req, resp);
                break;
            case "checkout":
                showCheckOutCart(req, resp);
                break;
            case "details":
                showDetails(req,resp);
                break;
            default:
                showCart(req, resp);

        }
    }

    private void showDetails(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Long.parseLong(req.getParameter("idOrder"));
        Order order = iOrderService.getOrderById(id);
        if (order!=null){
            req.setAttribute("orderDTO",convertToOrderDTO(order));
        }
        req.getRequestDispatcher(ResourceConfig.folderFrontEnd+"cart.jsp").forward(req,resp);
    }

    private void showCheckOutCart(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Order order = (Order) session.getAttribute("order");
        if (order != null) {
            req.setAttribute("orderDTO", convertToOrderDTO(order));
        }
        RequestDispatcher requestDispatcher = req.getRequestDispatcher(ResourceConfig.folderFrontEnd + "checkout.jsp");
        requestDispatcher.forward(req, resp);
    }

    private void showCart(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Category> categories = iCategoryService.getAllCategorys();
        HttpSession session = req.getSession();
        Order order = (Order) session.getAttribute("order");
        req.setAttribute("categories",categories);
        if (order != null) {
            req.setAttribute("orderDTO", convertToOrderDTO(order));
        }
        RequestDispatcher requestDispatcher = req.getRequestDispatcher(ResourceConfig.folderFrontEnd + "cart.jsp");
        requestDispatcher.forward(req, resp);

    }

    private void showEditCart(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = req.getRequestDispatcher(ResourceConfig.folderFrontEnd + "cart.jsp");
        requestDispatcher.forward(req, resp);
    }

    private void handleAddToCart(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Order order = (Order) session.getAttribute("order");
        if (order == null) {
            order = new Order();
        }
        int quantity = 1;
        if(req.getParameter("quantity")!= null){
            quantity= Integer.parseInt(req.getParameter("quantity"));
        }
        long id = Long.parseLong(req.getParameter("id"));
        // Kiem tra trước Id sản phẩm này có hợp lệ
        if (order.getOrderItems() == null) {
            List<OrderItem> orderItems = new ArrayList<>();
            OrderItem orderItem = new OrderItem();
            orderItem.setIdProduct(id);
            orderItem.setQuantiy(quantity);

            orderItems.add(orderItem);
            order.setOrderItems(orderItems);
        }else{
            if (checkIdProductExistInOrder(order, id)) {
                increaseQuantityInOrder(order, id, quantity);
            }else{
                OrderItem orderItem = new OrderItem();
                orderItem.setIdProduct(id);
                orderItem.setQuantiy(quantity);
                order.getOrderItems().add(orderItem);
            }
        }

        session.setAttribute("order", order);
        req.setAttribute("orderDTO", convertToOrderDTO(order));

//        orderDTO.getOderItemDTOS()
        RequestDispatcher requestDispatcher = req.getRequestDispatcher(ResourceConfig.folderFrontEnd + "cart.jsp");
        requestDispatcher.forward(req, resp);
    }

    public OrderDTO convertToOrderDTO(Order order) {
        OrderDTO orderDTO = order.toDTO();
        /**
         List<OrderItemDTO> orderItemDTOS = new ArrayList<>();
         List<OrderItem> orderItems = order.getOrderItems();
         for (int i = 0; i < orderItems.size(); i++) {
         OrderItemDTO orderItemDTO = new OrderItemDTO();
         orderItemDTO.setQuatity(orderItems.get(i).getQuantiy());
         orderItemDTO.setId(orderItems.get(i).getId());
         Product product = iProductService.findProductById(orderItems.get(i).getIdProduct());

         orderItemDTO.setProduct(product);
         orderItemDTOS.add(orderItemDTO);
         }
         orderDTO.setOderItemDTOS(orderItemDTOS);
         **/
        List<OrderItemDTO> orderItemDTOS = order.getOrderItems().stream().map(new Function<OrderItem, OrderItemDTO>() {
            @Override
            public OrderItemDTO apply(OrderItem orderItem) {
                OrderItemDTO orderItemDTO = new OrderItemDTO();
                orderItemDTO.setQuantity(orderItem.getQuantiy());
                orderItemDTO.setId(orderItem.getId());
                Product product = iProductService.findProductById(orderItem.getIdProduct());
                orderItemDTO.setProduct(product);
                return orderItemDTO;
            }
        }).collect(Collectors.toList());
        orderDTO.setOderItemDTOS(orderItemDTOS);
        return orderDTO;
    }
    public void increaseQuantityInOrder(Order order, long idProduct, int quantity) {
        for (int i = 0; i < order.getOrderItems().size(); i++) {
            if (order.getOrderItems().get(i).getIdProduct() == idProduct) {
                int quantityPresent = order.getOrderItems().get(i).getQuantiy();
                order.getOrderItems().get(i).setQuantiy(quantityPresent + quantity);
            }
        }
    }
    public void setQuantityInOrder(Order order, long idProduct, int quantity) {
        for (int i = 0; i < order.getOrderItems().size(); i++) {
            if (order.getOrderItems().get(i).getIdProduct() == idProduct) {
                if (quantity > 0) {
                    order.getOrderItems().get(i).setQuantiy(quantity);
                }else{
                    order.getOrderItems().remove(i);
                    break;
                }

            }
        }
    }
    public boolean checkIdProductExistInOrder(Order order, long idProduct) {
        for (int i = 0; i < order.getOrderItems().size(); i++) {
            if (order.getOrderItems().get(i).getIdProduct() == idProduct) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "edit":
                handleEditCart(req, resp);
                break;
            case "checkout":
                handleCheckout(req, resp);
                break;
        }
    }

    private void handleCheckout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        HttpSession session = req.getSession();
        Order order = (Order) session.getAttribute("order");

        if (order != null) {
            order.setAddress(address);
            order.setPhone(phone);
            order.setName(name);


            // Tự lưu xuong database đi
            iOrderService.saveOrder(order);
            // xóa thông tin trong session
            session.removeAttribute("order");
            resp.sendRedirect("/");
        }

    }

    private void handleEditCart(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long idProduct = Long.parseLong(req.getParameter("idproduct"));
        int quantity = Integer.parseInt(req.getParameter("quantity"));

        HttpSession session = req.getSession();
        Order order = (Order) session.getAttribute("order");


        // Nếu có ton tại
        if (checkIdProductExistInOrder(order, idProduct)) {
            setQuantityInOrder(order, idProduct, quantity);
            session.setAttribute("order", order);

            req.setAttribute("orderDTO", convertToOrderDTO(order));
        }else{
            // báo lỗi không có idproduct
            req.setAttribute("message", "Sản phẩm không tồn tại");
        }
        RequestDispatcher requestDispatcher = req.getRequestDispatcher(ResourceConfig.folderFrontEnd + "cart.jsp");
        requestDispatcher.forward(req, resp);
    }
}
