package bg.sofia.uni.fmi.mjt.foodanalyzer.client;

import java.io.Reader;
import java.util.Scanner;

public class ServerResponse implements Runnable {

    private Reader reader;

    public ServerResponse(Reader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        try (Scanner scanner = new Scanner(reader)) {
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
    }
}
