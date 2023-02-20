package com.example.casem3.controller;



import com.example.casem3.config.ResourceConfig;
import com.example.casem3.model.Customer;
import com.example.casem3.service.ICustomerService;
import com.example.casem3.service.mysql.MSCustomerService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet",urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    private ICustomerService iCustomerService;

    @Override
    public void init() throws ServletException {
        iCustomerService = new MSCustomerService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null){
            action = "";
        }
        switch (action){
            case "account":
                HttpSession session = req.getSession();
                Customer customer = (Customer) session.getAttribute("khachhang");
                req.getRequestDispatcher(ResourceConfig.folderFrontEnd + "my-account.jsp").forward(req,resp);
                break;
            default:
                req.getRequestDispatcher(ResourceConfig.folderFrontEnd + "login.jsp").forward(req,resp);
                break;

        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        Customer customer = iCustomerService.findCustomerByUsernamePassword(username,password);
        if (customer!=null){
            HttpSession session = req.getSession();
            session.setAttribute("khachhang",customer);
            resp.sendRedirect("/");
        } else {
            req.setAttribute("errors","Ten dang nhap hoac mat khau khong chinh xac");
            req.getRequestDispatcher(ResourceConfig.folderFrontEnd+"login.jsp").forward(req,resp);
        }
}
}
