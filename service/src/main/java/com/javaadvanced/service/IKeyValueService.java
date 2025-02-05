package com.javaadvanced.service;

import com.javaadvanced.model.Row;
import com.javaadvanced.model.SearchResult;
import java.io.IOException;
import java.util.List;

public interface IKeyValueService {
    void loadData() throws IOException;
    void saveData();
    List<Row> getData();
    void createFileFromResource();
    SearchResult searchPatt(String target);
    void editKeyOrValue(int rowIndex, int colIndex, boolean isKey, String newValue, String[] newValueForBoth);
    void addRow(int rowIndex, int numCells);
    void sortRow(int rowIndex, String sortOrder);
    void resetData(int rows, int cols);
    void print2DStructure();
}