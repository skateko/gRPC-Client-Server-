package pl.edu.pwr.rsi.grpc.config;

import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.edu.pwr.rsi.grpc.interfaces.lib.FileServiceGrpc;
import pl.edu.pwr.rsi.grpc.interfaces.lib.MyServiceGrpc;

@Configuration
public class GrpcConfig {

    @Bean
    public FileServiceGrpc.FileServiceStub getStubFileService(){
        var managedChannel =  ManagedChannelBuilder.forAddress("localhost", 9000)
                .usePlaintext()
                .build();
        return FileServiceGrpc.newStub(managedChannel);
    }

    @Bean
    public MyServiceGrpc.MyServiceBlockingStub getStubBlockingMyService(){
        var managedChannel =  ManagedChannelBuilder.forAddress("localhost", 9000)
                .usePlaintext()
                .build();
        return MyServiceGrpc.newBlockingStub(managedChannel);
    }

    @Bean
    public MyServiceGrpc.MyServiceStub getStubMyServiceStub(){
        var managedChannel =  ManagedChannelBuilder.forAddress("localhost", 9000)
                .usePlaintext()
                .build();
        return MyServiceGrpc.newStub(managedChannel);
    }
}
