package bg.sofia.uni.fmi.mjt.foodanalyzer.dto;

public class Nutrient {

    private double value;

    public Nutrient() {
        // default constructor for serialization
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
