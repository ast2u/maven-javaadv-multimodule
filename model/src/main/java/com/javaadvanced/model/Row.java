package com.javaadvanced.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Row {
    private List<KeyValuePair> cells = new ArrayList<>();


    public void addCell(KeyValuePair cell) {
        cells.add(cell);
    }

    public void sortIndexRow(String sortOrder) {
        Comparator<KeyValuePair> comparator = Comparator.comparing(KeyValuePair::getConcatString);
        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }
        cells = cells.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
    }
}
