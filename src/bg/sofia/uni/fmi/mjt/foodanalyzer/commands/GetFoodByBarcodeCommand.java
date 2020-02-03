package bg.sofia.uni.fmi.mjt.foodanalyzer.commands;

import bg.sofia.uni.fmi.mjt.foodanalyzer.CacheUtils;
import bg.sofia.uni.fmi.mjt.foodanalyzer.Constants;
import bg.sofia.uni.fmi.mjt.foodanalyzer.dto.FoodReportEntity;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class GetFoodByBarcodeCommand implements Command {

    private static final int MIN_ALLOWERD_ARGUMENTS = 2;
    private static final int MAX_ALLOWED_ARGUMENTS = 3;
    private static final int CLIENT_COMMAND = 1;

    private CacheUtils<FoodReportEntity> cacheUtils;

    public GetFoodByBarcodeCommand(CacheUtils<FoodReportEntity> cacheUtils) {
        this.cacheUtils = cacheUtils;
    }

    @Override
    public void execute(String[] parameters, PrintWriter writer) {
        if (!validate(parameters)) {
            writer.println(Constants.WRONG_COMMAND_MESSAGE);
            return;
        }

        if (shouldLookByCode(parameters)) {
            lookByCode(parameters, writer);
        } else {
            lookByImage(parameters, writer);
        }
    }

    private void lookByCode(String[] parameters, PrintWriter writer) {
        String upcCode = getUpcCode(parameters);

        Optional<FoodReportEntity> entry = lookup(upcCode);

        printResult(writer, entry);
    }

    private void lookByImage(String[] parameters, PrintWriter writer) {
        String image = getImageName(parameters);

        File file = new File(image);

        if (!file.exists()) {
            writer.println(Constants.NO_SUCH_FILE_MESSAGE);
            return;
        }

        Optional<String> qrCode = decodeQrCode(file);

        if (qrCode.isEmpty()) {
            writer.println(Constants.NO_RESULT_FOUND_MESSAGE);
            return;
        }

        Optional<FoodReportEntity> foodReportEntity = lookup(qrCode.get());

        printResult(writer, foodReportEntity);
    }

    private String getImageName(String[] parameters) {
        return parameters[1].replace(Constants.IMG_PREFIX, "");
    }

    private Optional<String> decodeQrCode(File imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            LuminanceSource luminanceSource = new BufferedImageLuminanceSource(image);
            Binarizer binarizer = new HybridBinarizer(luminanceSource);
            BinaryBitmap bitmap = new BinaryBitmap(binarizer);

            Result qrCodeResult = new MultiFormatReader().decode(bitmap);

            return Optional.of(qrCodeResult.getText());
        } catch (IOException | NotFoundException e) {
            System.out.println(Constants.ERROR_DECODING_QR_CODE_MESSAGE);
            return Optional.empty();
        }
    }

    private void printResult(PrintWriter writer, Optional<FoodReportEntity> foodReportEntity) {
        if (foodReportEntity.isPresent()) {
            writer.println(foodReportEntity.get().getResponse());
        } else {
            writer.println(Constants.NO_RESULT_FOUND_MESSAGE);
        }
    }

    private Optional<FoodReportEntity> lookup(String upcCode) {

        try (JsonReader reader = new JsonReader(new FileReader(Constants.FOOD_REPORT_JSON_FILE))) {

            return cacheUtils.lookup(reader, x -> upcCode.equals(x.getResponse().getGtinUpc()),
                    new TypeToken<List<FoodReportEntity>>() {
                    }.getType());

        } catch (IOException e) {
            System.out.println(Constants.ERROR_READING_FROM_CACHE_MESSAGE);
            return Optional.empty();
        }

    }

    private String getUpcCode(String[] parameters) {
        String command = Stream.of(parameters).filter(x -> x.startsWith(Constants.CODE_PREFIX))
                .findFirst()
                .orElse("");
        return command.replace(Constants.CODE_PREFIX, "");
    }

    private boolean shouldLookByCode(String[] parameters) {
        return Stream.of(parameters)
                .skip(CLIENT_COMMAND)
                .anyMatch(x -> x.startsWith(Constants.CODE_PREFIX));
    }

    private boolean validate(String[] parameters) {
        return parameters.length >= MIN_ALLOWERD_ARGUMENTS && parameters.length <= MAX_ALLOWED_ARGUMENTS
                && Stream.of(parameters)
                .skip(CLIENT_COMMAND)
                .anyMatch(x -> x.startsWith(Constants.CODE_PREFIX) || x.startsWith(Constants.IMG_PREFIX));
    }
}
