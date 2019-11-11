package fr.d2factory.libraryapp.library;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.book.ISBN;
import fr.d2factory.libraryapp.member.Member;
import fr.d2factory.libraryapp.member.Resident;
import fr.d2factory.libraryapp.member.Student;

import org.junit.Before;
import org.junit.Test;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Kais Belhareth
 *
 */
public class LibraryTest {
    private Library library ;
    private BookRepository bookRepository;
    private List<Book> listOfBooks;

    @Before
    public void setup(){
        //TODO instantiate the library and the repository
    	bookRepository = new BookRepository();
    	library = new LibraryImpl(bookRepository);
    	
        // Reading books from json file
        try {
            JSONParser parser = new JSONParser();
            JSONArray a = (JSONArray) parser.parse(new FileReader("src\\test\\resources\\books.json"));
            List<Book> list = new ArrayList<>();
            for (Object o : a) {
                JSONObject book = (JSONObject) o;
                String title = (String) book.get("title");
                String author = (String) book.get("author");
                long isbn = (long) ((JSONObject) book.get("isbn")).get("isbnCode");
                Book tmp = new Book(title, author, new ISBN(isbn));
                list.add(tmp);
            }
            listOfBooks = Collections.unmodifiableList(list);
        } catch (ParseException | IOException e) {
            fail("file books.json not found or error while parsing json");
        }
        // filling library with books

        //TODO add some test books (use BookRepository#addBooks)
        //TODO to help you a file called books.json is available in src/test/resources
        bookRepository.addBooks(listOfBooks);
    }

	@Test
	public void test_if_a_book_is_available() {
		assertEquals(46578964513l, bookRepository.findBook(46578964513l).getIsbn().getIsbnCode());
	}
	
    @Test
    public void member_can_borrow_a_book_if_book_is_available(){
		Member studentTest = new Student("10", "Kais", "Belhareth");
		library.borrowBook(46578964513l, studentTest, LocalDate.now().minusDays(30));
		// checking the non availability of the book thus it is impossible to borrow it
		assertEquals(null, bookRepository.findBook(46578964513l));
    }

    @Test
    public void borrowed_book_is_no_longer_available(){
		// according to the function test "member_can_borrow_a_book_if_book_is_available()" the book with isbn 46578964513l is available
		Member memberTest = new Student();
		library.borrowBook(46578964513l, memberTest, LocalDate.now());
		assertEquals(false, bookRepository.getAvailableBooks().containsKey(46578964513l));
    }

    @Test
    public void residents_are_taxed_10cents_for_each_day_they_keep_a_book(){
		Member residentTest = new Resident();
		residentTest.setWallet(20);	// wallet contains 20 euros
		residentTest.payBook(1);	// one day cost 0.10 
									// (according that the student is in the range of the 30 first days)
		assertEquals(19.9f, residentTest.getWallet(), 0f);
    }

    @Test
    public void students_pay_10_cents_the_first_30days(){
		Member studentTest = new Student();
		studentTest.setWallet(20);	// wallet contains 20 euros
		studentTest.payBook(1);		// one day cost 0.10 (according that the student is in the range of the 30 first
									// days)
		assertEquals(19.9f, studentTest.getWallet(), 0f);
    }

    @Test
    public void students_in_1st_year_are_not_taxed_for_the_first_15days(){
		Member studentTest = new Student();
		studentTest.setWallet(20);	// wallet contains 20 euros
		studentTest.payBook(15);	// 15 days cost 1.50 (according the rule are students who are in their first year,
	    							//who have 15 days of free period for each book)
		assertEquals(18.5f, studentTest.getWallet(), 0f);
    }

    @Test
    public void students_pay_15cents_for_each_day_they_keep_a_book_after_the_initial_30days(){
		Member studentTest = new Student();
		studentTest.setWallet(20);	// wallet contains 20 euros
		studentTest.payBook(31);	// 30 days cost 3.0 + 0.15 for the first taxed day
		assertEquals(16.85f, studentTest.getWallet(), 0f);
    }

    @Test
    public void residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days(){
		Member residentTest = new Resident();
		residentTest.setWallet(20);	// wallet contains 20 euros
		residentTest.payBook(61);	// 60 days cost 6.0 + 0.20 for the first taxed day
		assertEquals(13.8f, residentTest.getWallet(), 0f);
    }

    //this test must send a HasLateBooksException
  	@Test(expected=HasLateBooksException.class)
  	public void student_members_cannot_borrow_book_if_they_have_late_books() {
  		Member studentTest = new Student("10", "Kais", "Belhareth");
  		// student exceeds the borrowing period which is over than 30 days
  		library.borrowBook(46578964513l, studentTest, LocalDate.now().minusDays(31));
  		// this student tries to rent a book today
  		library.borrowBook(968787565445l, studentTest, LocalDate.now());

  	}

  	//this test must send a HasLateBooksException
  	@Test(expected=HasLateBooksException.class)
  	public void resident_members_cannot_borrow_book_if_they_have_late_books() {
  		Member residentTest = new Resident("10", "Kais", "Belhareth");
   		// resident exceeds the borrowing period which is over than 60 days
  		library.borrowBook(46578964513l, residentTest, LocalDate.now().minusDays(61));
  		// this resident tries to rent a book today
  		library.borrowBook(968787565445l, residentTest, LocalDate.now());  	
  	}
  	
    @Test
	public void equal_operator_between_two_identical_students() {
		Member studentTest1 = new Student("10", "Kais", "Belhareth");
		Member studentTest2 = new Student("10", "Kais", "Belhareth");
		assertEquals(studentTest1, studentTest2);
	}

	@Test
	public void equal_operator_between_two_identical_residents() {
		Member residentTest1 = new Resident("10", "Kais", "Belhareth");
		Member residentTest2 = new Resident("10", "Kais", "Belhareth");
		assertEquals(residentTest1, residentTest2);
	}

	@Test
	public void equal_operator_between_two_identical_Isbn() {
		ISBN isbnTest1 = new ISBN(1234567l);
		ISBN isbnTest2 = new ISBN(1234567l);
		assertEquals(isbnTest1, isbnTest2);
	}

	@Test
	public void equal_operator_between_two_identical_books() {
		Book bookTest1 = new Book("Harry Potter", "J.K. Rowling", new ISBN(46578964513l));
		Book bookTest2 = new Book("Harry Potter", "J.K. Rowling", new ISBN(46578964513l));
		assertEquals(bookTest1, bookTest2);
	}

	@Test
	public void students_members_have_debts_if_they_have_not_sufficient_wallet() {
		Member studentTest = new Student("10", "Momar", "Diene");
		studentTest.setWallet(0);	// wallet contains 0 euro
		studentTest.payBook(31);	// 30 days cost 3.0 + 0.15 for the first taxed day
		assertEquals(3.15f, studentTest.getDebt(), 0f);
	}

	@Test
	public void students_members_paid_correctly_if_they_have_sufficient_wallet(){
		Member studentTest = new Student();
		studentTest.setWallet(20);	// wallet contains 20 euros
		library.returnBook(library.borrowBook(46578964513l, studentTest, LocalDate.now().minusDays(20)), studentTest);
		assertEquals(18f, studentTest.getWallet(), 0f);
	}

	@Test
	public void residents_members_have_debts_if_they_have_not_sufficient_wallet() {
		Member residentTest1 = new Resident("10", "Momar", "Diene");
		residentTest1.setWallet(5);	// wallet contains 5 euro
		residentTest1.payBook(65);
		assertTrue(residentTest1.haveDebts());
	}

	@Test
	public void residents_members_paid_correctly_if_they_have_sufficient_wallet(){
		Member residentTest1 = new Resident("10", "Momar", "Diene");
		residentTest1.setWallet(20);	// wallet contains 20 euros
		library.returnBook(library.borrowBook(46578964513l, residentTest1, LocalDate.now().minusDays(20)), residentTest1);
		assertEquals(18f, residentTest1.getWallet(), 0f);
	}

	//this test must send a HasDebtException
	@Test(expected=HasDebtException.class)
	public void members_cannot_borrow_book_if_they_have_debts() {
		Member residentTest = new Resident("10", "Momar", "Diene");
		residentTest.setDebt(5f);
		// resident has debts and tries to rent a book today
		library.borrowBook(46578964513l, residentTest, LocalDate.now());
	}
}
