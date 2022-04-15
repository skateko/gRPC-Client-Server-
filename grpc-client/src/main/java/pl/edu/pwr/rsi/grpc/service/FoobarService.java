package pl.edu.pwr.rsi.grpc.service;

import com.google.protobuf.Descriptors;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import pl.edu.pwr.rsi.grpc.interfaces.lib.Author;
import pl.edu.pwr.rsi.grpc.interfaces.lib.Book;
import pl.edu.pwr.rsi.grpc.interfaces.lib.HelloReply;
import pl.edu.pwr.rsi.grpc.interfaces.lib.HelloRequest;
import pl.edu.pwr.rsi.grpc.interfaces.lib.MyServiceGrpc.MyServiceBlockingStub;
import pl.edu.pwr.rsi.grpc.interfaces.lib.MyServiceGrpc.MyServiceStub;

import net.devh.boot.grpc.client.inject.GrpcClient;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class FoobarService {

//    @GrpcClient("myService")
    private final MyServiceBlockingStub synchronousClient;

//    @GrpcClient("myService")
    private final MyServiceStub asynchronousClient;

    public String receiveGreeting(String name) {
        HelloRequest request = HelloRequest.newBuilder()
                .setName(name)
                .build();
        return synchronousClient.sayHello(request).getMessage();
    }

    public Map<Descriptors.FieldDescriptor, Object> getAuthor(int authorId) {
        Author authorRequest = Author.newBuilder().setAuthorId(authorId).build();
        Author authorResponse = synchronousClient.getAuthor(authorRequest);
        return authorResponse.getAllFields();
    }

    public String saveAuthor(Author author) {
        return synchronousClient.saveAuthor(author).getMessage();
    }

    public List<Map<Descriptors.FieldDescriptor, Object>> getBooksByAuthor(int authorId) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Author authorRequest = Author.newBuilder().setAuthorId(authorId).build();
        final List<Map<Descriptors.FieldDescriptor, Object>> response = new ArrayList<>();
        asynchronousClient.getBooksByAuthor(authorRequest, new StreamObserver<Book>() {
            @Override
            public void onNext(Book book) {
                response.add(book.getAllFields());
            }

            @Override
            public void onError(Throwable throwable) {
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });

        boolean await = countDownLatch.await(1, TimeUnit.MINUTES);
        return await ? response : Collections.emptyList();
    }

    public String saveMultipleBooks(ArrayList<Book> books) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final String[] responseFromServer = new String[1];
        var response = asynchronousClient.saveMultipleBooks(new StreamObserver<HelloReply>() {
            @Override
            public void onNext(HelloReply helloReply) {
                responseFromServer[0] = helloReply.getMessage();
            }

            @Override
            public void onError(Throwable throwable) {
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });
        books.stream().forEach(response::onNext);
        response.onCompleted();
        boolean await = countDownLatch.await(1, TimeUnit.MINUTES);
        return await ? responseFromServer[0] : "Server did not respond to request";
    }
}
