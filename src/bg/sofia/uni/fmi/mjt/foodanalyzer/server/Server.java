package bg.sofia.uni.fmi.mjt.foodanalyzer.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int NUMBER_OF_THREADS = 20;
    private static final int PORT = 7777;

    private static final String ERROR_MESSAGE = "Maybe another process is running on port " + PORT;

    public static void main(String[] args) {
        new Server().startServer();
    }

    private void startServer() {
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            Socket clientSocket;

            while (true) {
                clientSocket = serverSocket.accept();

                ClientRequestHandler handler = new ClientRequestHandler(clientSocket);

                executor.execute(handler);
            }

        } catch (IOException e) {
            System.out.println(ERROR_MESSAGE);
        }

        executor.shutdown();
    }
}
