package bg.sofia.uni.fmi.mjt.foodanalyzer.dto;

import java.util.List;

public class FoodSearchResponse {

    private List<Food> foods;

    public FoodSearchResponse() {
        // default constructor for serialization
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }
}
