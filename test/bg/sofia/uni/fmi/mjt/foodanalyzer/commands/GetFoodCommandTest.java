package bg.sofia.uni.fmi.mjt.foodanalyzer.commands;

import bg.sofia.uni.fmi.mjt.foodanalyzer.CacheUtils;
import bg.sofia.uni.fmi.mjt.foodanalyzer.Constants;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.Food;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodDescriptionEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class GetFoodCommandTest {

    private static final String[] INVALID_COMMAND = {"get-food"};
    private static final String[] VALID_COMMAND = {"get-food", "raffaello", "treat"};
    private static final String API_RESPONSE = "{\"foods\":[{\"fdcId\":415269,\"description\":\"RAFFAELLO, " +
            "ALMOND COCONUT TREAT\",\"gtinUpc\":\"009800146130\"}]}";

    private static final String DESCRIPTION = "RAFFAELLO, ALMOND COCONUT TREAT";
    private static final int FDC_ID = 415269;
    private static final String GTIN_UPC = "009800146130";
    private static final String API_FOOD_STRING = "raffaello%20treat";
    private static final String SEARCHED_FOOD = "vafla";

    @Mock
    private CacheUtils<FoodDescriptionEntity> cacheUtils;
    @Mock
    private PrintWriter writer;
    @Mock
    private HttpClient client;
    @Mock
    private HttpResponse httpResponse;

    private Command command;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        command = new GetFoodCommand(cacheUtils, client);
    }

    @Test
    public void testInvalidCommand() {

        command.execute(INVALID_COMMAND, writer);

        verify(writer).println(Constants.WRONG_COMMAND_MESSAGE);
        verifyNoMoreInteractions(writer);
    }

    @Test
    public void testGetEntityFromCache() {

        FoodDescriptionEntity entity = mockEntity();

        when(cacheUtils.lookup(any(), any(), any())).thenReturn(Optional.of(entity));

        command.execute(VALID_COMMAND, writer);

        verify(writer).println(entity.getFoods().get(0));
        verifyNoMoreInteractions(writer);
    }

    @Test
    public void testNoEntityInCacheAndNoResponseFromApi() throws IOException, InterruptedException {

        when(cacheUtils.lookup(any(), any(), any())).thenReturn(Optional.empty());
        when(client.send(getRequest(), HttpResponse.BodyHandlers.ofString())).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(null);

        command.execute(VALID_COMMAND, writer);

        verify(writer).println(Constants.NO_RESULT_FOUND_MESSAGE);
        verifyNoMoreInteractions(writer);
    }

    @Test
    public void testCallApiAndSaveInCache() throws IOException, InterruptedException {

        when(cacheUtils.lookup(any(), any(), any())).thenReturn(Optional.empty());
        when(client.send(getRequest(), HttpResponse.BodyHandlers.ofString())).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(API_RESPONSE);

        command.execute(VALID_COMMAND, writer);

        verify(cacheUtils).saveInCache(any(), any(), any());
    }

    @Test
    public void testErrorWhileCommunicateWithApi() throws IOException, InterruptedException {

        when(cacheUtils.lookup(any(), any(), any())).thenReturn(Optional.empty());
        when(client.send(getRequest(), HttpResponse.BodyHandlers.ofString())).thenThrow(IOException.class);

        command.execute(VALID_COMMAND, writer);

        verify(writer).println(Constants.NO_RESULT_FOUND_MESSAGE);

    }

    private HttpRequest getRequest() {
        String url = (Constants.API_URL + Constants.SEARCH_FOOD_URL
                + API_FOOD_STRING).replace(Constants.API_KEY_LITERAL, Constants.API_KEY);
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
    }

    private FoodDescriptionEntity mockEntity() {
        FoodDescriptionEntity entity = new FoodDescriptionEntity();
        Food food = mockFood();
        entity.setFoods(Arrays.asList(food));
        entity.setSearchedFood(SEARCHED_FOOD);

        return entity;
    }

    private Food mockFood() {
        Food food = new Food();
        food.setDescription(DESCRIPTION);
        food.setFdcId(FDC_ID);
        food.setGtinUpc(GTIN_UPC);

        return food;
    }

}
