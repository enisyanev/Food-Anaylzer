package bg.sofia.uni.fmi.mjt.foodanalyzer.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final String ERROR_MESSAGE = "Can't connect to server! Make sure the server is running!";

    private static final String HOST = "localhost";
    private static final int PORT = 7777;

    public static void main(String[] args) {
        new Client().start();
    }

    private void start() {
        try (Socket socket = new Socket(HOST, PORT);
             Scanner scanner = new Scanner(System.in);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            new Thread(new ServerResponse(reader)).start();

            while (scanner.hasNextLine()) {
                writer.println(scanner.nextLine());
            }

        } catch (IOException e) {
            System.out.println(ERROR_MESSAGE);
        }
    }

}
