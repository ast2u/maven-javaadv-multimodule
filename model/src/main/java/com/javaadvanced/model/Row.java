package com.javaadvanced.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class Row {
    private List<KeyValuePair> cells;

    public Row() {
        this.cells = new ArrayList<>();
    }

    public List<KeyValuePair> getCells() {
        return cells;
    }

    public void addCell(KeyValuePair cell) {
        cells.add(cell);
    }

    public void sortIndexRow(String sortOrder) {
        Comparator<KeyValuePair> comparator = Comparator.comparing(KeyValuePair::getConcatString);
        if (sortOrder.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }
        cells = cells.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
    }
}