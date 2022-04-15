package pl.edu.pwr.rsi.grpc.tempDB;

import org.springframework.stereotype.Service;
import pl.edu.pwr.rsi.grpc.interfaces.lib.Author;
import pl.edu.pwr.rsi.grpc.interfaces.lib.Book;

import java.util.ArrayList;
import java.util.List;

@Service
public class TempDB {
    private List<Author> authors;
    private List<Book> books;
    public TempDB()
    {
        authors = new ArrayList<Author>();
        authors.add(Author.newBuilder().setAuthorId(1).setBookId(1).setFirstName("Remigiusz").setLastName("Mróz").setGender("M").setBookId(1).build());
        authors.add(Author.newBuilder().setAuthorId(2).setBookId(2).setFirstName("Olga").setLastName("Tokarczuk").setGender("K").setBookId(2).build());
        authors.add(Author.newBuilder().setAuthorId(3).setBookId(3).setFirstName("Andrzej").setLastName("Sapkowski").setGender("M").setBookId(3).build());
        authors.add(Author.newBuilder().setAuthorId(4).setBookId(4).setFirstName("Wojciech").setLastName("Chmielarz").setGender("M").setBookId(4).build());

        books = new ArrayList<Book>();
        books.add(Book.newBuilder().setBookId(1).setAuthorId(1).setTitle("Behawiorysta").setPrice(40f).setPages(100).build());
        books.add(Book.newBuilder().setBookId(2).setAuthorId(1).setTitle("Projekt Riese").setPrice(40f).setPages(100).build());
        books.add(Book.newBuilder().setBookId(3).setAuthorId(2).setTitle("Bieguni").setPrice(40f).setPages(100).build());
        books.add(Book.newBuilder().setBookId(4).setAuthorId(3).setTitle("Ostatnie życzenie").setPrice(40f).setPages(100).build());
        books.add(Book.newBuilder().setBookId(5).setAuthorId(3).setTitle("Chrzest ognia").setPrice(40f).setPages(100).build());
        books.add(Book.newBuilder().setBookId(6).setAuthorId(4).setTitle("Podpalacz").setPrice(40f).setPages(100).build());
    }

    public List<Author> getAuthorsFromDb() {
        return authors;
    }

    public List<Book> getBooksFromTempDb() {
        return books;
    }
}

