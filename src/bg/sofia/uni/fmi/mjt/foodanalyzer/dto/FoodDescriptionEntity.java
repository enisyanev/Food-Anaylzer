package bg.sofia.uni.fmi.mjt.foodanalyzer.dto;

import java.util.List;

public class FoodDescriptionEntity {

    private String searchedFood;
    private List<Food> food;

    public FoodDescriptionEntity() {
        // default constructor for serialization
    }

    public FoodDescriptionEntity(String searchedFood, List<Food> food) {
        this.searchedFood = searchedFood;
        this.food = food;
    }

    public String getSearchedFood() {
        return searchedFood;
    }

    public void setSearchedFood(String searchedFood) {
        this.searchedFood = searchedFood;
    }

    public List<Food> getFoods() {
        return food;
    }

    public void setFoods(List<Food> food) {
        this.food = food;
    }
}
