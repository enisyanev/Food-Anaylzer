package bg.sofia.uni.fmi.mjt.foodanalyzer.dto;

public class FoodReportEntity {

    private String fdcId;
    private FoodReportResponse response;

    public FoodReportEntity() {
        // default constructor for serialization/deserialization
    }

    public FoodReportEntity(String fdcId, FoodReportResponse response) {
        this.fdcId = fdcId;
        this.response = response;
    }

    public String getFdcId() {
        return fdcId;
    }

    public void setFdcId(String fdcId) {
        this.fdcId = fdcId;
    }

    public FoodReportResponse getResponse() {
        return response;
    }

    public void setResponse(FoodReportResponse response) {
        this.response = response;
    }
}
