package business.order;

import api.ApiException;
import business.BookstoreDbException;
import business.JdbcUtils;
import business.book.Book;
import business.book.BookDao;
import business.cart.ShoppingCart;
import business.cart.ShoppingCartItem;
import business.customer.Customer;
import business.customer.CustomerDao;
import business.customer.CustomerForm;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DefaultOrderService implements OrderService {

	private BookDao bookDao;
	private CustomerDao customerDao;
	private LineItemDao lineItemDao;
	private OrderDao orderDao;

	public void setBookDao(BookDao bookDao) {
		this.bookDao = bookDao;
	}
	public void setCustomerDao(CustomerDao customerDao) {this.customerDao = customerDao;}
	public void setLineItemDao(LineItemDao lineItemDao) {this.lineItemDao = lineItemDao;}
	public void setOrderDao(OrderDao orderDao) {this.orderDao = orderDao;}

	@Override
	public OrderDetails getOrderDetails(long orderId) {
		Order order = orderDao.findByOrderId(orderId);
		Customer customer = customerDao.findByCustomerId(order.customerId());
		List<LineItem> lineItems = lineItemDao.findByOrderId(orderId);
		List<Book> books = lineItems
				.stream()
				.map(lineItem -> bookDao.findByBookId(lineItem.bookId()))
				.toList();
		return new OrderDetails(order, customer, lineItems, books);
	}
	private Date getCardExpirationDate(String monthString, String yearString) {
		try {
			int month = Integer.parseInt(monthString);
			int year = Integer.parseInt(yearString);

			if (month < 1 || month > 12) {
				throw new IllegalArgumentException("Invalid month: " + monthString);
			}

			YearMonth expiryDate = YearMonth.of(year, month);
			System.out.println("Parsed expiry date: " + expiryDate);

			return java.sql.Date.valueOf(expiryDate.atDay(1));
		} catch (Exception e) {
			System.err.println("Error parsing expiry date: " + e.getMessage());
			throw new ApiException.ValidationFailure("Invalid expiry date");
		}
	}

	private int generateConfirmationNumber(){
        return ThreadLocalRandom.current().nextInt(999999999);
    }

	private long performPlaceOrderTransaction(
			String name, String address, String phone, String email, String ccNumber, Date ccExpDate,
			ShoppingCart cart, Connection connection) {
		if (customerDao == null) {
			throw new IllegalStateException("CustomerDao is not initialized");
		}
		try {
			connection.setAutoCommit(false);

			long customerId = customerDao.create(connection, name, address, phone, email, ccNumber, ccExpDate);

			long customerOrderId = orderDao.create(
					connection,
					cart.getComputedSubtotal() + cart.getSurcharge(),
					generateConfirmationNumber(),
					customerId
			);

			for (ShoppingCartItem item : cart.getItems()) {
				lineItemDao.create(connection, item.getBookId(), customerOrderId, item.getQuantity());
			}

			connection.commit();
			return customerOrderId;
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException rollbackException) {
				throw new BookstoreDbException("Failed to roll back transaction", rollbackException);
			}
			throw new BookstoreDbException("Failed to complete order transaction", e);
		}
	}
	@Override
    public long placeOrder(CustomerForm customerForm, ShoppingCart cart) {
		System.out.println("Placing order...");
		System.out.println("CustomerForm: " + customerForm);
		System.out.println("ShoppingCart: " + cart);

		validateCustomer(customerForm);
		validateCart(cart);

		try (Connection connection = JdbcUtils.getConnection()) {
			Date ccExpDate = getCardExpirationDate(
					customerForm.getCcExpiryMonth(),
					customerForm.getCcExpiryYear());
			return performPlaceOrderTransaction(
					customerForm.getName(),
					customerForm.getAddress(),
					customerForm.getPhone(),
					customerForm.getEmail(),
					customerForm.getCcNumber(),
					ccExpDate, cart, connection);
		} catch (SQLException e) {
			throw new BookstoreDbException("Error during close connection for customer order", e);
		}

    }


	private void validateCustomer(CustomerForm customerForm) {
		System.out.println("Validating customer: " + customerForm);

    	String name = customerForm.getName();

		if (name == null || name.equals("") || name.length() > 45) {
			throw new ApiException.ValidationFailure("Invalid name field");
		}
		String address = customerForm.getAddress();
		if (address == null || address.isEmpty() || address.length() < 4 || address.length() > 45) {
			throw new ApiException.ValidationFailure("address", "Address must be between 4 and 45 characters.");
		}

		String phone = customerForm.getPhone().replaceAll("[\\s-]", ""); // Remove spaces and dashes
		if (phone.length() != 10 || !phone.matches("\\d+")) {
			throw new ApiException.ValidationFailure("phone", "Phone number must have exactly 10 digits.");
		}

		String email = customerForm.getEmail();
		if (email == null || email.contains(" ") || !email.contains("@") || email.endsWith(".")) {
			throw new ApiException.ValidationFailure("email", "Email must be valid and not contain spaces.");
		}

		String ccNumber = customerForm.getCcNumber().replaceAll("[\\s-]", ""); // Remove spaces and dashes
		if (ccNumber.length() < 14 || ccNumber.length() > 16 || !ccNumber.matches("\\d+")) {
			throw new ApiException.ValidationFailure("ccNumber", "Credit card number must have 14 to 16 digits.");
		}

		if (expiryDateIsInvalid(customerForm.getCcExpiryMonth(), customerForm.getCcExpiryYear())) {
			throw new ApiException.ValidationFailure("Invalid expiry date");

		}
	}

	private boolean expiryDateIsInvalid(String ccExpiryMonth, String ccExpiryYear) {

		try {
			int month = Integer.parseInt(ccExpiryMonth);
			int year = Integer.parseInt(ccExpiryYear);

			if (month < 1 || month > 12) {
				return true;
			}

			YearMonth expiryDate = YearMonth.of(year, month);
			YearMonth currentDate = YearMonth.now();

			return expiryDate.isBefore(currentDate);
		} catch (NumberFormatException e) {
			return true;
		}

	}

	private void validateCart(ShoppingCart cart) {

		if (cart.getItems().isEmpty()) {
			throw new ApiException.ValidationFailure("Cart is empty.");
		}
		System.out.println("Validating cart items...");
		cart.getItems().forEach(item-> {
			if (item.getQuantity() < 0 || item.getQuantity() > 99) {
				throw new ApiException.ValidationFailure("Invalid quantity");
			}
			Book databaseBook = bookDao.findByBookId(item.getBookId());
			if (databaseBook == null) {
				throw new ApiException.ValidationFailure("Book not found: " + item.getBookId());
			}
			if (item.getBookForm().getPrice() != databaseBook.price()) {
				throw new ApiException.ValidationFailure(
						"Price mismatch for book: " + item.getBookId() + ". Expected price: " + databaseBook.price()
				);
			}
			if (item.getBookForm().getCategoryId() != (databaseBook.categoryId())) {
				throw new ApiException.ValidationFailure(
						"Category mismatch for book: " + item.getBookId() + ". Expected category: " + databaseBook.categoryId()
				);
			}
		});
	}

}
