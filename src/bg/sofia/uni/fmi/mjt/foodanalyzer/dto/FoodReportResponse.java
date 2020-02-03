package bg.sofia.uni.fmi.mjt.foodanalyzer.dto;

public class FoodReportResponse {

    private String description;
    private String gtinUpc;
    private String ingredients;
    private Nutrients labelNutrients;

    public FoodReportResponse() {
        // default constructor for serialization
    }

    public String getDescription() {
        return description;
    }

    public String getGtinUpc() {
        return gtinUpc;
    }

    public void setGtinUpc(String gtinUpc) {
        this.gtinUpc = gtinUpc;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public Nutrients getLabelNutrients() {
        return labelNutrients;
    }

    public void setLabelNutrients(Nutrients labelNutrients) {
        this.labelNutrients = labelNutrients;
    }

    @Override
    public String toString() {
        return "Name:" + description + "\nIngredients: " + ingredients + "\n" + labelNutrients;
    }
}
