
package business;

import business.book.BookDao;
import business.book.BookDaoJdbc;
import business.category.CategoryDao;
import business.category.CategoryDaoJdbc;
import business.customer.CustomerDaoJdbc;
import business.order.*;
import business.customer.CustomerDao;

public class ApplicationContext {

    // TODO Add field and complete the getter for bookDao
    public static ApplicationContext INSTANCE = new ApplicationContext();

    private CategoryDao categoryDao;

    private BookDao bookDao;

    private OrderService orderService;

    private CustomerDao customerDao;
    private LineItemDao lineItemDao;
    private OrderDao orderDao;


    private ApplicationContext() {
        categoryDao = new CategoryDaoJdbc();
        bookDao = new BookDaoJdbc();
        orderService = new DefaultOrderService();
        customerDao = new CustomerDaoJdbc();
        System.out.println("CustomerDao initialized: " + (customerDao != null));
        lineItemDao = new LineItemDaoJdbc();
        orderDao = new OrderDaoJdbc();
        ((DefaultOrderService) orderService).setBookDao(bookDao);
        ((DefaultOrderService) orderService).setCustomerDao(customerDao);
        ((DefaultOrderService) orderService).setOrderDao(orderDao);
        ((DefaultOrderService) orderService).setLineItemDao(lineItemDao);

    }


    public CategoryDao getCategoryDao() {
        return categoryDao;
    }

    public BookDao getBookDao() {
        return bookDao;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public CustomerDao getCustomerDao() {
        return customerDao;
    }

    public LineItemDao getLineItemDao() {
        return lineItemDao;
    }

    public OrderDao getOrderDao() {
        return orderDao;
    }

}