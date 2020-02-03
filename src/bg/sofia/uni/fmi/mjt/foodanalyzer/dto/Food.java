package bg.sofia.uni.fmi.mjt.foodanalyzer.dto;

public class Food {

    private long fdcId;
    private String description;
    private String gtinUpc;

    public Food() {
        // default constructor for serialization
    }

    public long getFdcId() {
        return fdcId;
    }

    public void setFdcId(long fdcId) {
        this.fdcId = fdcId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGtinUpc() {
        return gtinUpc;
    }

    public void setGtinUpc(String gtinUpc) {
        this.gtinUpc = gtinUpc;
    }

    @Override
    public String toString() {
        String output = "Name: '" + description + "', Unique ID: " + fdcId;
        if (gtinUpc != null) {
            output += ", UPC Code: " + gtinUpc;
        }
        return output;
    }
}
