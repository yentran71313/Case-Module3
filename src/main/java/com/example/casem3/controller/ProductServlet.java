package com.example.casem3.controller;

import com.example.casem3.config.ResourceConfig;
import com.example.casem3.model.Category;
import com.example.casem3.model.Customer;
import com.example.casem3.model.Product;
import com.example.casem3.model.Role;
import com.example.casem3.service.ICategoryService;
import com.example.casem3.service.ICustomerService;
import com.example.casem3.service.IProductService;
import com.example.casem3.service.IRoleService;
import com.example.casem3.service.mysql.MSCategoryService;
import com.example.casem3.service.mysql.MSCustomerService;
import com.example.casem3.service.mysql.MSProductService;
import com.example.casem3.service.mysql.MSRoleService;
import com.example.casem3.utils.DateUtils;
import com.example.casem3.utils.ValidateUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "ProductServlet", urlPatterns = "/products")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 50, // 50MB
        maxRequestSize = 1024 * 1024 * 50) // 50MB
public class ProductServlet extends HttpServlet {

    private IProductService iProductService;
    private ICategoryService iCategoryService;

    @Override
    public void init() throws ServletException {
        iCategoryService = new MSCategoryService();
        iProductService = new MSProductService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        System.out.println("Đương dẫn chứa web ở server");
        String appRealPath = getServletContext().getRealPath("/");
        System.out.println(appRealPath);
        String action = req.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "create":
                showCreateProducts(req, resp);
                break;
            case "delete":
                showDeleteProducts(req, resp);
                break;
            case "edit":
                showEditProducts(req, resp);
                break;
            default:
                showProducts(req, resp);
        }



//        resp.sendRedirect("/translate");
    }

    private void showEditProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Category> categories = iCategoryService.getAllCategorys();
        int id = Integer.parseInt(req.getParameter("id"));
        Product product = iProductService.findProductById((long) id);

        if (product == null) {
            resp.sendRedirect("/products?message=edit");
        }else{

            req.setAttribute("product", product);
            req.setAttribute("categories", categories);
            RequestDispatcher requestDispatcher = req.getRequestDispatcher(ResourceConfig.folderDashboard + "product/edit.jsp");
            requestDispatcher.forward(req, resp);
        }


    }

    private void showDeleteProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Long.parseLong(req.getParameter("id"));
        Product product = iProductService.findProductById(id);

        req.setAttribute("product", product);
        RequestDispatcher requestDispatcher = req.getRequestDispatcher(ResourceConfig.folderDashboard + "product/delete.jsp");
        requestDispatcher.forward(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "create":
                createProduct(req, resp);
                break;
            case "delete":
                deleteProduct(req, resp);
                break;

            case "edit":
                editProduct(req, resp);
                break;
        }
    }

    private void editProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> errors = new ArrayList<>();
        Product product = new Product();

        isValidName(req, product, errors);
//        isValidAddress(req, product, errors);
        isValidCustomerType(req, product, errors);

        Part part = isValidImage(req, product, errors);

        String sCreatedAt = req.getParameter("createdAt");
        Date createAt = DateUtils.formatDate(sCreatedAt);
        product.setCreateAt(createAt);

//        errors.size()

        List<Category> categories = iCategoryService.getAllCategorys();
        req.setAttribute("categories", categories);
        RequestDispatcher requestDispatcher = req.getRequestDispatcher(ResourceConfig.folderDashboard + "product/edit.jsp");
        if (errors.size() == 0) {
            long idCustomer = Long.parseLong(req.getParameter("id"));
            product.setId(idCustomer);
//            handleEditImageUpload(req, customer, errors);

            Product productDB = iProductService.findProductById(product.getId());

            if ( product.getImage()!=null) {
                if(productDB.getImage() != null && !productDB.getImage().equals(productDB.getImage()) && part != null){
                    handleEditImageUploadAdvanced(part);
                }
            }
            req.setAttribute("message", "Them thanh cong");
            iProductService.editProduct(product);

            resp.sendRedirect("/products");
        }else{
            req.setAttribute("errors", errors);
            req.setAttribute("product", product);
            requestDispatcher.forward(req, resp);
        }
    }

    private void handleEditImageUploadAdvanced(Part part) throws IOException {

        String fileName = extractFileName(part);
        String appRealPath = getServletContext().getRealPath("/") + "images";
        File file = new File(appRealPath);
        if (!file.exists()) {
            file.mkdir();
        }
        String nameFileServer = appRealPath + File.separator + fileName;
        System.out.println("Name file server: " + nameFileServer);
        part.write(nameFileServer);
        String nameFileProject = ResourceConfig.folderImageUrlProject + File.separator + fileName;
        System.out.println("Name file project: " + nameFileProject);
        part.write(nameFileProject);

    }

    private Part isValidImage(HttpServletRequest req, Product product, List<String> errors) throws ServletException, IOException {
        for (Part part : req.getParts()) {
            String fileName = extractFileName(part);
            if(!fileName.equals("")){
                product.setImage(fileName);
                boolean check = iProductService.checkImageExists(fileName);
                if (check == true) {
                    return null;
                }else {
                    return part;
                }
            }
        }
        return null;
    }

    private void handleEditImageUpload(HttpServletRequest req, Product product, List<String> errors) throws ServletException, IOException {
        for (Part part : req.getParts()) {
            String fileName = extractFileName(part);
            // refines the fileName in case it is an absolute path
            if(!fileName.equals("")){
                Product productDB = iProductService.findProductById(product.getId());
                if (!productDB.getImage().equals(fileName)) {
                    String appRealPath = getServletContext().getRealPath("/") + "images";
                    File file = new File(appRealPath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    String nameFileServer = appRealPath + File.separator + fileName;
                    System.out.println("Name file server: " + nameFileServer);
                    part.write(nameFileServer);


                    String nameFileProject = ResourceConfig.folderImageUrlProject + File.separator + fileName;
                    System.out.println("Name file project: " + nameFileProject);
                    part.write(nameFileProject);

                    product.setImage(fileName);
                }else{
                    // lay lai anh cu
                    product.setImage(productDB.getImage());
                }
            }

//            part.write();
        }
    }

    private void deleteProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = Long.parseLong(req.getParameter("idDelete"));
        iProductService.deleteProductById(id);


        resp.sendRedirect("/products?message=delete");
    }

    private void createProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> errors = new ArrayList<>();
        Product product = new Product();

        isValidName(req, product, errors);
//        isValidAddress(req, product, errors);
        isValidCustomerType(req, product, errors);
        String sCreatedAt = req.getParameter("createdAt");
        Date createAt = DateUtils.formatDate(sCreatedAt);
        product.setCreateAt(createAt);
        isValidPrice(req,product,errors);
        String description = req.getParameter("description");
        product.setDescription(description);

//        errors.size()

        List<Category> categories = iCategoryService.getAllCategorys();
        req.setAttribute("categories", categories);
        RequestDispatcher requestDispatcher = req.getRequestDispatcher(ResourceConfig.folderDashboard + "product/create.jsp");
        if (errors.size() == 0) {
            handleImageUpload(req, product, errors);
            req.setAttribute("message", "Them thanh cong");
            iProductService.createProduct(product);
            requestDispatcher.forward(req, resp);
        }else{
            req.setAttribute("errors", errors);
            req.setAttribute("product", product);
            requestDispatcher.forward(req, resp);
        }
    }

    private void isValidPrice(HttpServletRequest req, Product product, List<String> errors) {
        try {
            double price = Double.parseDouble(req.getParameter("price"));
            product.setPrice(price);
            if (price<=0){
                errors.add("Giá phải là số lớn hơn 0");
            }
        }catch (Exception e){
            errors.add("Giá phải là số lớn hơn 0");
        }
    }


    private void isValidCustomerType(HttpServletRequest req, Product product, List<String> errors) {
        try {
            int idCustomerType = Integer.parseInt(req.getParameter("Category"));
            if (iCategoryService.getCategoryById(idCustomerType)!=null) {
                product.setIdCategory(idCustomerType);
            }else{
                errors.add("Loại khách hàng chưa hợp lệ");
            }
        } catch (NumberFormatException numberFormatException) {
            errors.add("Định dạng loại khách hàng chưa đúng");
        }
    }

    private void handleImageUpload(HttpServletRequest req, Product product, List<String> errors) throws ServletException, IOException {
        for (Part part : req.getParts()) {
            String fileName = extractFileName(part);
            // refines the fileName in case it is an absolute path
            if(!fileName.equals("")){
                String appRealPath = getServletContext().getRealPath("/") + "images";
                File file = new File(appRealPath);
                if (!file.exists()) {
                    file.mkdir();
                }
                String nameFileServer = appRealPath + File.separator + fileName;
                System.out.println("Name file server: " + nameFileServer);
                part.write(nameFileServer);


                String nameFileProject = ResourceConfig.folderImageUrlProject + File.separator + fileName;
                System.out.println("Name file project: " + nameFileProject);
                part.write(nameFileProject);

                product.setImage(fileName);
            }

//            part.write();
        }

    }
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }
        return "";
    }

//    private void isValidAddress(HttpServletRequest req, Product product, List<String> errors) {
//        String address = req.getParameter("address");
//        if (!ValidateUtils.isAddressValid(address)) {
//            errors.add("Địa chỉ không hợp lệ. Chỉ chứa từ từ 5-15 kí và bắt đầu A-Za-z0-9");
//        }
//        product.setTitle(address);
//    }

    private void isValidName(HttpServletRequest req, Product product, List<String> errors) {
        String name = req.getParameter("name");
        if (!ValidateUtils.isNameValid(name)) {
            errors.add("Tên không hợp lệ. Chỉ chứa từ từ 5-10 kí và bắt đầu A-Za-z0-9");
        }
        product.setName(name);
    }

    private void showCreateProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Category> categories = iCategoryService.getAllCategorys();
        req.setAttribute("categories", categories);
        RequestDispatcher requestDispatcher = req.getRequestDispatcher(ResourceConfig.folderDashboard  + "product/create.jsp");
        requestDispatcher.forward(req, resp);
    }

    private void showProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String kw = "";
        if (req.getParameter("kw") != null) {
            kw = req.getParameter("kw");
        }
        int idCategory = -1;
        if (req.getParameter("idCategory") != null && !req.getParameter("idCategory").equals("")) {
            idCategory = Integer.parseInt(req.getParameter("idCategory"));
        }
        int page = 1;
        if (req.getParameter("page") != null && !req.getParameter("page").equals("")) {
            page = Integer.parseInt(req.getParameter("page"));
        }
        int limit = 5;
        if (req.getParameter("limit") != null && !req.getParameter("limit").equals("")) {
            limit = Integer.parseInt(req.getParameter("limit"));
        }



        List<Product> products = iProductService.getAllProductSearchingPagging(kw,idCategory,  (page-1)*limit, limit);
        List<Category> categories = iCategoryService.getAllCategorys();


        int noOfRecords = iProductService.getNoOfRecords();
        int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / limit);


//        customers.size()
        req.setAttribute("products", products);
        req.setAttribute("categories", categories );
        req.setAttribute("kw", kw);
        req.setAttribute("idCategory", idCategory);

        req.setAttribute("currentPage", page);
        req.setAttribute("noOfPages", noOfPages);

        String message = req.getParameter("message");
        if (message != null) {
            // delete: success
            req.setAttribute("message", message );
        }

        RequestDispatcher requestDispatcher = req.getRequestDispatcher(ResourceConfig.folderDashboard + "product/products.jsp");
        requestDispatcher.forward(req, resp);
    }
}
