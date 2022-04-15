package pl.edu.pwr.rsi.grpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.edu.pwr.rsi.grpc.gui.ClientGuiMy;

import java.awt.*;

@SpringBootApplication
public class ClientApplication {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(ClientApplication.class, args);
    }
}
