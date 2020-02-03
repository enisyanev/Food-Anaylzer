package bg.sofia.uni.fmi.mjt.foodanalyzer.dto;

public class Nutrients {

    private Nutrient calories;
    private Nutrient protein;
    private Nutrient fat;
    private Nutrient carbohydrates;
    private Nutrient fiber;

    public Nutrients() {
        // default constructor for serialization
    }

    public Nutrient getCalories() {
        return calories;
    }

    public void setCalories(Nutrient calories) {
        this.calories = calories;
    }

    public Nutrient getProtein() {
        return protein;
    }

    public void setProtein(Nutrient protein) {
        this.protein = protein;
    }

    public Nutrient getFat() {
        return fat;
    }

    public void setFat(Nutrient fat) {
        this.fat = fat;
    }

    public Nutrient getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(Nutrient carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public Nutrient getFiber() {
        return fiber;
    }

    public void setFiber(Nutrient fiber) {
        this.fiber = fiber;
    }

    @Override
    public String toString() {
        return "calories=" + calories +
                ", protein=" + protein +
                ", fat=" + fat +
                ", carbohydrates=" + carbohydrates +
                ", fiber=" + fiber;
    }
}
