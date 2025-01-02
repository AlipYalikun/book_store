package business.book;



public record Book(long bookId, String title, String author,
				   String description,
				   int price,
				   double rating,
				   boolean isPublic,
				   boolean isFeatured,
				   long categoryId) {}
