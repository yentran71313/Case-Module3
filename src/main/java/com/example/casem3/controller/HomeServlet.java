package com.example.casem3.controller;



import com.example.casem3.config.ResourceConfig;
import com.example.casem3.model.Category;
import com.example.casem3.model.Product;
import com.example.casem3.service.ICategoryService;
import com.example.casem3.service.IProductService;
import com.example.casem3.service.mysql.MSCategoryService;
import com.example.casem3.service.mysql.MSProductService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@WebServlet(name = "HomeServlet", urlPatterns = "")
public class HomeServlet extends HttpServlet {
    private IProductService iProductService;
    private ICategoryService iCategoryService;

    @Override
    public void init() throws ServletException {
        iProductService = new MSProductService();
        iCategoryService = new MSCategoryService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher requestDispatcherIndex = req.getRequestDispatcher(ResourceConfig.folderFrontEnd + "index.jsp");
        String action = req.getParameter("action");
        if (action==null){
            action="";
        }
        switch (action){
            case "detail":
                ShowDetail(req,resp);
                break;
            default:
                showProduct(req,resp);
                break;

        }



//        req.setAttribute("productListPhone", productListPhone);
//        req.setAttribute("productListTablet", productListTablet);

//        requestDispatcherIndex.forward(req, resp);
    }

    private void ShowDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long idProduct = Long.parseLong(req.getParameter("pid"));
        Product product = iProductService.findProductById(idProduct);
        req.setAttribute("product",product);
        RequestDispatcher requestDispatcher = req.getRequestDispatcher(ResourceConfig.folderFrontEnd+"detail.jsp");
        requestDispatcher.forward(req,resp);
    }

    private void showProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String kw = "";
        if (req.getParameter("kw")!= null){
            kw = req.getParameter("kw");
        }
        int idCategory = -1;
        if (req.getParameter("cid")!= null && !req.getParameter("cid").equals("")){
            idCategory= Integer.parseInt(req.getParameter("cid"));
        }
        int page = 1;
        if (req.getParameter("page") != null && !req.getParameter("page").equals("")) {
            page = Integer.parseInt(req.getParameter("page"));
        }
        int limit = 5;
        if (req.getParameter("limit") != null && !req.getParameter("limit").equals("")) {
            limit = Integer.parseInt(req.getParameter("limit"));
        }
        List<Product> products = iProductService.getAllProductSearchingPagging(kw,idCategory,(page-1)*limit,limit);
        List<Category> categories = iCategoryService.getAllCategorys();

        int noOfRecords = iProductService.getNoOfRecords();
        int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / limit);;

        req.setAttribute("categories",categories);
        req.setAttribute("products",products);
        req.setAttribute("kw",kw);
        req.setAttribute("category",idCategory);

        req.setAttribute("currentPage",page);
        req.setAttribute("noOfPages", noOfPages);
        String message = req.getParameter("message");
        if (message != null) {
            // delete: success
            req.setAttribute("message", message );
        }

        RequestDispatcher requestDispatcher = req.getRequestDispatcher(ResourceConfig.folderFrontEnd + "index.jsp");
        requestDispatcher.forward(req,resp);
    }


}
