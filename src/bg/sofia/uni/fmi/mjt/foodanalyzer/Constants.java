package bg.sofia.uni.fmi.mjt.foodanalyzer;

public class Constants {

    private Constants() {
        // No.
    }

    // Error messages
    public static final String WRONG_COMMAND_MESSAGE = "Wrong command!";
    public static final String ERROR_READING_FROM_CACHE_MESSAGE = "Error while reading from cache!";
    public static final String ERROR_SAVING_TO_CACHE_MESSAGE = "Error while saving to cache!";
    public static final String ERROR_COMMUNICATING_WITH_API_MESSAGE = "Error while communicating with API";
    public static final String NO_RESULT_FOUND_MESSAGE = "No result found";
    public static final String ERROR_OCCURRED_MESSAGE = "Error occured!\n";
    public static final String NO_SUCH_FILE_MESSAGE = "There is no such file!";
    public static final String ERROR_DECODING_QR_CODE_MESSAGE = "Error decoding QR Code!";

    // File names
    public static final String FOOD_JSON_FILE = "./resources/food.json";
    public static final String FOOD_REPORT_JSON_FILE = "./resources/food-report.json";

    // API related strings
    public static final String API_KEY = "tw9P6UM3fbzCmIRkMHqiLuwjmoQpfdvNSAVArMpV";
    public static final String API_URL = "https://api.nal.usda.gov/fdc/v1/";
    public static final String REPLACEMENT = "%20";
    public static final String API_KEY_URL = "?api_key=";
    public static final String SEARCH_FOOD_URL = "search" + API_KEY_URL
            + "API_KEY&requireAllWords=true&generalSearchInput=";
    public static final String API_KEY_LITERAL = "API_KEY";

    // command prefix
    public static final String CODE_PREFIX = "--code=";
    public static final String IMG_PREFIX = "--img=";
}
