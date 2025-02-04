package com.javaadvanced.service;

import com.javaadvanced.model.Row;
import com.javaadvanced.model.KeyValuePair;
import com.javaadvanced.utils.AbstractFileHandler;
import com.javaadvanced.utils.TxtFileHandler; 
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class KeyValueManager {
    private List<Row> data;
    public AbstractFileHandler fileHandler;
    private final String filePath;

    public KeyValueManager(String filePath) {
        this.filePath = filePath;
        fileHandler = new TxtFileHandler(filePath);
        data = new ArrayList<>();
        
    }

    public void loadData() throws IOException {
        data = fileHandler.parseFile();
    }

    public void saveData() {
        fileHandler.saveToFile(filePath, data);
    }

    public List<Row> getData() {
        return data;
    }


    public void createFileFromResource() {
        System.out.println("Loading default resource data...");
        try (InputStream resourceStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("default_data.txt")) {
            if (resourceStream == null) {
                throw new IOException("Default resource file not found in the classpath.");
            }

            // Use the resourceStream, for example, to parse the file
            data = fileHandler.parseStream(resourceStream);
            fileHandler.saveToFile(filePath, data);
            System.out.println("File created successfully at " + filePath + " using default data.");
            print2DStructure();
        } catch (IOException e) {
            System.out.println("Error loading default resource data: " + e.getMessage());
            System.exit(0);
        }

    }

    public void searchPatt(String target) {
        List<String> results = new ArrayList<>();
        int count = 0;

        for (int i = 0; i < data.size(); i++) {
            Row row = data.get(i);
            for (int j = 0; j < row.getCells().size(); j++) {
                KeyValuePair pair = row.getCells().get(j);
                int keyMatches = countOccurrences(pair.getKey(), target);
                int valueMatches = countOccurrences(pair.getValue(), target);

                if (keyMatches > 0 || valueMatches > 0) {
                    results.add(String.format("%dx%d", i, j));
                    count += keyMatches + valueMatches;
                }
            }
        }

        if (count > 0) {
            System.out.println("Target found at: " + String.join(", ", results));
            System.out.println("Occurrences: " + count);
        } else {
            System.out.println("Target not found.");
        }
    }

    public void editKeyOrValue(int rowIndex, int colIndex, boolean isKey, String newValue, String[] newValueForBoth) {
        Row row = data.get(rowIndex);
        KeyValuePair pair = row.getCells().get(colIndex);

        if (newValueForBoth != null) {
            pair.setKey(newValueForBoth[0]);
            pair.setValue(newValueForBoth[1]);
            System.out.println("Updated both key and value at " + rowIndex + "x" + colIndex + ": " + pair);
        } else if (isKey) {
            pair.setKey(newValue);
            System.out.println("Updated key at " + rowIndex + "x" + colIndex + ": " + pair);
        } else {
            pair.setValue(newValue);
            System.out.println("Updated value at " + rowIndex + "x" + colIndex + ": " + pair);
        }
        saveData();
    }

    public void addRow(int rowIndex, int numCells) {
        Row newRow = new Row();
        for (int i = 0; i < numCells; i++) {
            newRow.addCell(new KeyValuePair(generateAscii(), generateAscii()));
        }
        data.add(rowIndex, newRow);
        saveData();
    }

    public void sortRow(int rowIndex, String sortOrder) {
        Row rowToSort = data.get(rowIndex);
        rowToSort.sortIndexRow(sortOrder);
        saveData();
    }

    public void resetData(int rows, int cols) {
        data.clear();
        for (int i = 0; i < rows; i++) {
            Row row = new Row();
            for (int j = 0; j < cols; j++) {
                row.addCell(new KeyValuePair(generateAscii(), generateAscii()));
            }
            data.add(row);
        }
        saveData();
    }

    public void print2DStructure() {
        data.forEach(row -> {
            row.getCells().forEach(cell -> System.out.print(cell + " "));
            System.out.println();
        });
    }

    private String generateAscii() {
        return new Random().ints(3, 32, 127)
            .mapToObj(c -> String.valueOf((char) c))
            .collect(Collectors.joining());
    }
    private int countOccurrences(String text, String target) {
        int count = (int) IntStream.range(0, text.length() - target.length() + 1 )
        .filter(i -> text.substring(i, i + target.length()).equals(target))
        .count();

        return count;
    }
}