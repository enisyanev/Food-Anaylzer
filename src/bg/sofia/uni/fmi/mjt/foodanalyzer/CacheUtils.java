package bg.sofia.uni.fmi.mjt.foodanalyzer;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class CacheUtils<T> {

    private static final Gson gson = new Gson();

    public synchronized Optional<T> lookup(JsonReader reader, Predicate<T> predicate, Type type) {
        List<T> entries = gson.fromJson(reader, type);

        if (entries == null) {
            return Optional.empty();
        }

        return entries.stream()
                .filter(predicate)
                .findFirst();
    }

    public synchronized void saveInCache(Writer writer, T newEntry, List<T> existingEntries) {
        if (existingEntries == null) {
            existingEntries = new ArrayList<>();
        }

        existingEntries.add(newEntry);

        gson.toJson(existingEntries, writer);
    }

    public synchronized List<T> readAll(Reader reader, Type type) {
        return gson.fromJson(reader, type);
    }

}
