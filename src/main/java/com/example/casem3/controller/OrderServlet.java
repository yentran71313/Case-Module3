package com.example.casem3.controller;

import com.example.casem3.config.ResourceConfig;
import com.example.casem3.model.Order;
import com.example.casem3.service.IOrderService;
import com.example.casem3.service.mysql.MSOrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "OrderServlet",urlPatterns = "/orders")
public class OrderServlet extends HttpServlet {
    private IOrderService iOrderService ;

    @Override
    public void init() throws ServletException {
        iOrderService = new MSOrderService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Order> orders = iOrderService.getAllOrder();
        req.setAttribute("orders",orders);
        req.getRequestDispatcher(ResourceConfig.folderFrontEnd+"order.jsp").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }
}
