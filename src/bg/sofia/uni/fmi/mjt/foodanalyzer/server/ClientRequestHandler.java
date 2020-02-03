package bg.sofia.uni.fmi.mjt.foodanalyzer.server;

import bg.sofia.uni.fmi.mjt.foodanalyzer.CacheUtils;
import bg.sofia.uni.fmi.mjt.foodanalyzer.Constants;
import bg.sofia.uni.fmi.mjt.foodanalyzer.commands.Command;
import bg.sofia.uni.fmi.mjt.foodanalyzer.commands.GetFoodByBarcodeCommand;
import bg.sofia.uni.fmi.mjt.foodanalyzer.commands.GetFoodCommand;
import bg.sofia.uni.fmi.mjt.foodanalyzer.commands.GetFoodReportCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;

public class ClientRequestHandler implements Runnable {

    private static final String WHITESPACE_REGEX = "\\s+";

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Map<String, Command> commands = initializeCommands();

    private Socket clientSocket;

    public ClientRequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            processRequests(reader, writer);

        } catch (IOException e) {
            System.out.println(Constants.ERROR_OCCURRED_MESSAGE + e.getMessage());
        }
    }

    void processRequests(BufferedReader reader, PrintWriter writer) throws IOException {
        String message;
        while ((message = reader.readLine()) != null) {
            String[] arguments = message.split(WHITESPACE_REGEX);

            if (!commands.containsKey(arguments[0])) {
                writer.println(Constants.WRONG_COMMAND_MESSAGE);
            } else {
                commands.get(arguments[0]).execute(arguments, writer);
            }
        }
    }

    private static Map<String, Command> initializeCommands() {
        Map<String, Command> initializer = new HashMap<>();

        initializer.put(ClientCommands.GET_FOOD.getCommand(),
                new GetFoodCommand(new CacheUtils<>(), httpClient));

        initializer.put(ClientCommands.GET_FOOD_REPORT.getCommand(),
                new GetFoodReportCommand(new CacheUtils<>(), httpClient));

        initializer.put(ClientCommands.GET_FOOD_BY_BARCODE.getCommand(),
                new GetFoodByBarcodeCommand(new CacheUtils<>()));

        return initializer;
    }
}
