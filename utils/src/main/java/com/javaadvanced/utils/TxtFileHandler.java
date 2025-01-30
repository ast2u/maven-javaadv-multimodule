package com.javaadvanced.utils;

import com.javaadvanced.model.Row;
import com.javaadvanced.model.KeyValuePair;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TxtFileHandler extends AbstractFileHandler {

    public TxtFileHandler(String filePath) {
        super(filePath);
    }

    @Override
    public List<Row> parseFile() throws IOException {
        List<Row> rows = new ArrayList<>();
        List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8); // Read all lines using UTF-8 encoding
        for (String line : lines) {
            rows.add(parseLine(line));
        }
        return rows;
    }

    @Override
    public List<Row> parseStream(InputStream inputStream) throws IOException {
        List<Row> rows = new ArrayList<>();
        List<String> lines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
        for (String line : lines) {
            rows.add(parseLine(line));
        }
        return rows;
    }


    private Row parseLine(String line) {
        Row row = new Row();
        Arrays.stream(line.trim().split(PAIR_DELIMITER))
                .map(pair -> pair.replaceAll("^\\[|\\]$", ""))
                .forEach(pair -> {
                    if (!pair.contains(DELIMITER)) {
                        int midPoint = pair.length() / 2;
                        pair = pair.substring(0, midPoint) + DELIMITER + pair.substring(midPoint);
                    }
                    String[] keyValue = pair.split(DELIMITER, 2);
                    String value = keyValue.length > 1 ? keyValue[1] : "";
                    row.addCell(new KeyValuePair(keyValue[0], value));
                });
        return row;
    }

    @Override
    public void saveToFile(String filePath, List<Row> rows) {
        try {
            // Convert rows to a single string
            String data = rows.stream()
                .map(row -> row.getCells().stream()
                    .map(cell -> String.format("[%s%s%s]", cell.getKey(), DELIMITER, cell.getValue()))
                    .collect(Collectors.joining(PAIR_DELIMITER)))
                .collect(Collectors.joining(System.lineSeparator()));

            // Write string to file using FileUtils
            FileUtils.writeStringToFile(new File(filePath), data, StandardCharsets.UTF_8);

            System.out.println("\nData saved to " + filePath + " successfully.");
        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }
}
