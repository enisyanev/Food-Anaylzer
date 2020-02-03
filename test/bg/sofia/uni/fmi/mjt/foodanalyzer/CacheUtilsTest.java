package bg.sofia.uni.fmi.mjt.foodanalyzer;

import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodReportEntity;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class CacheUtilsTest {

    private static final String FOOD_REPORT_JSON = "[{\"fdcId\":\"1234\"}]";
    private static final String FDC_ID = "1234";

    private Type type = new TypeToken<List<FoodReportEntity>>() {}.getType();
    private StringWriter writer = new StringWriter();
    private JsonReader reader;

    private static CacheUtils<FoodReportEntity> cacheUtils;

    @BeforeClass
    public static void init() {
        cacheUtils = new CacheUtils<>();
    }

    @Test
    public void testLookupWithNoResult() {

        reader = new JsonReader(new StringReader(""));

        Optional<FoodReportEntity> lookup = cacheUtils.lookup(reader, null, type);

        assertEquals(lookup, Optional.empty());
    }

    @Test
    public void testLookupWithResult() {
        reader = new JsonReader(new StringReader(FOOD_REPORT_JSON));

        Optional<FoodReportEntity> lookup = cacheUtils.lookup(reader, Objects::nonNull, type);

        assertEquals(lookup.get().getFdcId(), FDC_ID);
    }

    @Test
    public void testSaveInCache() {
        cacheUtils.saveInCache(writer, new FoodReportEntity(FDC_ID, null), null);

        assertEquals(writer.toString(), FOOD_REPORT_JSON);
    }

}
