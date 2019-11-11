package fr.d2factory.libraryapp.library;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.member.Member;
import fr.d2factory.libraryapp.member.Resident;
import fr.d2factory.libraryapp.member.Student;

/**
 * 
 * @author Kais Belhareth
 *
 */
public class LibraryImpl implements Library{

    private static final int daysBeforeLateStudent = 30;
    private static final int daysBeforeLateResident = 60;
    
	private BookRepository bookRepository;
	
	Map<Book, Member> borrowers = new HashMap<>();
	
	public LibraryImpl(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}
		
	@Override
	public Book borrowBook(long isbnCode, Member member, LocalDate borrowedAt) throws  HasDebtException, HasLateBooksException {
		if (member.haveDebts())
			throw new hasDebtException();
		isMemberLate(member);
		Book bookToBorrow = bookRepository.findBook(isbnCode);
		if(member.isLate()) {
			throw new HasLateBooksException();
		}else if (bookToBorrow != null && !member.isLate()) {
			bookRepository.saveBookBorrow(bookToBorrow, borrowedAt);
			borrowers.put(bookRepository.removeBook(isbnCode), member);
		}
		return bookToBorrow;		
	}

	@Override
	public void returnBook(Book book, Member member) {
		member.payBook(bookRepository.getBorrowedBooks().get(book).until(LocalDate.now()).getDays());
		List<Book> list = new ArrayList<>();
		list.add(book);
		bookRepository.addBooks(list);
		bookRepository.getBorrowedBooks().remove(book);		
	}

	@Override
	public void isMemberLate(Member member) {
		LocalDate today = LocalDate.now();
		int amountOfDaysAllowed = 0;
		if (member instanceof Student)
			amountOfDaysAllowed = daysBeforeLateStudent;
		if (member instanceof Resident)
			amountOfDaysAllowed = daysBeforeLateResident;

		Iterator<Map.Entry<Book, Member>> iterator = borrowers.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Book, Member> entry = iterator.next();
			if (entry.getValue().equals(member)) {
				if (bookRepository.findBorrowedBookDate(entry.getKey()).plusDays(amountOfDaysAllowed).isBefore(today)) {
					entry.getValue().setLate(true);
				}
			}
		}
	}
}
