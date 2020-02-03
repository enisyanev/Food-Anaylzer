package bg.sofia.uni.fmi.mjt.foodanalyzer.commands;

import bg.sofia.uni.fmi.mjt.foodanalyzer.CacheUtils;
import bg.sofia.uni.fmi.mjt.foodanalyzer.Constants;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.Food;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodDescriptionEntity;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodSearchResponse;
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

public class GetFoodCommand implements Command {

    private static final int MIN_ALLOWED_ARGUMENTS = 2;

    private static final Gson gson = new Gson();
    private static final Type type = new TypeToken<List<FoodDescriptionEntity>>() {
    }.getType();

    private CacheUtils<FoodDescriptionEntity> cacheUtils;
    private HttpClient httpClient;

    public GetFoodCommand(CacheUtils<FoodDescriptionEntity> cacheUtils, HttpClient httpClient) {
        this.cacheUtils = cacheUtils;
        this.httpClient = httpClient;
    }

    @Override
    public void execute(String[] parameters, PrintWriter writer) {
        if (!validate(parameters)) {
            writer.println(Constants.WRONG_COMMAND_MESSAGE);
            return;
        }

        String product = getProductName(parameters);

        Optional<FoodDescriptionEntity> cacheHit = checkCache(product);

        if (cacheHit.isPresent()) {
            cacheHit.get().getFoods().forEach(writer::println);
            return;
        }

        FoodSearchResponse response = callApi(product);

        if (response == null || response.getFoods().isEmpty()) {
            writer.println(Constants.NO_RESULT_FOUND_MESSAGE);
            return;
        }

        saveInCache(product, response.getFoods());

        response.getFoods().forEach(writer::println);
    }

    private Optional<FoodDescriptionEntity> checkCache(String product) {

        try (JsonReader reader = new JsonReader(new FileReader(Constants.FOOD_JSON_FILE))) {

            return cacheUtils.lookup(reader, x -> x.getSearchedFood().equalsIgnoreCase(product), type);

        } catch (IOException e) {
            System.out.println(Constants.ERROR_READING_FROM_CACHE_MESSAGE);
            return Optional.empty();
        }

    }

    private FoodSearchResponse callApi(String product) {
        HttpRequest request = buildRequest(product);

        try {
            String response = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return gson.fromJson(response, FoodSearchResponse.class);
        } catch (IOException | InterruptedException e) {
            System.out.println(Constants.ERROR_COMMUNICATING_WITH_API_MESSAGE);
            return null;
        }
    }

    private synchronized void saveInCache(String searchedFood, List<Food> foundFood) {

        try (Reader reader = new FileReader(Constants.FOOD_JSON_FILE)) {
            List<FoodDescriptionEntity> existingEntries = cacheUtils.readAll(reader, type);
            FoodDescriptionEntity newEntry = new FoodDescriptionEntity(searchedFood, foundFood);

            try (Writer writer = new FileWriter(Constants.FOOD_JSON_FILE)) {
                cacheUtils.saveInCache(writer, newEntry, existingEntries);
            }

        } catch (IOException e) {
            System.out.println(Constants.ERROR_SAVING_TO_CACHE_MESSAGE);
        }
    }

    private HttpRequest buildRequest(String product) {
        String url = (Constants.API_URL + Constants.SEARCH_FOOD_URL
                + product.replace(" ", Constants.REPLACEMENT))
                .replace(Constants.API_KEY_LITERAL, Constants.API_KEY);

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
    }

    private String getProductName(String[] parameters) {
        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < parameters.length; i++) {
            builder.append(parameters[i]).append(" ");
        }

        return builder.toString().stripTrailing();
    }

    private boolean validate(String[] parameters) {
        return parameters.length >= MIN_ALLOWED_ARGUMENTS;
    }
}
