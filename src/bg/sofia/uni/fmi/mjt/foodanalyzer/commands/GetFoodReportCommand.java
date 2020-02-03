package bg.sofia.uni.fmi.mjt.foodanalyzer.commands;

import bg.sofia.uni.fmi.mjt.foodanalyzer.CacheUtils;
import bg.sofia.uni.fmi.mjt.foodanalyzer.Constants;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodReportEntity;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodReportResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

public class GetFoodReportCommand implements Command {

    private static final int ALLOWED_NUMBER_OF_ARGUMENTS = 2;
    private static final int FDC_ID = 1;

    private static final Gson gson = new Gson();
    private static final Type type = new TypeToken<List<FoodReportEntity>> () {}.getType();

    private CacheUtils<FoodReportEntity> cacheUtils;
    private HttpClient httpClient;

    public GetFoodReportCommand(CacheUtils<FoodReportEntity> cacheUtils, HttpClient httpClient) {
        this.cacheUtils = cacheUtils;
        this.httpClient = httpClient;
    }

    @Override
    public void execute(String[] parameters, PrintWriter writer) {
        if (!validate(parameters)) {
            writer.println(Constants.WRONG_COMMAND_MESSAGE);
            return;
        }

        String fdcId = parameters[FDC_ID];

        Optional<FoodReportEntity> cacheHit = checkCache(fdcId);

        if (cacheHit.isPresent()) {
            writer.println(cacheHit.get().getResponse());
            return;
        }

        FoodReportResponse response = callApi(fdcId);

        if (response == null || response.getDescription() == null) {
            writer.println(Constants.NO_RESULT_FOUND_MESSAGE);
            return;
        }

        saveInCache(fdcId, response);

        writer.println(response);
    }

    private Optional<FoodReportEntity> checkCache(String fdcId) {

        try (JsonReader reader = new JsonReader(new FileReader(Constants.FOOD_REPORT_JSON_FILE))) {

            return cacheUtils.lookup(reader, x -> x.getFdcId().equals(fdcId), type);

        } catch (IOException e) {
            System.out.println(Constants.ERROR_READING_FROM_CACHE_MESSAGE);
            return Optional.empty();
        }
    }

    private FoodReportResponse callApi(String fdcId) {
        HttpRequest request = buildRequest(fdcId);

        try {
            String response = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return gson.fromJson(response, FoodReportResponse.class);
        } catch (IOException | InterruptedException e) {
            System.out.println(Constants.ERROR_COMMUNICATING_WITH_API_MESSAGE);
            return null;
        }
    }

    private synchronized void saveInCache(String fdcId, FoodReportResponse response) {

        try (Reader reader = new FileReader(Constants.FOOD_REPORT_JSON_FILE)) {

            List<FoodReportEntity> existingEntries = cacheUtils.readAll(reader, type);
            FoodReportEntity newEntry = new FoodReportEntity(fdcId, response);

            try (Writer writer = new FileWriter(Constants.FOOD_REPORT_JSON_FILE)) {
                cacheUtils.saveInCache(writer, newEntry, existingEntries);
            }

        } catch (IOException e) {
            System.out.println(Constants.ERROR_SAVING_TO_CACHE_MESSAGE);
        }
    }

    private HttpRequest buildRequest(String fdcId) {
        String url = Constants.API_URL + fdcId + Constants.API_KEY_URL + Constants.API_KEY;

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
    }

    private boolean validate(String[] parameters) {
        return parameters.length == ALLOWED_NUMBER_OF_ARGUMENTS;
    }
}
