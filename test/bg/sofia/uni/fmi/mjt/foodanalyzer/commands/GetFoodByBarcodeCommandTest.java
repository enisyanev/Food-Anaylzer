package bg.sofia.uni.fmi.mjt.foodanalyzer.commands;

import bg.sofia.uni.fmi.mjt.foodanalyzer.CacheUtils;
import bg.sofia.uni.fmi.mjt.foodanalyzer.Constants;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodReportEntity;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodReportResponse;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.Nutrient;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.Nutrients;
import bg.sofia.uni.fmi.mjt.foodanalyzer.server.ClientCommands;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.net.http.HttpClient;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class GetFoodByBarcodeCommandTest {

    private static final String[] INVALID_COMMAND = new String[]{ClientCommands.GET_FOOD_BY_BARCODE.getCommand(),
            "-ig=12"};
    private static final String[] GET_BY_CODE_COMMAND = new String[]{ClientCommands.GET_FOOD_BY_BARCODE.getCommand(),
            "--img=asd",
            "--code=1234"};
    private static final String[] GET_BY_IMAGE_NO_FILE = new String[]{ClientCommands.GET_FOOD_BY_BARCODE.getCommand(),
            "--img=asd"};
    private static final String[] GET_BY_IMAGE = new String[]{ClientCommands.GET_FOOD_BY_BARCODE.getCommand(),
            "--img=/home/enisyanev/Code/FoodAnalyzer/test/bg/sofia/uni/fmi/mjt/foodanalyzer/valid_barcode.png"};
    private static final String[] GET_BY_EMPTY_BARCODE = new String[]{ClientCommands.GET_FOOD_BY_BARCODE.getCommand(),
            "--img=/home/enisyanev/Code/FoodAnalyzer/test/bg/sofia/uni/fmi/mjt/foodanalyzer/invalid_barcode.png"};

    private static final String FDC_ID = "1234";
    private static final String DESCRIPTION = "RAFFAELLO, ALMOND COCONUT TREAT";
    private static final String INGREDIENTS = "VEGETABLE OILS (PALM AND SHEANUT). DRY COCONUT";
    private static final double CALORIES = 189.9;
    private static final double FAT = 15.0;
    private static final double PROTEIN = 2.001;
    private static final double CARBOHYDRATES = 12.0;
    private static final double FIBER = 0.99;
    private static final String GTIN_UPC = "009800146130";

    @Mock
    private CacheUtils<FoodReportEntity> cacheUtils;
    @Mock
    private PrintWriter writer;
    @Mock
    private HttpClient client;

    private Command command;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        command = new GetFoodByBarcodeCommand(cacheUtils);
    }

    @Test
    public void testWrongCommand() {

        command.execute(INVALID_COMMAND, writer);

        verify(writer).println(Constants.WRONG_COMMAND_MESSAGE);
        verifyNoMoreInteractions(writer);
    }

    @Test
    public void testNoCodeInCache() {

        when(cacheUtils.lookup(any(), any(), any())).thenReturn(Optional.empty());

        command.execute(GET_BY_CODE_COMMAND, writer);

        verify(writer).println(Constants.NO_RESULT_FOUND_MESSAGE);
        verifyNoMoreInteractions(writer);
    }

    @Test
    public void testGetCodeFromCache() {

        FoodReportEntity entity = mockEntity();
        when(cacheUtils.lookup(any(), any(), any())).thenReturn(Optional.of(entity));

        command.execute(GET_BY_CODE_COMMAND, writer);

        verify(writer).println(entity.getResponse());
        verifyNoMoreInteractions(writer);
    }

    @Test
    public void testGetByImageNoFile() {

        command.execute(GET_BY_IMAGE_NO_FILE, writer);

        verify(writer).println(Constants.NO_SUCH_FILE_MESSAGE);
        verifyNoMoreInteractions(writer);
    }

    @Test
    public void testGetByBarcodeNoEntityInCache() {

        when(cacheUtils.lookup(any(), any(), any())).thenReturn(Optional.empty());

        command.execute(GET_BY_IMAGE, writer);

        verify(writer).println(Constants.NO_RESULT_FOUND_MESSAGE);
        verifyNoMoreInteractions(writer);
    }

    @Test
    public void testGetByBarcode() {

        FoodReportEntity entity = mockEntity();
        when(cacheUtils.lookup(any(), any(), any())).thenReturn(Optional.of(entity));

        command.execute(GET_BY_IMAGE, writer);

        verify(writer).println(entity.getResponse());
        verifyNoMoreInteractions(writer);
    }

    @Test
    public void testGetEmptyBarcode() {

        command.execute(GET_BY_EMPTY_BARCODE, writer);

        verify(writer).println(Constants.NO_RESULT_FOUND_MESSAGE);
        verifyNoMoreInteractions(writer);
    }

    private FoodReportEntity mockEntity() {
        return new FoodReportEntity(FDC_ID, mockResponse());
    }

    private FoodReportResponse mockResponse() {
        FoodReportResponse response = new FoodReportResponse();
        response.setDescription(DESCRIPTION);
        response.setIngredients(INGREDIENTS);
        response.setLabelNutrients(mockNutrients());
        response.setGtinUpc(GTIN_UPC);

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
