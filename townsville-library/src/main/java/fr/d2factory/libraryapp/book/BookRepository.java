package fr.d2factory.libraryapp.book;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The book repository emulates a database via 2 HashMaps
 */
public class BookRepository {
    private Map<ISBN, Book> availableBooks = new HashMap<>();
    private Map<Book, LocalDate> borrowedBooks = new HashMap<>();

    public void addBooks(List<Book> books){
    	for(Book b:books) {
    		availableBooks.put(b.getIsbn(),b);
    	}
    }

    public Book findBook(long isbnCode) {
    		Iterator<Map.Entry<ISBN, Book>> iterator = availableBooks.entrySet().iterator();
			
			while (iterator.hasNext()) {
				Map.Entry<ISBN, Book> entry = iterator.next();
				if (entry.getKey().getIsbnCode() == isbnCode )
					return entry.getValue();
			}
			return null;
    }

    public void saveBookBorrow(Book book, LocalDate borrowedAt){
    	borrowedBooks.put(book, borrowedAt);
    }

    public LocalDate findBorrowedBookDate(Book book) {
    	if(book!= null) {
			long isbn = book.getIsbn().getIsbnCode();
			Iterator<Map.Entry<Book, LocalDate>> iterator = borrowedBooks.entrySet().iterator();
			
			while (iterator.hasNext()) {
				Map.Entry<Book, LocalDate> entry = iterator.next();
				if (entry.getKey().getIsbn().getIsbnCode() ==isbn)
					return entry.getValue();
			}
		}
		return null;
    }
    
	public Book removeBook(long isbnCode) {		
		Iterator<Map.Entry<ISBN, Book>> iterator = availableBooks.entrySet().iterator();
		
		while (iterator.hasNext()) {
			Map.Entry<ISBN, Book> entry = iterator.next();
			if (entry.getKey().getIsbnCode() == isbnCode ) {
				availableBooks.remove(entry.getKey());
				return entry.getValue();				
			}
		}
		return null;		
	}

	public Map<ISBN, Book> getAvailableBooks() {
		return availableBooks;
	}

	public void setAvailableBooks(Map<ISBN, Book> availableBooks) {
		this.availableBooks = availableBooks;
	}

	public Map<Book, LocalDate> getBorrowedBooks() {
		return borrowedBooks;
	}

	public void setBorrowedBooks(Map<Book, LocalDate> borrowedBooks) {
		this.borrowedBooks = borrowedBooks;
	}
}
