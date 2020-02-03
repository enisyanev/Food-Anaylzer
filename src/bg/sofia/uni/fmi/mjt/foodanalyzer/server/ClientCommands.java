package bg.sofia.uni.fmi.mjt.foodanalyzer.server;

public enum ClientCommands {

    GET_FOOD("get-food"),
    GET_FOOD_REPORT("get-food-report"),
    GET_FOOD_BY_BARCODE("get-food-by-barcode");

    private String command;

    ClientCommands(String command) {
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }

}
