package com.javaadvanced.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.javaadvanced.model.KeyValuePair;
import com.javaadvanced.model.Row;
import com.javaadvanced.model.SearchResult;
import com.javaadvanced.utils.AbstractFileHandler;
import com.javaadvanced.utils.TxtFileHandler;

public class KeyValueServiceImpl implements IKeyValueService {

    private List<Row> data;
    public AbstractFileHandler fileHandler;
    private String filePath;

    public KeyValueServiceImpl(String filePath) {
        this.filePath = filePath;
        fileHandler = new TxtFileHandler(filePath);
        data = new ArrayList<>();

    }

    @Override
    public void loadData() throws IOException {
        data = fileHandler.parseFile();
    }

    @Override
    public void saveData() {
        fileHandler.saveToFile(filePath, data);
    }

    @Override
    public List<Row> getData() {
        return data;
    }

    @Override
    public void createFileFromResource() {
        System.out.println("Loading default resource data...");
        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("default_data.txt")) {
            if (resourceStream == null) {
                throw new IOException("Default resource file not found in the module's resources.");
            }
            data = fileHandler.parseStream(resourceStream);
            fileHandler.saveToFile(filePath, data);
            System.out.println("File created successfully at " + filePath + " using default data.");
            print2DStructure();
        } catch (IOException e) {
            throw new RuntimeException("Error loading default resource data: " + e.getMessage());
        }
    }

    @Override
    public SearchResult searchPatt(String target) {
        List<String> searchResults = new ArrayList<>();
        int matchCount = 0;

        for (int i = 0; i < data.size(); i++) {
            Row row = data.get(i);
            for (int j = 0; j < row.getCells().size(); j++) {
                KeyValuePair pair = row.getCells().get(j);
                int keyMatches = countOccurrences(pair.getKey(), target);
                int valueMatches = countOccurrences(pair.getValue(), target);

                if (keyMatches > 0 || valueMatches > 0) {
                    searchResults.add(String.format("%dx%d", i, j));
                    matchCount += keyMatches + valueMatches;
                }
            }
        }

        return new SearchResult(matchCount, searchResults);
    }

    @Override
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

    @Override
    public void addRow(int rowIndex, int numCells) {
        Row newRow = new Row();
        for (int i = 0; i < numCells; i++) {
            newRow.addCell(new KeyValuePair(generateAscii(), generateAscii()));
        }
        data.add(rowIndex, newRow);
        saveData();
    }

    @Override
    public void sortRow(int rowIndex, String sortOrder) {
        Row rowToSort = data.get(rowIndex);
        rowToSort.sortIndexRow(sortOrder);
        saveData();
    }

    @Override
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

    @Override
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

    public int countOccurrences(String text, String target) {
        if (text == null || target == null || target.isEmpty()) {
            return 0;
        }
        return text.split(target, -1).length - 1;
    }

}
