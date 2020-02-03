package bg.sofia.uni.fmi.mjt.foodanalyzer.commands;

import bg.sofia.uni.fmi.mjt.foodanalyzer.CacheUtils;
import bg.sofia.uni.fmi.mjt.foodanalyzer.Constants;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodReportEntity;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodReportResponse;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.Nutrient;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.Nutrients;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class GetFoodReportCommandTest {

    private static final String[] INVALID_COMMAND = new String[]{"get-food-report", "1234", "1234"};
    private static final String[] VALID_COMMAND = new String[]{"get-food-report", "1234"};

    private static final String FDC_ID = "1234";

    private static final String RESPONSE_JSON = "{\"description\":\"RAFFAELLO, ALMOND COCONUT TREAT\"," +
            "\"ingredients\":\"VEGETABLE OILS (PALM AND SHEANUT). DRY COCONUT\",\"labelNutrients\":" +
            "{\"calories\":{\"value\":189.9},\"protein\":{\"value\":2.001},\"fat\":" +
            "{\"value\":15.0},\"carbohydrates\":{\"value\":12.0},\"fiber\":{\"value\":0.99}}}";

    private static final String DESCRIPTION = "RAFFAELLO, ALMOND COCONUT TREAT";
    private static final String INGREDIENTS = "VEGETABLE OILS (PALM AND SHEANUT). DRY COCONUT";
    private static final double CALORIES = 189.9;
    private static final double FAT = 15.0;
    private static final double PROTEIN = 2.001;
    private static final double CARBOHYDRATES = 12.0;
    private static final double FIBER = 0.99;

    @Mock
    private CacheUtils<FoodReportEntity> cacheUtils;
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

        when(cacheUtils.lookup(any(), any(), any())).thenReturn(Optional.empty());

        command = new GetFoodReportCommand(cacheUtils, client);
    }

    @Test
    public void testInvalidCommand() {

        command.execute(INVALID_COMMAND, writer);

        verify(writer).println(Constants.WRONG_COMMAND_MESSAGE);
        verifyNoMoreInteractions(writer);
    }

    @Test
    public void testEntityInCache() {

        FoodReportEntity entity = mockEntity();

        when(cacheUtils.lookup(any(), any(), any())).thenReturn(Optional.of(entity));

        command.execute(VALID_COMMAND, writer);

        verify(writer).println(entity.getResponse());
        verifyNoMoreInteractions(writer);
    }

    @Test
    public void testNoEntityInCacheCallApi() throws IOException, InterruptedException {

        when(client.send(any(), any())).thenReturn(httpResponse);

        command.execute(VALID_COMMAND, writer);

        verify(client).send(any(), any());

    }

    @Test
    public void testNoResponseFromApi() throws IOException, InterruptedException {

        when(client.send(mockRequest(), HttpResponse.BodyHandlers.ofString())).thenReturn(httpResponse);

        command.execute(VALID_COMMAND, writer);

        verify(writer).println(Constants.NO_RESULT_FOUND_MESSAGE);
        verifyNoMoreInteractions(writer);
    }

    @Test
    public void testGetResponseFromApiThenSaveInCache() throws IOException, InterruptedException {

        when(client.send(mockRequest(), HttpResponse.BodyHandlers.ofString())).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(RESPONSE_JSON);

        command.execute(VALID_COMMAND, writer);

        verify(cacheUtils).saveInCache(any(), any(), any());

    }

    private HttpRequest mockRequest() {
        String url = Constants.API_URL + FDC_ID + Constants.API_KEY_URL + Constants.API_KEY;

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
    }

    private FoodReportEntity mockEntity() {
        return new FoodReportEntity(FDC_ID, mockResponse());
    }

    private FoodReportResponse mockResponse() {
        FoodReportResponse response = new FoodReportResponse();
        response.setDescription(DESCRIPTION);
        response.setIngredients(INGREDIENTS);
        response.setLabelNutrients(mockNutrients());

        return response;
    }

    private Nutrients mockNutrients() {
        Nutrients nutrients = new Nutrients();
        nutrients.setCalories(mockNutrient(CALORIES));
        nutrients.setFat(mockNutrient(FAT));
        nutrients.setProtein(mockNutrient(PROTEIN));
        nutrients.setCarbohydrates(mockNutrient(CARBOHYDRATES));
        nutrients.setFiber(mockNutrient(FIBER));

        return nutrients;
    }

    private Nutrient mockNutrient(double value) {
        Nutrient nutrient = new Nutrient();
        nutrient.setValue(value);

        return nutrient;
    }

}
