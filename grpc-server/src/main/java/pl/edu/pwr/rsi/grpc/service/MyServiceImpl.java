package pl.edu.pwr.rsi.grpc.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import pl.edu.pwr.rsi.grpc.interfaces.lib.*;

import io.grpc.stub.StreamObserver;

import net.devh.boot.grpc.server.service.GrpcService;
import pl.edu.pwr.rsi.grpc.tempDB.TempDB;

import java.util.ArrayList;
import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class MyServiceImpl extends MyServiceGrpc.MyServiceImplBase {

    private final TempDB database;

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder()
                .setMessage("Hello ==> " + request.getName())
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void saveAuthor(Author author, StreamObserver<HelloReply> responseObserver) {
        database.getAuthorsFromDb().add(author);
        HelloReply reply = HelloReply.newBuilder()
                .setMessage("Author succesfully saved")
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void getAuthor(Author request, StreamObserver<Author> responseObserver) {
        database.getAuthorsFromDb()
                .stream()
                .filter(author -> author.getAuthorId() == request.getAuthorId())
                .findFirst()
                .ifPresent(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    @Override
    public void getBooksByAuthor(Author request, StreamObserver<Book> responseObserver) {
        database.getBooksFromTempDb()
                .stream()
                .filter(book -> book.getAuthorId() == request.getAuthorId())
                .forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Book> saveMultipleBooks(StreamObserver<HelloReply> responseObserver) {
        return new StreamObserver<Book>() {
            @Override
            public void onNext(Book book) {
                database.getBooksFromTempDb().add(book);
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                HelloReply reply = HelloReply.newBuilder()
                        .setMessage("Books successfully loaded")
                        .build();
                System.out.println(reply.getMessage());
                responseObserver.onNext(reply);
                responseObserver.onCompleted();
            }
        };
    }

    /*
    @Override
    public StreamObserver<Book> getBookByAuthorGender(StreamObserver<Book> responseObserver) {
        return new StreamObserver<Book>() {
            List<Book> booklist = new ArrayList<>();
            @Override
            public void onNext(Book book) {
                database.getBooksFromTempDb()
                        .stream()
                        .filter(bookFromDb -> book.getAuthorId() == bookFromDb.getAuthorId())
                        .forEach(booklist::add);
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                booklist.forEach(responseObserver::onNext);
                responseObserver.onCompleted();
            }
        };
    }
    */
}
