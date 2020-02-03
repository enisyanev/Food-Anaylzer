package bg.sofia.uni.fmi.mjt.foodanalyzer.commands;

import java.io.PrintWriter;

public interface Command {

    void execute(String[] parameters, PrintWriter writer);

}
