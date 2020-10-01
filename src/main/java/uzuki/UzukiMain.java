package uzuki;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class UzukiMain {
    public static void main(String[] args) throws IOException, URISyntaxException, ExecutionException, InterruptedException {
        System.setProperty("vertx.disableDnsResolver", "true");
        new UzukiServer()
                .buildRest()
                .startServer();
    }
}
